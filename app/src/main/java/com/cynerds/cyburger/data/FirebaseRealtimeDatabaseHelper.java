package com.cynerds.cyburger.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.OnFatalErrorListener;
import com.cynerds.cyburger.models.general.BaseModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fabri on 07/07/2017.
 */

public class FirebaseRealtimeDatabaseHelper<T> {
    private final Class<BaseModel> classType;
    private final DatabaseReference databaseReference;
    private final String tableName;



    private  Context context;
    private FirebaseDatabase database;
    private DatabaseReference tableReference;
    private List<BaseModel> items;
    private DataChangeListener dataChangeListener;
    private ChildEventListener tableListener;
    private ChildEventListener databaseListener;
    private long tableChildCount = 0;
    private boolean notified;




    public FirebaseRealtimeDatabaseHelper(Class<BaseModel> classType) {

        tableName = classType.getSimpleName();
        this.classType = classType;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        tableReference = databaseReference.child(tableName);
        tableReference.keepSynced(true);
        items = new ArrayList<>();

        createDataWatcher();




    }

    public FirebaseRealtimeDatabaseHelper(Context context, Class<BaseModel> classType) {

        this(classType);
        this.context = context;

    }

    public void setDataChangeListener(DataChangeListener dataChangeListener) {

        LogHelper.error("create a new dataChangeListener of type: " + classType.getSimpleName());
        this.dataChangeListener = dataChangeListener;

    }


    public void removeListenters() {
        LogHelper.error("Remove the dataChangeListener of type: " + classType.getSimpleName());
        tableReference.removeEventListener(tableListener);
    }

    private void createDataWatcher() {


        databaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals(tableName) && !notified) {


                    tableChildCount = dataSnapshot.getChildrenCount();
                    tableReference.addChildEventListener(tableListener);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(tableName) && !notified) {


                    tableChildCount = dataSnapshot.getChildrenCount();
                    tableReference.addChildEventListener(tableListener);

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(tableName) && !notified) {


                    tableChildCount = dataSnapshot.getChildrenCount();
                    tableReference.addChildEventListener(tableListener);

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(tableName) && !notified) {


                    tableChildCount = dataSnapshot.getChildrenCount();
                    tableReference.addChildEventListener(tableListener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(databaseListener);

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

                        if (dataChangeListener != null && tableChildCount == items.size()) {
                            dataChangeListener.onDataChanged(object);

                        }

                    }
                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context,exception);
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


                        if (dataChangeListener != null && tableChildCount == items.size()) {
                            dataChangeListener.onDataChanged(object);
                        }
                    }

                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context,exception);
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

                        if (dataChangeListener != null) {
                            if (tableChildCount == items.size() || items.size() == 0) {
                                dataChangeListener.onDataChanged(object);
                            }


                        }
                    }
                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context,exception);
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
                        if (dataChangeListener != null && tableChildCount == items.size()) {
                            dataChangeListener.onDataChanged(object);
                        }
                    }

                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context,exception);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


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

        LogHelper.error("Insert new object into the database of type: " + classType.getSimpleName());
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

        LogHelper.error("Update an object into the database of type: " + classType.getSimpleName());

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

    public interface DataChangeListener {
        void onDataChanged(Object item);
    }
}
