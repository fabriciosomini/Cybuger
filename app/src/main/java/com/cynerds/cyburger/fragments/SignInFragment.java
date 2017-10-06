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
import com.cynerds.cyburger.activities.LoginActivity;

import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    public static boolean isRememberMeChecked;
    EditText signInUserTxt;
    EditText signInPasswordTxt;
    Button signInBtn;
    CheckBox signInRememberCbx;
    private Preferences preferences;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String rememberMePref;
    private Permissions permissions;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View inflatedView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        inflatedView.setFocusableInTouchMode(true);
        mAuth = FirebaseAuth.getInstance();
        preferences = new Preferences(getActivity());
        permissions = new Permissions(getActivity());
        signInUserTxt = (EditText) inflatedView.findViewById(R.id.signUserInTxt);
        signInPasswordTxt = (EditText) inflatedView.findViewById(R.id.signInPasswordTxt);
        signInBtn = (Button) inflatedView.findViewById(R.id.signInBtn);
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


        signInUserTxt.setText("farofa@test.com");
        signInPasswordTxt.setText("123_@123");

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
                    signIn(email, password);

                }


            }
        });


        return inflatedView;
    }

    private void signIn(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        boolean isSuccessful = task.isSuccessful();
                        if (isSuccessful) {


                            signInPasswordTxt.setError(null);


                            doLogin();

                        } else {

                            signInBtn.setEnabled(true);


                            Exception exception = task.getException();
                            if (exception != null && exception.getClass() == FirebaseAuthInvalidUserException.class) {

                                signInPasswordTxt.setError(getString(R.string.login_label_incorrectPassword));

                            } else if (exception != null && exception.getClass() == FirebaseNetworkException.class) {

                                if (mAuth.getCurrentUser() != null) {
                                    doLogin();
                                }

                            }


                        }

                    }
                });
    }

    private void doLogin() {

        preferences.setPreferenceValue(rememberMePref, String.valueOf(isRememberMeChecked));

        ActivityManager.startActivityKillingThis(getActivity(), MainActivity.class);
        getActivity().finish();

    }

}
