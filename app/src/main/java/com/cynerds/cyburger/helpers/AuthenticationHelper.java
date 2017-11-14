package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.data.FirebaseDatabaseManager;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.interfaces.OnSignInListener;
import com.cynerds.cyburger.interfaces.OnSyncResultListener;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.role.Role;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
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
    FirebaseDatabaseManager firebaseDatabaseManager;
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

        LogHelper.log("Trying to create a new profile for the new user");

        Profile profile = new Profile();
        profile.setRole(Role.USER);
        profile.setUserId(user.getUid());

        if (user == null) {
            this.user = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (firebaseDatabaseManager == null) {
            firebaseDatabaseManager = new FirebaseDatabaseManager(activity, Profile.class);
        }

        firebaseDatabaseManager.insert(profile);

        CyburgerApplication.setProfile(profile);

    }

    public void setOnSignInListener(OnSignInListener onSignInListener) {
        this.onSignInListener = onSignInListener;
    }

    public void signIn(final String email, final String password) {

        LogHelper.log("Tentando fazer signIn...");

        mAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        boolean isSuccessful = task.isSuccessful();
                        if (isSuccessful) {

                            LogHelper.log("Sign in task result: isSuccessful");
                            onSuccessfulSignIn();



                        } else {


                            final Exception exception = task.getException();

                            if (exception != null) {

                                CyburgerApplication.setOnSyncResultListener(new OnSyncResultListener() {
                                    @Override
                                    public void onSyncResult(boolean isSynced) {
                                        if (exception.getClass().equals(FirebaseNetworkException.class)&& isSynced) {
                                            LogHelper.log("Não tem internet, " +
                                                    "mas os dados já estão sincronizados, " +
                                                    "então pode entrar!");
                                            onSuccessfulSignIn();
                                            return;
                                        }

                                        LogHelper.log("Sign in task result: log - " + exception.getMessage());

                                        if (onSignInListener != null) {

                                            LogHelper.log("Callback Sign-in onError");
                                            onSignInListener.onError(exception);
                                        }
                                    }
                                });



                            }


                        }

                    }
                });
    }

    private void onSuccessfulSignIn() {
        if (user == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (firebaseDatabaseManager == null) {
            firebaseDatabaseManager = new FirebaseDatabaseManager(activity, Profile.class);
        }

        if (!findUserProfileAndSetToApplication()) {
            createProfilesList();
        }
    }

    private void createProfilesList() {
        LogHelper.log("Initializing a new onDataChangeListener for Profile");
        OnDataChangeListener onDataChangeListener = new OnDataChangeListener() {
            @Override
            public void onDataChanged() {

                if (!findUserProfileAndSetToApplication()) {
                    if (onSignInListener != null) {


                        Exception exception = new FirebaseAuthException("ERROR_CANT_LOAD_PROFILE", activity.getString(R.string.login_error_unable_load_profile));

                        onSignInListener.onError(exception);
                    }

                }

            }

            @Override
            public void onCancel() {

            }
        };

        firebaseDatabaseManager.setOnDataChangeListener(onDataChangeListener);
    }

    private boolean findUserProfileAndSetToApplication() {
        LogHelper.log("Trying to get profiles list");

        List<Profile> profiles = getProfiles();


        for (Profile profile :
                profiles) {

            if (profile != null && user != null) {
                if (profile.getUserId().equals(user.getUid())) {

                    LogHelper.log("Found a matching profile in the list: " + profile.getUserId());

                    CyburgerApplication.setProfile(profile);

                    if (onSignInListener != null) {

                        LogHelper.log("Callback Sign-in onSuccess");
                        onSignInListener.onSuccess();

                        firebaseDatabaseManager.removeListenters();

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
                            LogHelper.log("E-mail atualizado!");
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                LogHelper.log("Falha ao atualizar e-mail - "
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
                            LogHelper.log("Senha atualizada!");
                        } else {
                            LogHelper.log("Falha ao atualizar senha");
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
                            LogHelper.log("Nome atualizado!");
                        } else {
                            LogHelper.log("Falha ao atualizar nome");
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
                            LogHelper.log("Foto atualizada!");
                        } else {
                            LogHelper.log("Falha ao atualizar foto");
                        }
                    }
                });

    }

    public List<Profile> getProfiles() {
        List<Profile> profiles = firebaseDatabaseManager.get();
        LogHelper.log("Profiles loaded: " + profiles.size());
        return profiles;
    }

    public void removeOnSignInListener() {
        onSignInListener = null;
    }

}
