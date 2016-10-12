package com.hasgeek.funnel.space.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Author: @karthikb351
 * Project: zalebi
 */
public class ScannerFragment extends DialogFragment implements ZBarScannerView.ResultHandler {
    public ZBarScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        ArrayList<BarcodeFormat> list = new ArrayList<>();
        list.add(BarcodeFormat.QRCODE);
        mScannerView.setFormats(list);
        getDialog().setTitle("Scan Participant Badge");
        mScannerView.setResultHandler(this);
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void handleResult(Result rawResult) {
        final String data = rawResult.getContents();
        Log.i("handleResult()", "Raw Data:"+data);
        mScannerView.stopCameraPreview();

        new AlertDialog.Builder(getActivity())
                .setTitle("Checking attendee?")
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        boolean success = CheckinService.addCheckinParticipant(data, event);
//                        if (!success)
                            Toast.makeText(getActivity(), "Scanned this: "+data,
                                    Toast.LENGTH_LONG).show();
                        mScannerView.resumeCameraPreview(ScannerFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScannerView.resumeCameraPreview(ScannerFragment.this);
                    }
                })
                .create().show();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCameraPreview();
    }
}
