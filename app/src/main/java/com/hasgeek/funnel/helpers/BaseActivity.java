package com.hasgeek.funnel.helpers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hasgeek.funnel.data.DatabaseController;

import io.realm.Realm;

public abstract class BaseActivity extends AppCompatActivity {

    public Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseController.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        if (realm.isClosed()) {
            le("This shouldn't be happening");
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (realm.isClosed())
            realm = Realm.getDefaultInstance();
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg+"", Toast.LENGTH_SHORT).show();
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

    public abstract void initViews(Bundle savedInstanceState);

    public abstract void notFoundError();

}
