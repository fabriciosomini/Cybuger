package com.cynerds.cyburger.fragments;


import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.cynerds.cyburger.BuildConfig;
import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.LoginActivity;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.cynerds.cyburger.helpers.LogHelper;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private final static  int RC_LOAD = 50;
    private final static int RC_SAVE = 100;
    public static boolean isRememberMeChecked;
    private LoginActivity currentActivity;
    private EditText signInUserTxt;
    private EditText signInPasswordTxt;
    private Button signInBtn;
    private Button signInFacebookBtn;

    private CheckBox signInRememberCbx;
    private Preferences preferences;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String rememberMePref;
    private Permissions permissions;
    private CallbackManager mCallbackManager;
    private AuthenticationHelper authenticationHelper;
    private GoogleApiClient mCredentialsApiClient;
    private CredentialRequestResult credentialRequestResultGlobal;


    public SignInFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View inflatedView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        this.currentActivity = (LoginActivity) getActivity();

        authenticationHelper = new AuthenticationHelper(currentActivity);
        setUIEvents(inflatedView);


        return inflatedView;
    }

    private void doLoginFacebook() {
        // Initialize Facebook Login button
       /* mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) getActivity().findViewById(R.id.signInFacebookBtn);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogHelper.error(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                LogHelper.error(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                LogHelper.error(TAG, "facebook:onError", error);
                // ...
            }
        });*/
    }


    // @Override
   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }*/




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                LogHelper.error("Credentials saved");
            } else {
                LogHelper.error("User cancelled saving credentials");
            }
        }

        if (requestCode == RC_LOAD) {
            if (resultCode == RESULT_OK) {
                LogHelper.error("Credentials loaded");
                fillCredentials();
            } else {
                LogHelper.error("User cancelled loading credentials");
            }
        }


    }



    private void loadCredentials() {

        CredentialRequest mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                //.setAccountTypes(IdentityProviders.GOOGLE)
                .build();

        Auth.CredentialsApi.request(mCredentialsApiClient, mCredentialRequest).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        Status status = credentialRequestResult.getStatus();
                        if (status.isSuccess()) {

                            credentialRequestResultGlobal = credentialRequestResult;
                            fillCredentials();

                        } else {

                            if (status.hasResolution()) {
                                // Try to resolve the save request. This will prompt the user if
                                // the credential is new.
                                try {
                                    credentialRequestResultGlobal = credentialRequestResult;
                                    status.startResolutionForResult(currentActivity, RC_LOAD);

                                } catch (IntentSender.SendIntentException e) {
                                    // Could not resolve the request
                                    LogHelper.error("Failed to load credential - Could not resolve the request: ["
                                            + status.getStatusCode() + "]"
                                            + status.getStatusMessage());

                                }
                            } else {
                                // Request has no resolution
                                LogHelper.error("Failed to load credential - Request has no resolution: ["
                                        + status.getStatusCode() + "]"
                                        + status.getStatusMessage());
                            }

                        }
                    }
                });
    }

    private void fillCredentials() {

        if(credentialRequestResultGlobal!=null){

            Credential credential = credentialRequestResultGlobal.getCredential();

            String currentEmail = String.valueOf(signInUserTxt.getText().toString());
            String currentPassword = String.valueOf(signInPasswordTxt.getText().toString());
            String email = credential.getId();
            if(currentEmail.isEmpty() && currentPassword.isEmpty()){
                signInUserTxt.setText(email);
                signInPasswordTxt.setText(credential.getPassword());

                if(CyburgerApplication.autoLogin){
                    performSignIn();
                }
            }
        }

    }

    private void storeCredentials(String email, String password) {


        Credential credential = new Credential.Builder(email)
                .setPassword(password)
                .build();

        Auth.CredentialsApi.save(mCredentialsApiClient, credential).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {

                            LogHelper.error("Credential Saved");
                            //hideProgress();
                        } else {

                            if (status.hasResolution()) {
                                // Try to resolve the save request. This will prompt the user if
                                // the credential is new.
                                try {
                                    status.startResolutionForResult(currentActivity, RC_SAVE);
                                } catch (IntentSender.SendIntentException e) {
                                    // Could not resolve the request
                                    LogHelper.error("Failed to save credential - Could not resolve the request: ["
                                            + status.getStatusCode() + "]"
                                            + status.getStatusMessage());

                                }
                            } else {
                                // Request has no resolution
                                LogHelper.error("Failed to save credential - Request has no resolution: ["
                                        + status.getStatusCode() + "]"
                                        + status.getStatusMessage());
                            }

                        }
                    }
                });


    }


    public void setUIEvents(View inflatedView) {


        mAuth = FirebaseAuth.getInstance();
        preferences = new Preferences(currentActivity);
        permissions = new Permissions(currentActivity);
        signInUserTxt = inflatedView.findViewById(R.id.signUserInTxt);
        signInPasswordTxt = inflatedView.findViewById(R.id.signInPasswordTxt);
        signInBtn = inflatedView.findViewById(R.id.signInBtn);
        signInFacebookBtn = inflatedView.findViewById(R.id.signInFacebookBtn);
        signInRememberCbx = inflatedView.findViewById(R.id.signInRememberCbx);
        rememberMePref = "rememberMe";
        isRememberMeChecked = Boolean.parseBoolean(preferences.getPreferenceValue(rememberMePref));
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    LogHelper.error("Sessão expirada. Por favor, faça login novamente.");
                    DialogManager sessionExpiredDialogManager = new DialogManager(currentActivity,
                            DialogManager.DialogType.OK);


                    sessionExpiredDialogManager.showDialog("Sessão expirada",
                            "Sua sessão expirou, você precisa fazer login outra vez");
                }

            }

        };

        mCredentialsApiClient = new GoogleApiClient.Builder(currentActivity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.CREDENTIALS_API)
                .build();

        mCredentialsApiClient.connect();

        signInBtn.setFocusableInTouchMode(true);


      /*  if (BuildConfig.DEBUG) {
            signInUserTxt.setText("admin@cynerds.com");
            signInPasswordTxt.setText("123456");
        }*/


        signInBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    performSignIn();

                }
            }
        });

        signInFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLoginFacebook();
            }
        });

        signInRememberCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SignInFragment.isRememberMeChecked = isChecked;
                if (isChecked) {
                    if (!permissions.isPermissionForExternalStorageGranted()) {

                        permissions.requestPermissionForExternalStorage();
                    }

                }
            }
        });

        loadCredentials();



    }

    private void performSignIn() {

        if (FieldValidationHelper.isEditTextValidated(signInUserTxt) &&
                FieldValidationHelper.isEditTextValidated(signInPasswordTxt)) {

            signInBtn.setEnabled(false);
            currentActivity.displayProgressBar(true);

            final String email = String.valueOf(signInUserTxt.getText().toString());
            final String password = String.valueOf(signInPasswordTxt.getText().toString());

            authenticationHelper.setOnSignInListener(new AuthenticationHelper.OnSignInListener() {
                @Override
                public void onSuccess() {


                   signInSuccess(email, password);

                }

                @Override
                public void onError(Exception exception) {

                    signInBtn.setEnabled(true);
                    currentActivity.displayProgressBar(false);

                    if (exception != null) {
                        if (exception.getClass() == FirebaseAuthInvalidUserException.class) {

                            signInUserTxt.setError(getString(R.string.login_label_incorrectPassword));

                        } else if (exception.getClass() == FirebaseNetworkException.class) {

                           // signInSuccess(email, password);

                           DialogManager dialogManager = new DialogManager(getContext(), DialogManager.DialogType.OK);
                            dialogManager.showDialog("Verifique sua conexão", getString(R.string.login_error_no_connection));

                        } else {

                            LogHelper.error(
                                    exception.getClass().getSimpleName()
                                            + ": " + exception.getMessage());
                        }
                    }
                }

            });
            authenticationHelper.signIn(email, password);

        }
    }

    private void signInSuccess(String email, String  password) {
        signInPasswordTxt.setError(null);
        storeCredentials(email, password);

        ActivityManager.startActivityKillingThis(currentActivity, MainActivity.class);
        authenticationHelper.removeOnSignInListener();
    }
}
