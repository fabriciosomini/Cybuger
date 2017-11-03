package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.roles.Role;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

/**
 * Created by fabri on 07/10/2017.
 */

public class AuthenticationHelper {

    private final Activity activity;
    FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    private UserProfileChangeRequest profileUpdates;
    private UserProfileChangeRequest.Builder profileBuilder;
    private FirebaseAuth mAuth;
    private Preferences preferences;
    private OnSignInListener onSignInListener;
    private FirebaseUser user;


    public AuthenticationHelper(Activity activity) {
        this.activity = activity;

        preferences = new Preferences(activity);


        profileBuilder = new UserProfileChangeRequest.Builder();
        mAuth = FirebaseAuth.getInstance();


    }

    public void createProfile(FirebaseUser user) {

        LogHelper.error("Trying to create a new profile for the new user");

        Profile profile = new Profile();
        profile.setRole(Role.USER);
        profile.setUserId(user.getUid());

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (firebaseRealtimeDatabaseHelper == null) {
            firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(activity, Profile.class);
        }

        firebaseRealtimeDatabaseHelper.insert(profile);

        CyburgerApplication.setProfile(profile);

    }

    public void setOnSignInListener(OnSignInListener onSignInListener) {
        this.onSignInListener = onSignInListener;
    }

    public void signIn(final String email, final String password) {

        LogHelper.error("Tentando fazer signIn...");

        mAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        boolean isSuccessful = task.isSuccessful();
                        if (isSuccessful) {

                            LogHelper.error("Sign in task result: isSuccessful");

                            if (user == null) {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                            }

                            if (firebaseRealtimeDatabaseHelper == null) {
                                firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(activity, Profile.class);
                            }

                            if(!findUserProfileAndSetToApplication())
                            {
                                createProfilesList();
                            }



                        } else {


                            Exception exception = task.getException();

                            if (exception != null) {

                                LogHelper.error("Sign in task result: error - " + exception.getMessage());

                                if (onSignInListener != null) {

                                    LogHelper.error("Callback Sign-in onError");
                                    onSignInListener.onError(exception);
                                }

                            }


                        }

                    }
                });
    }

    private void createProfilesList() {
        LogHelper.error("Initializing a new dataChangeListener for Profile");
        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {


                if(!findUserProfileAndSetToApplication())
                {
                    if (onSignInListener != null) {


                        Exception exception = new FirebaseAuthException("ERROR_CANT_LOAD_PROFILE", activity.getString(R.string.login_error_unable_load_profile));

                        onSignInListener.onError(exception);
                    }

                }


            }
        };

        firebaseRealtimeDatabaseHelper.setDataChangeListener(dataChangeListener);
    }

    private boolean findUserProfileAndSetToApplication() {
        LogHelper.error("Trying to get profiles list");

        List<Profile> profiles = getProfiles();


        for (Profile profile :
                profiles) {

            if (profile != null && user != null) {
                if (profile.getUserId().equals(user.getUid())) {
                    LogHelper.error("Found a matching profile in the list: " + profile.getUserId());
                    CyburgerApplication.setProfile(profile);

                    if (onSignInListener != null) {

                        LogHelper.error("Callback Sign-in onSuccess");
                        onSignInListener.onSuccess();

                        firebaseRealtimeDatabaseHelper.removeListenters();

                        return true;
                    }


                }
            }

        }

        return false;



    }

    public void updateEmail(@NonNull String email) {
        if (!email.isEmpty()) {
            profileBuilder.setDisplayName(email);
        }

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LogHelper.error("E-mail atualizado!");
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                LogHelper.error("Falha ao atualizar e-mail - "
                                        + exception.getClass().getSimpleName()
                                        + ": " + exception.getMessage());
                            }
                        }
                    }
                });
    }

    public void updatePassword(@NonNull String password) {

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LogHelper.error("Senha atualizada!");
                        } else {
                            LogHelper.error("Falha ao atualizar senha");
                        }
                    }
                });
    }

    public void updateDisplayName(@NonNull String displayName) {


        if (!displayName.isEmpty()) {
            profileBuilder.setDisplayName(displayName);
        }

        profileUpdates = profileBuilder.build();

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LogHelper.error("Nome atualizado!");
                        } else {
                            LogHelper.error("Falha ao atualizar nome");
                        }
                    }
                });

    }

    public void updateProfilePicture(@NonNull Uri profileUri) {


        profileBuilder.setPhotoUri(profileUri);
        profileUpdates = profileBuilder.build();

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LogHelper.error("Foto atualizada!");
                        } else {
                            LogHelper.error("Falha ao atualizar foto");
                        }
                    }
                });

    }

    public List<Profile> getProfiles() {
        List<Profile> profiles = firebaseRealtimeDatabaseHelper.get();
        LogHelper.error("Profiles loaded: " + profiles.size());
        return profiles;
    }

    public void removeOnSignInListener() {
        onSignInListener = null;
    }

    public interface OnSignInListener {

        void onSuccess();

        void onError(Exception exception);

    }
}
