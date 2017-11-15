package com.cynerds.cyburger.activities;

import android.app.Fragment;
import android.content.Intent;
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
import com.cynerds.cyburger.components.NonSwipeableViewPager;
import com.cynerds.cyburger.fragments.SignInFragment;
import com.cynerds.cyburger.fragments.SignUpFragment;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.Permissions;
import com.cynerds.cyburger.helpers.Preferences;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class LoginActivity extends BaseActivity {

    public static SignInFragment signInFragment;
    public static SignUpFragment signUpFragment;




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NonSwipeableViewPager nonSwipeableViewPager = findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter simpleFragmentPagerAdapter =
                (SimpleFragmentPagerAdapter)nonSwipeableViewPager.getAdapter();
        android.support.v4.app.Fragment fragment = simpleFragmentPagerAdapter.getCurrentSelectedItem(nonSwipeableViewPager.getCurrentItem());
        fragment.onActivityResult(requestCode, resultCode, data);


    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        generateTabs();


    }


    public void generateTabs() {

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





