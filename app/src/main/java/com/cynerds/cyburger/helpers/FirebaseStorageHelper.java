package com.cynerds.cyburger.helpers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by fabri on 19/11/2017.
 */

public class FirebaseStorageHelper {
    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public FirebaseStorageHelper(){
         storage = FirebaseStorage.getInstance();
         storageRef = storage.getReference();
    }

    public UploadTask insert(String path, byte[] data){

        StorageReference pathRef = storageRef.child(path);
        UploadTask uploadTask = pathRef.putBytes(data);


        return uploadTask;

    }
}
