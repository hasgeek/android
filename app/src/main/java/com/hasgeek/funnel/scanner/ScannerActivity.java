package com.hasgeek.funnel.scanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hasgeek.funnel.R;
import com.hasgeek.funnel.data.APIController;
import com.hasgeek.funnel.data.ContactExchangeController;
import com.hasgeek.funnel.data.SpaceController;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.helpers.utils.ContactExchangeUtils;
import com.hasgeek.funnel.model.ContactExchangeContact;
import com.hasgeek.funnel.model.Space;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ScannerActivity extends BaseActivity implements ZBarScannerView.ResultHandler {

    public static final String EXTRA_SPACE_ID = "extra_space_id";

    private ZBarScannerView zBarScannerView;

    Space space_Cold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        Intent intent = getIntent();
        final String spaceId = intent.getStringExtra(EXTRA_SPACE_ID);

        space_Cold = SpaceController.getSpaceById_Cold(getRealm(), spaceId);

        if (space_Cold == null) {
            notFoundError();
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.activity_scanner_scanner);

        zBarScannerView = new ZBarScannerView(this);
        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QRCODE);
        zBarScannerView.setFormats(list);

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

        Dexter.checkPermission(new CompositePermissionListener(permissionListener, snackbarOnDeniedPermissionListener), Manifest.permission.CAMERA);
    }


    void syncContactExchangeContacts() {
        List<ContactExchangeContact> contactExchangeContacts = ContactExchangeController.getUnsyncedContactExchangeContactsBySpaceId_Cold(getRealm(), space_Cold.getId());

        for (ContactExchangeContact c: contactExchangeContacts) {
            APIController.getService().syncContactExchangeContact(c)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ContactExchangeContact>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ContactExchangeContact contactExchangeContact) {
                            contactExchangeContact.setSpace(space_Cold);
                            contactExchangeContact.setSynced(true);
                            ContactExchangeController.updateContactExchangeContact(getRealm(), contactExchangeContact);
                            l("synced");
                        }
                    });
        }
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


        if (ContactExchangeUtils.isValidCode(data)) {
            String puk = ContactExchangeUtils.getPukFromCode(data);
            String key = ContactExchangeUtils.getKeyFromCode(data);

            final ContactExchangeContact contactExchangeContact = ContactExchangeController.getContactExchangeContactFromPukAndKeyAndSpaceId_Hot(realm, puk, key, space_Cold.getId());

            if (contactExchangeContact != null) {
                zBarScannerView.stopCameraPreview();
                new AlertDialog.Builder(ScannerActivity.this)
                        .setTitle("Add "+contactExchangeContact.getFullname()+" ?")
                        .setMessage(""+contactExchangeContact.getJobTitle()+"\n"+contactExchangeContact.getCompany())
                        .setCancelable(false)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContactExchangeController.addContactExchangeContact(getRealm(), contactExchangeContact);
                                syncContactExchangeContacts();
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
            else
                Toast.makeText(ScannerActivity.this, "Not found. Are you sure you are in the right event?",
                        Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(ScannerActivity.this, "Invalid QR Code", Toast.LENGTH_LONG).show();
        }




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
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            token.cancelPermissionRequest();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            token.continuePermissionRequest();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            token.cancelPermissionRequest();
                        }
                    })
                    .show();
        }
    };
}
