package com.cynerds.cyburger.helpers;

import android.os.Environment;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by fabri on 19/11/2017.
 */

public class FirebaseStorageHelper {
    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public FirebaseStorageHelper(){
         storage = FirebaseStorage.getInstance();
         storageRef = storage.getReference();

         createConstDirectories();

    }

    private void createConstDirectories() {
        File file = new File(Environment.getExternalStorageDirectory(), FirebaseStorageConstants.PICTURE_FOLDER);

        if(!file.exists()){
            file.mkdir();
        }
    }

    public UploadTask insert(String path, byte[] data){

        StorageReference pathRef = storageRef.child(path);
        UploadTask uploadTask = pathRef.putBytes(data);
        return uploadTask;

    }

    public FileDownloadTask get(String path, File file) {


        StorageReference pathRef = storageRef.child(path);
        return pathRef.getFile(file);
    }
}
