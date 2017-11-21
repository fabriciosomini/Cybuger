package com.cynerds.cyburger.helpers;

import android.content.Context;

import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.models.general.FirebaseRealtimeDatabaseResult;
import com.cynerds.cyburger.models.general.BaseModel;
import com.cynerds.cyburger.models.general.MessageType;
import com.google.android.gms.tasks.Task;
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

public class FirebaseDatabaseHelper<T> {
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

    private boolean notityPending;
    private long loadedItemsCount;

    public FirebaseDatabaseHelper(Class<BaseModel> classType) {

        loadedItemsCount = -1;
        tableName = classType.getSimpleName();
        this.classType = classType;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        tableReference = databaseReference.child(tableName);
        tableReference.keepSynced(true);
        items = new ArrayList<>();
        createDataWatcher();

    }


    public FirebaseDatabaseHelper(Context context, Class<BaseModel> classType) {

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
        onDataChangeListener = null;
    }

    private void createDataWatcher() {


        tableReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                loadedItemsCount = dataSnapshot.getChildrenCount();

                LogHelper.log("Loaded " + loadedItemsCount
                        + " items of type: " + classType.getSimpleName());

                notifyIfNeeded();
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

                    }

                    notifyIfNeeded();

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

                    }

                    notifyIfNeeded();

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


                    }

                    notifyIfNeeded();
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

                    }

                    notifyIfNeeded();

                } catch (DatabaseException exception) {
                    CyburgerApplication.onFatalErrorListener.onFatalError(context, exception);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onDataChangeListener != null && loadedItemsCount == items.size()) {
                    onDataChangeListener.onCancel();
                   // MessageHelper.show(context, MessageType.ERROR, databaseError.getMessage());
                }
            }
        };


        tableReference.addChildEventListener(tableListener);


    }

    private void notifyIfNeeded() {
        if (onDataChangeListener != null && items.size() == loadedItemsCount) {
            notityPending = false;
            onDataChangeListener.onDatabaseChanges();

        } else if (items.size() != loadedItemsCount) {
            notityPending = true;
        }
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

    public Task<Void> insert(BaseModel baseModel) {

        LogHelper.log("InsertUnique new object into the database of type: " + classType.getSimpleName());
        if (baseModel != null) {
            if (baseModel.getId() != null) {
                if (baseModel.getId().isEmpty()) {
                    baseModel.setId(UUID.randomUUID().toString());
                }
            } else {

                baseModel.setId(UUID.randomUUID().toString());
            }
        }

        return tableReference.push().setValue(baseModel);
    }

    public List<T> get() {
        LogHelper.log("Get items " + loadedItemsCount + " of type: " + classType.getSimpleName());


        return (List<T>) items;
    }

    public List<T> get(String key) {
        LogHelper.log("Get items " + loadedItemsCount + " of type: " + classType.getSimpleName());

        List<T> filtered = new ArrayList<>();

        for (BaseModel t :
                items) {
            if(t.getKey().equals(key))
            {
                filtered.add((T)t);
            }
        }

        return (List<T>) filtered;
    }

    public Task<Void> delete(BaseModel baseModel) {

        String key = "0";
        if(baseModel !=null){
            key = baseModel.getKey();
        }

        return tableReference.child(key).removeValue();

    }


    public Task<Void> update(BaseModel baseModel) {

        LogHelper.log("Update an object into the database of type: " + classType.getSimpleName());

        final FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult = new FirebaseRealtimeDatabaseResult();


        String key = "0";
        if(baseModel !=null){
            key = baseModel.getKey();
        }
       return tableReference.child(key).setValue(baseModel);

    }

    public boolean isFullLoaded() {
        return get().size() == loadedItemsCount;
    }

    public void notifyChanges() {
        if(onDataChangeListener!=null)
        {
            onDataChangeListener.onDatabaseChanges();
        }
    }


    public enum DatabaseOperationResultType {

        SUCCESS, ERROR

    }

}
