package com.cynerds.cyburger.fragments;


import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cynerds.cyburger.BuildConfig;
import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.LoginActivity;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private EditText signUpUserTxt;
    private EditText signUpDisplayNameTxt;
    private EditText signUpPasswordTxt;
    private EditText signUpConfirmPasswordTxt;
    private Button signUpBtn;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private AuthenticationHelper authenticationHelper;
    private LoginActivity currentActivity;
    private GoogleApiClient mCredentialsApiClient;
    final static int RC_SAVE = 100;

    public SignUpFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        currentActivity = (LoginActivity) getActivity();
        currentActivity.signUpFragment = this;

        authenticationHelper = new AuthenticationHelper(currentActivity);

        setUIEvents(inflatedView);


        return inflatedView;
    }

    private void setUIEvents(View inflatedView) {
        signUpDisplayNameTxt = inflatedView.findViewById(R.id.signUpDisplayNameTxt);
        signUpUserTxt = inflatedView.findViewById(R.id.signUpUserTxt);
        signUpPasswordTxt = inflatedView.findViewById(R.id.signUpPassword);
        signUpConfirmPasswordTxt = inflatedView.findViewById(R.id.signUpConfirmPasswordTxt);
        signUpBtn = inflatedView.findViewById(R.id.signInBtn);

        signUpBtn.setFocusableInTouchMode(true);
        signUpDisplayNameTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        signUpDisplayNameTxt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        signUpUserTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        signUpUserTxt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //test
        if (BuildConfig.DEBUG) {
            test_fill();
        }


        signUpBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    String displayName = signUpDisplayNameTxt.getText().toString().trim();
                    String email = signUpUserTxt.getText().toString().trim();
                    String password = signUpPasswordTxt.getText().toString().trim();
                    String confirmPassword = signUpConfirmPasswordTxt.getText().toString().trim();


                    if (FieldValidationHelper.isEditTextValidated(signUpDisplayNameTxt) &&
                            FieldValidationHelper.isEditTextValidated(signUpUserTxt) &&
                            FieldValidationHelper.isEditTextValidated(signUpPasswordTxt) &&
                            FieldValidationHelper.isEditTextValidated(signUpConfirmPasswordTxt)) {

                        if (password.equals(confirmPassword)) {

                            FieldValidationHelper.setFieldAsValid(signUpConfirmPasswordTxt);
                            FieldValidationHelper.setFieldAsValid(signUpConfirmPasswordTxt);

                            createUser(displayName, email, password);
                        } else {

                            FieldValidationHelper.setFieldAsInvalid(signUpPasswordTxt, R.string.general_unmatching_password);
                            FieldValidationHelper.setFieldAsInvalid(signUpConfirmPasswordTxt, R.string.general_unmatching_password);
                        }

                    }

                }
            }
        });

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
    }

    private void test_fill() {
        signUpDisplayNameTxt.setText(String.valueOf(new Random().nextInt()));
        signUpUserTxt.setText(new Random().nextInt() + "@test.com");
        signUpPasswordTxt.setText("123456");
        signUpConfirmPasswordTxt.setText("123456");

    }

    private void createUser(final String displayName, final String email, final String password) {


        LogHelper.error("Trying to create a new user using email and password");
        signUpBtn.setEnabled(false);
        currentActivity.displayProgressBar(true);

        Task<AuthResult> createNewUser = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);

        createNewUser.addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    LogHelper.error("New user created successfully");

                    authenticationHelper.createProfile(task.getResult().getUser());
                    authenticationHelper.updateDisplayName(displayName);

                    authenticationHelper.setOnSignInListener(new AuthenticationHelper.OnSignInListener() {
                        @Override
                        public void onSuccess() {

                            storeCredentials(email, password);


                        }

                        @Override
                        public void onError(Exception exception) {
                            currentActivity.displayProgressBar(false);
                            LogHelper.error(exception.getMessage());
                        }
                    });

                    authenticationHelper.signIn(email, password);

                } else {

                    LogHelper.error("Failed to create user");

                    signUpBtn.setEnabled(true);
                    currentActivity.displayProgressBar(false);

                    if (task.getException() instanceof FirebaseAuthException) {
                        FirebaseAuthException authException = (FirebaseAuthException) task.getException();
                        if (authException != null) {
                            String errorCode = authException.getErrorCode();

                            if (errorCode.equals("ERROR_INVALID_EMAIL")) {

                                FieldValidationHelper.setFieldAsInvalid(signUpUserTxt, R.string.login_error_invalid_email);

                            } else if (errorCode.equals("ERROR_WEAK_PASSWORD")) {

                                FieldValidationHelper.setFieldAsInvalid(signUpPasswordTxt, R.string.login_error_weakPassword);
                                FieldValidationHelper.setFieldAsInvalid(signUpConfirmPasswordTxt, R.string.login_error_weakPassword);

                            } else if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {

                                FieldValidationHelper.setFieldAsInvalid(signUpUserTxt, R.string.login_error_email_already_taken);
                            } else {

                                LogHelper.error(
                                        authException.getClass().getSimpleName()
                                                + ": " + authException.getMessage());
                            }
                        }
                    }

                    if (task.getException() instanceof FirebaseNetworkException) {

                        DialogManager dialogManager = new DialogManager(getContext(), DialogManager.DialogType.OK);
                        dialogManager.showDialog("Verifique sua conexão", getString(R.string.login_error_no_connection));

                    }


                    LogHelper.error("Algo deu errado ao criar o usuário");
                }
            }
        });

    }

    private void signInSuccess() {
        ActivityManager.startActivityKillingThis(currentActivity, MainActivity.class);
        authenticationHelper.removeOnSignInListener();
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
                            signInSuccess();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            if (requestCode == RC_SAVE) {
                if (resultCode == RESULT_OK) {
                    LogHelper.error("Credentials SignUp RC_SAVE");
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    CyburgerApplication.setCredential(credential);
                    CyburgerApplication.setCredential(credential);

                } else {
                    LogHelper.error("User cancelled saving credentials");
                }
            }
        }

        signInSuccess();

    }

}
