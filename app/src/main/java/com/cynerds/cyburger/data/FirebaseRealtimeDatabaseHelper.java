package com.cynerds.cyburger.data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 07/07/2017.
 */

public class FirebaseRealtimeDatabaseHelper<T> {
    private final Class<T> classType;
    private final DatabaseReference mainReference;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    // ConnectivityManager connectivityManager;
    private List<T> items;
    private DataChangeListener dataChangeListener;

    public interface DataChangeListener {
        public void onDataChanged(Object item);
    }

    public void setDataChangeListener(DataChangeListener dataChangeListener ){

        this.dataChangeListener = dataChangeListener;

    }
    public FirebaseRealtimeDatabaseHelper(Class<T> classType) {

        this.classType = classType;
        database = FirebaseDatabase.getInstance();
        mainReference = database.getReference();
        //connectivityManager = new ConnectivityManager(mainReference);

        databaseReference = mainReference.child(classType.getSimpleName());
        databaseReference.keepSynced(true);

        items = new ArrayList<>();

        createDataWatcher();



    }


    /*public boolean isConnected(){
        return connectivityManager.isConnected();
    }*/

    private void createDataWatcher() {
        ChildEventListener postListener = new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                T object = dataSnapshot.getValue(classType);

                if (object != null) {
                    if (!items.contains(object)) {
                        items.add(object);

                    }

                    if(dataChangeListener !=null){
                        dataChangeListener.onDataChanged(object);

                    }

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                T object = dataSnapshot.getValue(classType);
                if (object != null) {


                    if(dataChangeListener !=null){
                        dataChangeListener.onDataChanged(object);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                T object = dataSnapshot.getValue(classType);
                if (object != null) {


                    if(dataChangeListener !=null){
                        dataChangeListener.onDataChanged(object);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                T object = dataSnapshot.getValue(classType);
                if (object != null) {

                    if(dataChangeListener !=null){
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

    public FirebaseRealtimeDatabaseResult insert(T t) {

        FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult = new FirebaseRealtimeDatabaseResult();
        firebaseRealtimeDatabaseResult.setMessage("Success");
        firebaseRealtimeDatabaseResult.setResultType(DatabaseOperationResultType.SUCCESS);

        databaseReference.push().setValue(t);

        return firebaseRealtimeDatabaseResult;
    }


    public List<T> selectAll() {


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
}
