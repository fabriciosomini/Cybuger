package com.cynerds.cyburger.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.LoginActivity;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.dao.UserAccountDAO;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.NetworkHelper;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.interfaces.OnSignInListener;
import com.cynerds.cyburger.models.account.UserAccount;
import com.facebook.CallbackManager;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private final static int RC_LOAD = 50;
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
    private UserAccountDAO userAccountDAO;
    private int touchCount;
    private URL url;


    public SignInFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View inflatedView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        currentActivity = (LoginActivity) getActivity();
        currentActivity.signInFragment = this;
        userAccountDAO = new UserAccountDAO(currentActivity);

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
                LogHelper.log(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                LogHelper.log(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException log) {
                LogHelper.log(TAG, "facebook:onError", log);
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


    }


    private UserAccount loadCredentials() {

        List<UserAccount> userAccountList = userAccountDAO.SelectAll();
        UserAccount userAccount = null;

        if (userAccountList.size() > 0) {
            userAccount = userAccountList.get(0);
        }

        return userAccount;
    }

    private void fillCredentials(UserAccount userAccount) {


        String currentEmail = String.valueOf(signInUserTxt.getText().toString());
        String currentPassword = String.valueOf(signInPasswordTxt.getText().toString());
        String email = userAccount.getEmail();
        String password = userAccount.getPassword();

        if (currentEmail.isEmpty() && currentPassword.isEmpty()) {

            signInUserTxt.setText(email);
            signInPasswordTxt.setText(password);

            if (CyburgerApplication.autoLogin) {
                performSignIn();
            }

        }


    }

    private void storeCredentials(String email, String password) {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(email);
        userAccount.setPassword(password);
        CyburgerApplication.setUserAccount(userAccount);

        userAccountDAO.InsertUnique(email, password);

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
        secretCode();

        if(!NetworkHelper.isNetworkAvailable(currentActivity))
        {
            signInUserTxt.setKeyListener(null);
            signInPasswordTxt.setKeyListener(null);

            signInUserTxt.setError("Você está offline");

        }else{
            signInUserTxt.setError(null);
            signInUserTxt.setFocusable(true);
            signInPasswordTxt.setFocusable(true);
        }


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    LogHelper.log("Sessão expirada. Por favor, faça login novamente.");
                    DialogManager sessionExpiredDialogManager = new DialogManager(currentActivity,
                            DialogManager.DialogType.OK);


                    sessionExpiredDialogManager.showDialog("Sessão expirada",
                            "Sua sessão expirou, você precisa fazer login outra vez");
                }

            }

        };


        signInBtn.setFocusableInTouchMode(true);


        /*if (BuildConfig.DEBUG) {
            signInUserTxt.setText("admin@cynerds.com");
            signInPasswordTxt.setText("123456");
        }*/


        signInBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    performSignIn();
                    signInBtn.clearFocus();

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

        if (isRememberMeChecked) {
            UserAccount userAccount = loadCredentials();

            if (userAccount != null) {
                CyburgerApplication.setUserAccount(userAccount);
                fillCredentials(userAccount);
            }

        }

        signInRememberCbx.setChecked(isRememberMeChecked);



    }

    private void secretCode() {

        touchCount = 0;
        ImageView appLogo = currentActivity.findViewById(R.id.appLogo);
        final GifTextView eg = currentActivity.findViewById(R.id.eg);
        appLogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (NetworkHelper.isNetworkAvailable(currentActivity)) {
                    if (touchCount == 6) {
                        touchCount = 0;
                        url = null;
                        try {
                            url = new URL("http://i.imgur.com/la8CnEB.gif");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        AsyncTask asyncTask = new AsyncTask<Object, Void, GifDrawable>() {


                            @Override
                            protected GifDrawable doInBackground(Object... objects) {

                                InputStream sourceIs = null;
                                try {
                                    sourceIs = url.openConnection().getInputStream();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                BufferedInputStream bis = new BufferedInputStream(sourceIs, 8192);

                                try {
                                    return new GifDrawable(bis);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return null;

                            }

                            @Override
                            protected void onPostExecute(GifDrawable gifFromStream) {
                                super.onPostExecute(gifFromStream);

                                eg.setBackground(gifFromStream);
                                eg.setVisibility(View.VISIBLE);
                                eg.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        eg.setVisibility(View.GONE);
                                        return false;
                                    }
                                });
                            }
                        };
                        asyncTask.execute();


                    } else {
                        touchCount++;

                        switch (touchCount) {
                            case 3:
                                Toast.makeText(currentActivity, "Você está quase descrobrindo o segredo"
                                        , Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                return false;
            }
        });
    }

    private void performSignIn() {

        if (FieldValidationHelper.isEditTextValidated(signInUserTxt) &&
                FieldValidationHelper.isEditTextValidated(signInPasswordTxt)) {

            signInBtn.setEnabled(false);
            currentActivity.showBusyLoader(true);
            LogHelper.log("Remove busyloader");

            final String email = String.valueOf(signInUserTxt.getText().toString());
            final String password = String.valueOf(signInPasswordTxt.getText().toString());

            authenticationHelper.setOnSignInListener(new OnSignInListener() {
                @Override
                public void onSuccess() {

                    signInSuccess(email, password);

                }

                @Override
                public void onError(Exception exception) {


                    signInBtn.setEnabled(true);
                    currentActivity.showBusyLoader(false);

                    if (exception != null) {
                        if (exception.getClass() == FirebaseAuthInvalidUserException.class ||
                                exception.getClass() == FirebaseAuthInvalidCredentialsException.class) {

                            signInUserTxt.setError(getString(R.string.login_label_incorrectPassword));

                        } else if (exception.getClass() == FirebaseNetworkException.class) {

                            DialogManager dialogManager = new DialogManager(getContext(), DialogManager.DialogType.OK);
                            dialogManager.showDialog("Verifique sua conexão", getString(R.string.login_error_unsynced));


                        } else {

                            DialogManager dialogManager = new DialogManager(getContext(), DialogManager.DialogType.OK);
                            dialogManager.showDialog("Erro", exception.getMessage());

                        }


                        LogHelper.log(
                                exception.getClass().getSimpleName()
                                        + ": " + exception.getMessage());

                    }

                    authenticationHelper.removeOnSignInListener();
                }

            });
            authenticationHelper.signIn(email, password);

        }
    }

    private void signInSuccess(String email, String password) {
        signInPasswordTxt.setError(null);
        if (isRememberMeChecked) {
            storeCredentials(email, password);
        } else {
            removeCredentials();
        }

        preferences.setPreferenceValue("rememberMe", String.valueOf(isRememberMeChecked));


        ActivityManager.startActivityKillingThis(currentActivity, MainActivity.class);
        authenticationHelper.removeOnSignInListener();
    }

    private void removeCredentials() {
        userAccountDAO.DeleteAll();
    }


}
