package com.cynerds.cyburger.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.models.general.FirebaseRealtimeDatabaseResult;
import com.cynerds.cyburger.models.general.BaseModel;
import com.cynerds.cyburger.models.general.MessageType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fabri on 07/07/2017.
 */

public class FirebaseDatabaseManager<T> {
    private final Class<BaseModel> classType;
    private final DatabaseReference databaseReference;
    private final String tableName;


    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference tableReference;
    private List<BaseModel> items;
    private OnDataChangeListener onDataChangeListener;
    private ChildEventListener tableListener;
    private ChildEventListener databaseListener;
    private long tableChildCount = 0;


    public FirebaseDatabaseManager(Class<BaseModel> classType) {

        tableName = classType.getSimpleName();
        this.classType = classType;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        tableReference = databaseReference.child(tableName);
        tableReference.keepSynced(true);
        items = new ArrayList<>();
        createDataWatcher();

    }


    public FirebaseDatabaseManager(Context context, Class<BaseModel> classType) {

        this(classType);
        this.context = context;

    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {

        LogHelper.log("create a new onDataChangeListener of type: " + classType.getSimpleName());
        this.onDataChangeListener = onDataChangeListener;

    }


    public void removeListenters() {
        LogHelper.log("Remove the onDataChangeListener of type: " + classType.getSimpleName());
        tableReference.removeEventListener(tableListener);
    }

    private void createDataWatcher() {


        tableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableChildCount = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        tableListener = new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try {

                    BaseModel object = dataSnapshot.getValue(classType);
                    object.setKey(dataSnapshot.getKey());

                    if (object != null) {

                        int index = getIndexOfObject(object);

                        if (index > -1) {

                            items.set(index, object);

                        } else {

                            items.add(object);
                        }

                        if (onDataChangeListener != null && tableChildCount == items.size()) {
                            onDataChangeListener.onDataChanged(object);

                        }

                    }
                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context, exception);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                try {
                    BaseModel object = dataSnapshot.getValue(classType);
                    object.setKey(dataSnapshot.getKey());
                    if (object != null) {

                        int index = getIndexOfObject(object);

                        if (index > -1) {


                            items.set(index, object);

                        }


                        if (onDataChangeListener != null && tableChildCount == items.size()) {
                            onDataChangeListener.onDataChanged(object);
                        }
                    }

                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context, exception);
                }


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {
                    BaseModel object = dataSnapshot.getValue(classType);
                    object.setKey(dataSnapshot.getKey());
                    if (object != null) {

                        int index = getIndexOfObject(object);

                        if (index > -1) {

                            items.remove(index);
                        }

                        if (onDataChangeListener != null) {
                            if (tableChildCount == items.size() || items.size() == 0) {
                                onDataChangeListener.onDataChanged(object);
                            }


                        }
                    }
                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context, exception);
                }


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                try {

                    BaseModel object = dataSnapshot.getValue(classType);
                    object.setKey(dataSnapshot.getKey());
                    if (object != null) {


                        int index = getIndexOfObject(object);

                        if (index > -1) {

                            items.set(index, object);

                        }
                        if (onDataChangeListener != null && tableChildCount == items.size()) {
                            onDataChangeListener.onDataChanged(object);
                        }
                    }

                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context, exception);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onDataChangeListener != null && tableChildCount == items.size()) {
                    onDataChangeListener.onCancel();
                    MessageHelper.show(context, MessageType.ERROR, databaseError.getMessage());
                }
            }
        };

        tableReference.addChildEventListener(tableListener);


    }


    private int getIndexOfObject(BaseModel object) {

        for (int i = 0; i < items.size(); i++) {
            BaseModel baseModel = items.get(i);
            if (baseModel.getId().equals(object.getId())) {
                return i;
            }
        }
        return -1;
    }

    public void insert(BaseModel baseModel) {

        LogHelper.log("Insert new object into the database of type: " + classType.getSimpleName());
        if (baseModel != null) {
            if (baseModel.getId() != null) {
                if (baseModel.getId().isEmpty()) {
                    baseModel.setId(UUID.randomUUID().toString());
                }
            } else {

                baseModel.setId(UUID.randomUUID().toString());
            }
        }


        final FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult = new FirebaseRealtimeDatabaseResult();


        tableReference.push().setValue(baseModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseRealtimeDatabaseResult.setMessage("Success");
                firebaseRealtimeDatabaseResult.setResultType(DatabaseOperationResultType.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                firebaseRealtimeDatabaseResult.setMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
                firebaseRealtimeDatabaseResult.setResultType(DatabaseOperationResultType.ERROR);
            }
        });


    }


    public List<T> get() {
        return (List<T>) items;
    }

    public void delete(BaseModel object) {

        if (object != null) {
            if (object.getKey() != null) {
                tableReference.child(object.getKey()).removeValue();
            }

        }


    }


    public void update(BaseModel baseModel) {

        LogHelper.log("Update an object into the database of type: " + classType.getSimpleName());

        final FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult = new FirebaseRealtimeDatabaseResult();


        tableReference.child(baseModel.getKey()).setValue(baseModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseRealtimeDatabaseResult.setMessage("Success");
                firebaseRealtimeDatabaseResult.setResultType(DatabaseOperationResultType.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseRealtimeDatabaseResult.setMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
                firebaseRealtimeDatabaseResult.setResultType(DatabaseOperationResultType.ERROR);
            }
        });

    }


    public enum DatabaseOperationResultType {

        SUCCESS, ERROR

    }

}
