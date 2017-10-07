package com.cynerds.cyburger.data;

import com.cynerds.cyburger.models.BaseModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fabri on 07/07/2017.
 */

public class FirebaseRealtimeDatabaseHelper<T> {
    private final Class<BaseModel> classType;
    private final DatabaseReference mainReference;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    // ConnectivityManager connectivityManager;
    private List<BaseModel> items;
    private DataChangeListener dataChangeListener;

    public FirebaseRealtimeDatabaseHelper(Class<BaseModel> classType) {

        this.classType = classType;
        database = FirebaseDatabase.getInstance();
        mainReference = database.getReference();
        //connectivityManager = new ConnectivityManager(mainReference);

        databaseReference = mainReference.child(classType.getSimpleName());
        databaseReference.keepSynced(true);

        items = new ArrayList<>();

        createDataWatcher();


    }

    public void setDataChangeListener(DataChangeListener dataChangeListener) {

        this.dataChangeListener = dataChangeListener;

    }

    private void createDataWatcher() {
        ChildEventListener postListener = new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                BaseModel object = dataSnapshot.getValue(classType);


                if (object != null) {

                    int index = getIndexOfObject(object);

                    if (index > -1) {

                        items.set(index, object);

                    } else {

                        items.add(object);
                    }

                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(object);

                    }

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                BaseModel object = dataSnapshot.getValue(classType);
                if (object != null) {

                    int index = getIndexOfObject(object);

                    if (index > -1) {


                        items.set(index, object);

                    }


                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(object);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BaseModel object = dataSnapshot.getValue(classType);
                if (object != null) {

                    int index = getIndexOfObject(object);

                    if (index > -1) {

                        items.remove(index);
                    }

                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(object);

                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                BaseModel object = dataSnapshot.getValue(classType);
                if (object != null) {


                    int index = getIndexOfObject(object);

                    if (index > -1) {

                        items.set(index, object);

                    }
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(object);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        databaseReference.addChildEventListener(postListener);

    }


    /*public boolean isConnected(){
        return connectivityManager.isConnected();
    }*/

    private int getIndexOfObject(BaseModel object) {

        for (int i = 0; i < items.size(); i++) {
            BaseModel baseModel = items.get(i);
            if (baseModel.getId().equals(object.getId())) {
                return i;
            }
        }
        return -1;
    }

    public FirebaseRealtimeDatabaseResult insert(BaseModel baseModel) {

        if (baseModel != null) {
            if (baseModel.getId() != null) {
                if (baseModel.getId().isEmpty()) {
                    baseModel.setId(UUID.randomUUID().toString());
                }
            } else {

                baseModel.setId(UUID.randomUUID().toString());
            }
        }


        FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult = new FirebaseRealtimeDatabaseResult();
        firebaseRealtimeDatabaseResult.setMessage("Success");
        firebaseRealtimeDatabaseResult.setResultType(DatabaseOperationResultType.SUCCESS);

        databaseReference.push().setValue(baseModel);

        return firebaseRealtimeDatabaseResult;
    }

    public List<BaseModel> selectAll() {


        return items;



       /* if (type.equals(Incoming.class.getName())) {


            FirebaseRealtimeDatabaseHelper<IncomingType> incomingTypeDatabaseOperations = new FirebaseRealtimeDatabaseHelper<>(IncomingType.class);
            List<IncomingType> incomingTypes = incomingTypeDatabaseOperations.selectAll(IncomingType.class.getName());
            IncomingType incomingType = incomingTypes.get(0);

            Incoming incoming1 = new Incoming();
            incoming1.setDescription("Salário parte 1");
            incoming1.setAmount(900.50f);
            incoming1.setIncomingType(incomingType);

            Incoming incoming2 = new Incoming();
            incoming2.setDescription("Salário parte 2");
            incoming2.setAmount(300.50f);
            incoming2.setIncomingType(incomingType);


            items.add((T) incoming1);
            items.add((T) incoming2);
        }

        if (type.equals(IncomingType.class.getName())) {

            IncomingType incomingTypeSalary = new IncomingType();
            incomingTypeSalary.setId(0);
            incomingTypeSalary.setDescription("Salário");

            IncomingType incomingTypeExtraIncoming = new IncomingType();
            incomingTypeExtraIncoming.setId(1);
            incomingTypeExtraIncoming.setDescription("Receita extra");

            IncomingType incomingTypeSavings = new IncomingType();
            incomingTypeSavings.setId(2);
            incomingTypeSavings.setDescription("Econômias");

            items.add((T) incomingTypeSalary);
            items.add((T) incomingTypeExtraIncoming);
            items.add((T) incomingTypeSavings);
        }


        return (ArrayList<T>) items;*/
    }

    public Query getDatabaseReference() {
        return databaseReference;
    }

    public enum DatabaseOperationResultType {

        SUCCESS, ERROR

    }

    public interface DataChangeListener {
        void onDataChanged(Object item);
    }
}
