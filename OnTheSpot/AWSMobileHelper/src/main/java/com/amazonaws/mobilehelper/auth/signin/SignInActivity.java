package com.amazonaws.mobilehelper.auth.signin;
//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.16
//

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobilehelper.auth.IdentityProviderType;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;

import com.amazonaws.mobilehelper.R;
import com.amazonaws.mobilehelper.auth.SignInResultHandler;

/**
 * Activity for handling Sign-in with an Identity Provider.
 */
public class SignInActivity extends Activity {
    private static final String LOG_TAG = SignInActivity.class.getSimpleName();
    private SignInManager signInManager;

    /**
     * The Google OnClick listener, since we must override it to get permissions on Marshmallow and above.
     */
    private View.OnClickListener googleOnClickListener;

    /**
     * SignInProviderResultHandlerImpl handles the final result from sign in.
     */
    private class SignInProviderResultHandlerImpl implements SignInProviderResultHandler {
        /**
         * Receives the successful sign-in result and starts the main activity.
         *
         * @param provider the identity provider used for sign-in.
         */
        @Override
        public void onSuccess(final IdentityProvider provider) {
            Log.i(LOG_TAG, String.format("Sign-in with %s succeeded.", provider.getDisplayName()));

            // The sign-in manager is no longer needed once signed in.
            SignInManager.dispose();

            final IdentityManager identityManager = signInManager.getIdentityManager();
            final SignInResultHandler signInResultsHandler = signInManager.getResultHandler();

            // Load user name and image.
            identityManager.loadUserIdentityProfile(provider, new Runnable() {
                @Override
                public void run() {
                    // Call back the results handler.
                    signInResultsHandler.onSuccess(SignInActivity.this, provider);
                }
            });
            finish();
        }

        /**
         * Receives the sign-in result indicating the user canceled and shows a toast.
         *
         * @param provider the identity provider with which the user attempted sign-in.
         */
        @Override
        public void onCancel(final IdentityProvider provider) {
            Log.i(LOG_TAG, String.format("Sign-in with %s canceled.", provider.getDisplayName()));
            signInManager.getResultHandler()
                .onIntermediateProviderCancel(SignInActivity.this, provider);
        }

        /**
         * Receives the sign-in result that an error occurred signing in and shows a toast.
         *
         * @param provider the identity provider with which the user attempted sign-in.
         * @param ex       the exception that occurred.
         */
        @Override
        public void onError(final IdentityProvider provider, final Exception ex) {
            Log.e(LOG_TAG, String.format("Sign-in with %s caused an error.", provider.getDisplayName()), ex);
            signInManager.getResultHandler()
                .onIntermediateProviderError(SignInActivity.this, provider, ex);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInManager = SignInManager.getInstance();

        signInManager.setProviderResultsHandler(this, new SignInProviderResultHandlerImpl());

        // Initialize sign-in buttons.
        signInManager.initializeSignInButton(IdentityProviderType.FACEBOOK,
            this.findViewById(R.id.fb_login_button));

        signInManager.initializeSignInButton(IdentityProviderType.COGNITO_USER_POOL,
            this.findViewById(R.id.signIn_imageButton_login));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signInManager.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (signInManager.getResultHandler().onCancel(this)) {
            super.onBackPressed();
            // Since we are leaving sign-in via back, we can dispose the sign-in manager, since sign-in was cancelled.
            SignInManager.dispose();
        }
    }
}
