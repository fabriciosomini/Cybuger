package com.cynerds.cyburger.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.cynerds.cyburger.BuildConfig;
import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.LoginActivity;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.cynerds.cyburger.helpers.LogHelper;
import com.facebook.CallbackManager;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

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

        signInBtn.setFocusableInTouchMode(true);


       if(BuildConfig.DEBUG){
           signInUserTxt.setText("admin@cynerds.com");
           signInPasswordTxt.setText("123456");
       }

        signInRememberCbx.setChecked(isRememberMeChecked);
        String storedEmail;
        String storedPwd;
        try {
            boolean anyAccounts = false;//Existem contas salvas
            if (anyAccounts) {
                storedEmail = "";//pega email do registro
                storedPwd = "";//pega senha do registro

                signInUserTxt.setText(storedEmail);
                signInPasswordTxt.setText(storedPwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        signInBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                    if (FieldValidationHelper.isEditTextValidated(signInUserTxt) &&
                            FieldValidationHelper.isEditTextValidated(signInPasswordTxt)) {

                        signInBtn.setEnabled(false);
                        currentActivity.displayProgressBar(true);

                        String email = String.valueOf(signInUserTxt.getText().toString());
                        String password = String.valueOf(signInPasswordTxt.getText().toString());

                        authenticationHelper.setOnSignInListener(new AuthenticationHelper.OnSignInListener() {
                            @Override
                            public void onSuccess() {


                                signInPasswordTxt.setError(null);
                                preferences.setPreferenceValue(rememberMePref, String.valueOf(isRememberMeChecked));


                                ActivityManager.startActivityKillingThis(currentActivity, MainActivity.class);
                                authenticationHelper.removeOnSignInListener();

                            }

                            @Override
                            public void onError(Exception exception) {

                                signInBtn.setEnabled(true);
                                currentActivity.displayProgressBar(false);

                                if (exception != null) {
                                    if (exception.getClass() == FirebaseAuthInvalidUserException.class) {

                                        signInUserTxt.setError(getString(R.string.login_label_incorrectPassword));

                                    } else if (exception.getClass() == FirebaseNetworkException.class) {

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


    }
}
