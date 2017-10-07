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
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    public static boolean isRememberMeChecked;
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

        authenticationHelper = new AuthenticationHelper(getActivity());
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
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
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
        preferences = new Preferences(getActivity());
        permissions = new Permissions(getActivity());
        signInUserTxt = (EditText) inflatedView.findViewById(R.id.signUserInTxt);
        signInPasswordTxt = (EditText) inflatedView.findViewById(R.id.signInPasswordTxt);
        signInBtn = (Button) inflatedView.findViewById(R.id.signInBtn);
        signInFacebookBtn = (Button) inflatedView.findViewById(R.id.signInFacebookBtn);
        signInRememberCbx = (CheckBox) inflatedView.findViewById(R.id.signInRememberCbx);

        rememberMePref = "rememberMe";


        isRememberMeChecked = Boolean.parseBoolean(preferences.getPreferenceValue(rememberMePref));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {


                    Toast.makeText(getActivity(), "Sessão expirada. Por favor, faça login novamente.",
                            Toast.LENGTH_SHORT).show();
                }

            }

        };


        signInUserTxt.setText("fa@test.com");
        signInPasswordTxt.setText("123456");

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

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInBtn.setEnabled(false);

                String email = "";
                String password = "";


                email = String.valueOf(signInUserTxt.getText().toString());
                password = String.valueOf(signInPasswordTxt.getText().toString());


                boolean isFilledOut = true;
                if (signInUserTxt.getText().toString().trim().equals("")) {

                    signInUserTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }

                if (signInPasswordTxt.getText().toString().trim().equals("")) {

                    signInPasswordTxt.setError(getString(R.string.general_label_requiredfield));

                    isFilledOut = false;
                }


                if (isFilledOut) {

                    authenticationHelper.setOnSignInListener(new AuthenticationHelper.OnSignInListener() {
                        @Override
                        public void onSuccess() {
                            signInPasswordTxt.setError(null);
                            preferences.setPreferenceValue(rememberMePref, String.valueOf(isRememberMeChecked));

                            ActivityManager.startActivityKillingThis(getActivity(), MainActivity.class);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Exception exception) {
                            signInBtn.setEnabled(true);

                            if (exception != null && exception.getClass() == FirebaseAuthInvalidUserException.class) {

                                signInUserTxt.setError(getString(R.string.login_label_incorrectPassword));

                            }
                        }
                    });
                    authenticationHelper.signIn(email, password);

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
