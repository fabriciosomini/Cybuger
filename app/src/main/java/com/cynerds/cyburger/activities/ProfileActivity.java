package com.cynerds.cyburger.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {


    FirebaseUser user;
    private AuthenticationHelper authenticationHelper;

    public ProfileActivity() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        authenticationHelper = new AuthenticationHelper(ProfileActivity.this);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setActionBarTitle(getString(R.string.title_profile));

        setUIEvents();


    }

    private void setUIEvents() {



        if (user != null) {

            final String name = user.getDisplayName() == null ? "" : user.getDisplayName();
            final String email = user.getEmail() == null ? "" : user.getEmail();
            final Uri photoUrl = user.getPhotoUrl();


            final Button saveProfileBtn = (Button) findViewById(R.id.saveProfileBtn);
            final TextView profileNameTxtEditText = (TextView) findViewById(R.id.profileNameTxt);
            final EditText profileEmailTxt = (EditText) findViewById(R.id.profileEmailTxt);
            final ImageView profilePictureImg = (ImageView) findViewById(R.id.profilePictureImg);

            String profileName = profileNameTxtEditText.getText().toString();
            profileNameTxtEditText.setText(profileName.replace("{user.name}", name));
            profileEmailTxt.setText(email);


            profilePictureImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogAction dialogAction = new DialogAction();
                    dialogAction.setPositiveAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    dialogAction.setNeutralAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    DialogManager dialogManager = new DialogManager(ProfileActivity.this,
                            DialogManager.DialogType.SAVE_CANCEL,
                            dialogAction);

                    dialogManager.showDialog("Foto do perfil", "");
                }
            });

            saveProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveProfileBtn.setEnabled(false);
                    String updatedEmail = profileEmailTxt.getText().toString().trim();

                    if (!updatedEmail.isEmpty() && !updatedEmail.equals(email)) {

                        authenticationHelper.updateEmail(updatedEmail);


                    }

                    saveProfileBtn.setEnabled(true);
                    finish();

                }
            });

        }


    }


}
