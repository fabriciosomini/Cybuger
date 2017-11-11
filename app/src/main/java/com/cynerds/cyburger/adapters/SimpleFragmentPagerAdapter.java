package com.cynerds.cyburger.adapters;

import android.content.Context;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.fragments.SignInFragment;
import com.cynerds.cyburger.fragments.SignUpFragment;

/**
 * Created by comp8 on 04/10/2017.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private SignInFragment signInFragment;
    private SignUpFragment signUpFragment;


    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;


    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
              signInFragment = new SignInFragment();
            return signInFragment;
        } else {
             signUpFragment =  new SignUpFragment();
            return signUpFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_signIn);
            case 1:
                return mContext.getString(R.string.title_signUp);

            default:
                return null;
        }
    }


    public Fragment getCurrentSelectedItem(int currentSelectedItem) {
        if (currentSelectedItem == 0) {
            return signInFragment;
        } else {
            return signUpFragment;
        }
    }
}