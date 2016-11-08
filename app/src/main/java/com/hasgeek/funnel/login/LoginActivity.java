package com.hasgeek.funnel.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.hasgeek.funnel.data.APIController;
import com.hasgeek.funnel.data.AuthController;
import com.hasgeek.funnel.helpers.BaseActivity;
import com.hasgeek.funnel.model.wrapper.AuthWrapper;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Author: @karthikb351
 * Project: android
 */

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        // check if this intent is started via custom scheme link
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = Uri.parse("talkfunnel://login?"+intent.getData().getFragment());
            final String access_token = uri.getQueryParameter("access_token");
            String token_type = uri.getQueryParameter("token_type");
            l("URI is "+uri.toString());

            APIController.getService().getAuthVerification(access_token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AuthWrapper>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            loginFailed();
                        }

                        @Override
                        public void onNext(AuthWrapper authWrapper) {
                            if (authWrapper.getCode() == 200)
                                loginSuccessful(access_token);
                            else
                                loginFailed();

                        }
                    });
        }
    }

    void loginSuccessful(String access_token) {
        AuthController.saveAuthToken(access_token);
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod("Browser")
                .putSuccess(true));
        Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
        finish();

    }

    void loginFailed() {
        Toast.makeText(LoginActivity.this, "Oops, something went wrong", Toast.LENGTH_SHORT).show();
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod("Browser")
                .putSuccess(false));
        finish();
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }


    @Override
    public void notFoundError() {

    }

}
