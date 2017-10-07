package com.cynerds.cyburger.helpers;

import android.app.Activity;
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

import java.util.List;

/**
 * Created by fabri on 07/10/2017.
 */

public class AuthenticationHelper {

    private final Activity activity;
    private FirebaseAuth mAuth;
    private Preferences preferences;
    private FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    private OnSignInListener onSignInListener;

    public AuthenticationHelper(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        preferences = new Preferences(activity);
        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Profile.class);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

    public interface OnSignInListener {

        void onSuccess();

        void onError(Exception exception);

    }
}
