package com.cynerds.cyburger.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {


    public ProfileActivity() {


    }


    public void updateUser() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setActionBarTitle(getString(R.string.title_profile));

        setUIEvents();


    }

    private void setUIEvents() {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String name = user.getDisplayName() == null ? "" : user.getDisplayName();
            String email = user.getEmail() == null ? "" : user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

            TextView profileNameTxtEditText = (TextView) findViewById(R.id.profileNameTxt);
            String profileName = profileNameTxtEditText.getText().toString();
            profileNameTxtEditText.setText(profileName.replace("{user.name}", name));

            EditText profileEmailTxt = (EditText) findViewById(R.id.profileEmailTxt);
            profileEmailTxt.setText(email);
        }



    }
}
