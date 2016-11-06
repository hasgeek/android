package com.hasgeek.funnel.helpers;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Author: @karthikb351
 * Project: android
 */

public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
        setRetainInstance(true);
    }

    public void toast(String msg) {
        Toast.makeText(getActivity(), msg+"", Toast.LENGTH_SHORT).show();
    }

    public void l(String msg) {
        Log.d(this.getClass().getSimpleName(), msg+" ");
    }

    public void le(String msg) {
        Log.e(this.getClass().getSimpleName(), msg+" ");
    }

    public void li(String msg) {
        Log.i(this.getClass().getSimpleName(), msg+" ");
    }

    public abstract void notFoundError();

}
