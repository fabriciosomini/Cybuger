package com.cynerds.cyburger.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.adapters.SimpleFragmentPagerAdapter;
import com.cynerds.cyburger.components.JBButton;
import com.cynerds.cyburger.components.JBCheckbox;
import com.cynerds.cyburger.components.JBEmail;
import com.cynerds.cyburger.components.JBPassword;
import com.cynerds.cyburger.helpers.Account;
import com.cynerds.cyburger.helpers.AccountManager;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends WorkspaceActivity {
    public static boolean isRememberMeChecked;
    JBEmail signInUserTxt;
    JBPassword signInPasswordTxt;
    JBButton signInBtn;
    JBCheckbox signInRememberCbx;
    private Preferences preferences;
    private AccountManager accountManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Account account;

    private Permissions permissions;


    //rememberMe 5xb64
    private String rememberMePref;

    @Override
    public void onStart() {
        super.onStart();

      //  mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setIgnoreUnsavedChanges(true);

        generateTabs();
      /*  mAuth = FirebaseAuth.getInstance();


        try {

            String aType = "Vmxjd2VHTXlWbGRqUm1oVVlsZG9jVlJYZUdGUk1XUlZVMnM1YTJKV1NsbFViRkpDVUZFOVBRPT0=";
            accountManager = new AccountManager(this, aType);
        } catch (Exception e) {
            e.printStackTrace();
        }


        rememberMePref = "VjFSSmVGWXlVa2RqUm1oT1ZqSjRhRll3Vm5kVU1XUnpVbFJzVVZWVU1Eaz0=";

        account = accountManager.getAccount();

        preferences = new Preferences(this);
        permissions = new Permissions(this);

        isRememberMeChecked = Boolean.parseBoolean(preferences.getPreferenceValue(rememberMePref));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {


                    Toast.makeText(LoginActivity.this, "Sessão expirada. Por favor, faça login novamente.",
                            Toast.LENGTH_SHORT).show();
                }

            }

        };

        signInUserTxt = (JBEmail) findViewById(R.id.signUserInTxt);
        signInPasswordTxt = (JBPassword) findViewById(R.id.signInPasswordTxt);
        signInBtn = (JBButton) findViewById(R.id.signInBtn);
        signInRememberCbx = (JBCheckbox) findViewById(R.id.signInRememberCbx);


        signInUserTxt.getComponent().setText("conta_testes_123456_moneyme@gmail.com");
        signInPasswordTxt.getComponent().setText("senhatests_123456#");



        signInRememberCbx.getCheckbox().setChecked(isRememberMeChecked);
        String storedEmail;
        String storedPwd;
        try {
            boolean noAccountFound = accountManager.getEmail(account).isEmpty() &&
                    accountManager.getPassword(account).isEmpty();
            if (!noAccountFound) {
                storedEmail = accountManager.getEmail(account);
                storedPwd = accountManager.getPassword(account);

                signInUserTxt.getComponent().setText(storedEmail);
                signInPasswordTxt.getComponent().setText(storedPwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        signInRememberCbx.getCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                LoginActivity.isRememberMeChecked = isChecked;
                if (isChecked) {
                    if (!permissions.isPermissionForExternalStorageGranted()) {

                        permissions.requestPermissionForExternalStorage();
                    }

                }
            }
        });


        signInBtn.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    signInBtn.getButton().setEnabled(false);

                    String email = "";
                    String password = "";


                    email = String.valueOf(signInUserTxt.getComponentValue());
                    password = String.valueOf(signInPasswordTxt.getComponentValue());


                    signIn(email, password);
                }

            }
        });
*/

    }

    private void signIn(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        boolean isSuccessful = task.isSuccessful();
                        if (isSuccessful) {


                            signInPasswordTxt.hideValidationMessage();


                            try {
                                boolean noAccountFound = accountManager.getEmail(account).isEmpty() &&
                                        accountManager.getPassword(account).isEmpty();
                                if (isRememberMeChecked) {

                                    accountManager.setAccount(email, password);


                                } else {
                                    if (!noAccountFound) {

                                        accountManager.removeAccount(email, password);

                                    }

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            doLogin();

                        } else {

                            signInBtn.getButton().setEnabled(true);


                            Exception exception = task.getException();
                            if (exception != null && exception.getClass() == FirebaseAuthInvalidUserException.class) {

                                signInPasswordTxt.setValidationMessage(getString(R.string.login_label_incorrectPassword));
                                signInPasswordTxt.showValidationMessage();
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

        ActivityManager.startActivityKillingThis(LoginActivity.this, MainActivity.class);
        finish();

    }

    public void generateTabs(){

        // Set the content of the activity to use the  activity_main.xml layout file
       // setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    }





