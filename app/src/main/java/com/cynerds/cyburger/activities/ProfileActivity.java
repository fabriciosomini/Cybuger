package com.cynerds.cyburger.activities;

import android.os.Bundle;

import com.cynerds.cyburger.R;

public class ProfileActivity extends WorkspaceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setActionBarTitle(getString(R.string.title_profile));
    }
}
