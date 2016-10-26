package com.hasgeek.funnel.scanner;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ScannerActivity extends BaseActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView zBarScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.activity_scanner_scanner);
        zBarScannerView = new ZBarScannerView(this);
        contentFrame.addView(zBarScannerView);

        SnackbarOnDeniedPermissionListener snackbarOnDeniedPermissionListener = SnackbarOnDeniedPermissionListener.Builder
                .with(contentFrame, "We need to access your camera")
                .withOpenSettingsButton("Grant Access")
                .withCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        finish();
                    }
                })
                .build();

        Dexter.checkPermission(new CompositePermissionListener(permissionListener, snackbarOnDeniedPermissionListener),  Manifest.permission.CAMERA);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        zBarScannerView.setResultHandler(this);
        zBarScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zBarScannerView.stopCamera();
    }

    @Override
    public void notFoundError() {

    }

    @Override
    public void handleResult(Result result) {

        final String data = result.getContents();
        l("Raw Data:"+data);
        zBarScannerView.stopCameraPreview();

        new AlertDialog.Builder(ScannerActivity.this)
                .setTitle("Checking attendee?")
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        boolean success = CheckinService.addCheckinParticipant(data, event);
//                        if (!success)
                        Toast.makeText(ScannerActivity.this, "Scanned this: "+data,
                                Toast.LENGTH_LONG).show();
                        zBarScannerView.resumeCameraPreview(ScannerActivity.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zBarScannerView.resumeCameraPreview(ScannerActivity.this);
                    }
                })
                .create().show();


    }

    PermissionListener permissionListener = new PermissionListener() {

        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {

        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {

        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
            new AlertDialog.Builder(ScannerActivity.this).setTitle("Boy is it dark in here...")
                    .setMessage("We need to access to your camera to scan badges")
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            token.cancelPermissionRequest();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            token.continuePermissionRequest();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override public void onDismiss(DialogInterface dialog) {
                            token.cancelPermissionRequest();
                        }
                    })
                    .show();
        }
    };
}
