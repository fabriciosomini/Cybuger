package com.cynerds.cyburger.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.adapters.SimpleFragmentPagerAdapter;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends BaseActivity {
    public static boolean isRememberMeChecked;
    EditText signInUserTxt;
    EditText signInPasswordTxt;
    Button signInBtn;
    CheckBox signInRememberCbx;
    private Preferences preferences;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Permissions permissions;

    public void displayProgressBar(boolean display) {
        View progressBackground = findViewById(R.id.progressBackground);
        View progressBar = findViewById(R.id.progressBar);

        if (display) {

            progressBackground.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

        } else {
            progressBackground.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        }
    }

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

        generateTabs();


    }



    public void generateTabs(){

        // Set the content of the activity to use the  activity_main.xml layout file
       // setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    }





