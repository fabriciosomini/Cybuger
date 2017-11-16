package com.cynerds.cyburger.activities;

import android.net.Uri;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.PhotoViewer;
import com.cynerds.cyburger.dao.UserAccountDAO;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.BonusPointExchangeHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.models.account.UserAccount;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.profile.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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



        Profile curentProfile = CyburgerApplication.getProfile();
        if (user != null && curentProfile !=null) {

            final String name = user.getDisplayName() == null ? "" : user.getDisplayName();
            final String email = user.getEmail() == null ? "" : user.getEmail();
            final Uri photoUrl = user.getPhotoUrl();


            final Button saveProfileBtn = findViewById(R.id.saveProfileBtn);
            final TextView profileNameTxtEditText = findViewById(R.id.profileNameTxt);
            final EditText profileEmailTxt = findViewById(R.id.profileEmailTxt);
            final TextView profileBonusPointsTextView = findViewById(R.id.profileBonusPointsTextView);
            final PhotoViewer profilePictureImg = findViewById(R.id.profilePictureImg);


            String strTotalBonusPoints = String.valueOf(curentProfile.getBonusPoints());
            String profileName = profileNameTxtEditText.getText().toString();
            String profileBonusPoints = profileBonusPointsTextView.getText().toString();
            profileNameTxtEditText.setText(profileName.replace("{user.name}", name));
            profileEmailTxt.setText(email);
            profilePictureImg.setEditable(true);

            profileBonusPointsTextView.setText(profileBonusPoints.replace("{totalBonusPoints}",
                    strTotalBonusPoints ));

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
                            DialogManager.DialogType.SAVE_CANCEL);

                    dialogManager.setAction(dialogAction);
                    dialogManager.setContentView(R.layout.component_photo_viewer);
                    dialogManager.showDialog("Foto do perfil", "");


                }
            });

            saveProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final String updatedEmail = profileEmailTxt.getText().toString().trim();

                    if (FieldValidationHelper.isEditTextValidated(profileEmailTxt)) {

                        saveProfileBtn.setEnabled(false);
                        showBusyLoader(true);

                        authenticationHelper.updateEmail(updatedEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    UserAccountDAO userAccountDAO = new UserAccountDAO(ProfileActivity.this);
                                    UserAccount userAccount = CyburgerApplication.getUserAccount();
                                    userAccount.setEmail(updatedEmail);

                                    userAccountDAO.InsertUnique(userAccount.getEmail(), userAccount.getPassword());

                                    MessageHelper.show(ProfileActivity.this,
                                            MessageType.SUCCESS,
                                            "Perfil atualizado");

                                    finish();
                                }
                                else{
                                    MessageHelper.show(ProfileActivity.this,
                                            MessageType.ERROR,
                                            "Erro ao atualizar perfil");

                                    showBusyLoader(false);
                                    saveProfileBtn.setEnabled(true);

                                }
                            }
                        });


                    }




                }
            });

        }


    }


}
