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
    private UserProfileChangeRequest profileUpdates;
    private UserProfileChangeRequest.Builder profileBuilder;
    private FirebaseAuth mAuth;
    private Preferences preferences;
    private FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    private OnSignInListener onSignInListener;
    private FirebaseUser user;

    public AuthenticationHelper(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        preferences = new Preferences(activity);
        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Profile.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
        profileBuilder = new UserProfileChangeRequest.Builder();
    }

    public void createProfile(FirebaseUser user) {

        Profile profile = new Profile();
        profile.setRole(Role.USER);
        profile.setUserId(user.getUid());

        firebaseRealtimeDatabaseHelper.insert(profile);

        ((CyburgerApplication) activity.getApplication()).setProfile(profile);

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


                            try {
                                loadUserProfile();

                                if (onSignInListener != null) {

                                    onSignInListener.onSuccess();
                                }
                            } catch (FirebaseAuthException e) {

                                DialogManager dialogManager = new DialogManager(activity);

                                dialogManager.showDialog("",
                                        activity.getString(R.string.login_error_unable_load_profile));

                                if (onSignInListener != null) {

                                    onSignInListener.onError(e);
                                }
                            }


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

    private void loadUserProfile() throws FirebaseAuthException {


        List<Profile> profiles = firebaseRealtimeDatabaseHelper.get();


        if (profiles.size() > 0) {


            for (Profile profile :
                    profiles) {

                if (profile.getUserId().equals(user.getUid())) {
                    ((CyburgerApplication) activity.getApplication()).setProfile(profile);

                    return;
                }
            }


        }


        throw new FirebaseAuthException("ERROR_CANT_LOAD_PROFILE", activity.getString(R.string.login_error_unable_load_profile));


    }

    public void updateEmail(@NonNull String email) {
        if (!email.isEmpty()) {
            profileBuilder.setDisplayName(email);
        }
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "E-mail atualizado!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Falha ao atualizar e-mail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updatePassword(@NonNull String password) {

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

    public interface OnSignInListener {

        void onSuccess();

        void onError(Exception exception);

    }
}
