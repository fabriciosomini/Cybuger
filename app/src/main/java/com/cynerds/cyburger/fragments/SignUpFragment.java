package com.cynerds.cyburger.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.LoginActivity;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;


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

    public SignUpFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        authenticationHelper = new AuthenticationHelper(getActivity());

        setUIEvents(inflatedView);

        this.currentActivity = (LoginActivity) getActivity();

        return inflatedView;
    }

    private void setUIEvents(View inflatedView) {
        signUpDisplayNameTxt = inflatedView.findViewById(R.id.signUpDisplayNameTxt);
        signUpUserTxt = inflatedView.findViewById(R.id.signUpUserTxt);
        signUpPasswordTxt = inflatedView.findViewById(R.id.signUpPassword);
        signUpConfirmPasswordTxt = inflatedView.findViewById(R.id.signUpConfirmPasswordTxt);
        signUpBtn = inflatedView.findViewById(R.id.signInBtn);

        signUpDisplayNameTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        signUpDisplayNameTxt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        signUpUserTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        signUpUserTxt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);




        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String displayName = signUpDisplayNameTxt.getText().toString().trim();
                String email = signUpUserTxt.getText().toString().trim();
                String password = signUpPasswordTxt.getText().toString().trim();
                String confirmPassword = signUpConfirmPasswordTxt.getText().toString().trim();

                boolean isFilledOut = true;

                if (displayName.isEmpty()) {

                    signUpDisplayNameTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }

                if (email.isEmpty()) {

                    signUpUserTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }

                if (password.isEmpty()) {

                    signUpPasswordTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }

                if (confirmPassword.isEmpty()) {

                    signUpConfirmPasswordTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }
                if (isFilledOut) {

                    if (password.equals(confirmPassword)) {

                        signUpPasswordTxt.setError(null);
                        signUpConfirmPasswordTxt.setError(null);
                        createUser(displayName, email, password);
                    } else {

                        signUpPasswordTxt.setError(getString(R.string.general_unmatching_password));
                        signUpConfirmPasswordTxt.setError(getString(R.string.general_unmatching_password));
                    }

                }


            }
        });
    }

    private void createUser(final String displayName, final String email, final String password) {


        signUpBtn.setEnabled(false);
        currentActivity.displayProgressBar(true);

        Task<AuthResult> createNewUser = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);

        createNewUser.addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {



                    authenticationHelper.createProfile(task.getResult().getUser());
                    authenticationHelper.updateDisplayName(displayName);

                    authenticationHelper.setOnSignInListener(new AuthenticationHelper.OnSignInListener() {
                        @Override
                        public void onSuccess() {


                            ActivityManager.startActivityKillingThis(getActivity(), MainActivity.class);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Exception exception) {
                            currentActivity.displayProgressBar(false);
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    authenticationHelper.signIn(email, password);

                } else {

                    signUpBtn.setEnabled(true);
                    currentActivity.displayProgressBar(false);

                    FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                    String errorCode = exception.getErrorCode();
                    if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                        signUpUserTxt.setError(getString(R.string.login_error_invalid_email));

                    } else if (errorCode.equals("ERROR_WEAK_PASSWORD")) {

                        signUpPasswordTxt.setError(getString(R.string.login_error_weakPassword));
                        signUpConfirmPasswordTxt.setError(getString(R.string.login_error_weakPassword));
                    } else if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {

                        signUpUserTxt.setError(getString(R.string.login_error_email_already_taken));
                    }
                    Toast.makeText(getActivity(), "Algo deu errado ao criar o usuário", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
