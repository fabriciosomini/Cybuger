package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

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

        Profile profile = new Profile();
        profile.setRole(Role.USER);
        profile.setUserId(user.getUid());

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (firebaseRealtimeDatabaseHelper == null) {
            firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Profile.class);
        }

        firebaseRealtimeDatabaseHelper.insert(profile);

        CyburgerApplication.setProfile(profile);

        if (onSignInListener != null) {

            onSignInListener.onSuccess();

        }

    }

    public void setOnSignInListener(OnSignInListener onSignInListener) {
        this.onSignInListener = onSignInListener;
    }

    public void signIn(final String email, final String password) {


        mAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        boolean isSuccessful = task.isSuccessful();
                        if (isSuccessful) {

                            if (user == null) {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                            }

                            if (firebaseRealtimeDatabaseHelper == null) {
                                firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Profile.class);
                            }

                            getProfilesList();


                        } else {


                            Exception exception = task.getException();

                            if (exception != null) {


                                if (onSignInListener != null) {

                                    onSignInListener.onError(exception);
                                }

                            }


                        }

                    }
                });
    }

    private void getProfilesList() {
        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {


                loadUserProfile();


            }
        };

        firebaseRealtimeDatabaseHelper.setDataChangeListener(dataChangeListener);
    }

    private void loadUserProfile() {

        List<Profile> profiles = getProfiles();


        for (Profile profile :
                profiles) {

            if (profile != null && user != null) {
                if (profile.getUserId().equals(user.getUid())) {
                    CyburgerApplication.setProfile(profile);

                    if (onSignInListener != null) {

                        onSignInListener.onSuccess();
                        return;
                    }


                }
            }

        }

        if (onSignInListener != null) {


            Exception exception = new FirebaseAuthException("ERROR_CANT_LOAD_PROFILE", activity.getString(R.string.login_error_unable_load_profile));

            onSignInListener.onError(exception);
        }


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
                            Toast.makeText(activity, "E-mail atualizado!", Toast.LENGTH_SHORT).show();
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                Toast.makeText(activity, "Falha ao atualizar e-mail - "
                                        + exception.getClass().getSimpleName()
                                        + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(activity, "Senha atualizada!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Falha ao atualizar senha", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(activity, "Nome atualizado!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Falha ao atualizar nome", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(activity, "Foto atualizada!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Falha ao atualizar foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public List<Profile> getProfiles() {
        List<Profile> profiles = firebaseRealtimeDatabaseHelper.get();
        return profiles;
    }

    public interface OnSignInListener {

        void onSuccess();

        void onError(Exception exception);

    }
}
