package com.cynerds.cyburger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    boolean isFilledOut = true;
    EditText signUpUserTxt;
    EditText signUpPasswordTxt;
    EditText signUpConfirmPasswordTxt;
    Button signUpBtn;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        setUIEvents(inflatedView);

        return inflatedView;
    }

    private void setUIEvents(View inflatedView) {
        inflatedView.setFocusableInTouchMode(true);

        signUpUserTxt = (EditText) inflatedView.findViewById(R.id.signUpUserTxt);
        signUpPasswordTxt = (EditText) inflatedView.findViewById(R.id.signUpPassword);
        signUpConfirmPasswordTxt = (EditText) inflatedView.findViewById(R.id.signUpConfirmPasswordTxt);
        signUpBtn = (Button) inflatedView.findViewById(R.id.signInBtn);

        final String email = signUpUserTxt.getText().toString().trim();
        final String password = signUpPasswordTxt.getText().toString().trim();


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (signUpUserTxt.getText().toString().trim().equals("")) {

                    signUpUserTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }

                if (signUpPasswordTxt.getText().toString().trim().equals("")) {

                    signUpPasswordTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }

                if (signUpConfirmPasswordTxt.getText().toString().trim().equals("")) {

                    signUpConfirmPasswordTxt.setError(getString(R.string.general_label_requiredfield));
                    isFilledOut = false;
                }
                if (isFilledOut) {
                    //Cadastra usuário
                    createUser(email, password);
                }


            }
        });
    }

    private void createUser(String email, String password) {
        //Criar usuário no fibase
    }

}
