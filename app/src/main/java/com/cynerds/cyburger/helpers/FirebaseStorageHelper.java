package com.cynerds.cyburger.helpers;

import android.os.Environment;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fabri on 19/11/2017.
 */

public class FirebaseStorageHelper {
    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public FirebaseStorageHelper() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


    }


    public UploadTask insert(String pictureUri, byte[] data) {

        File file = new File(Environment.getExternalStorageDirectory() + "/" + pictureUri);
        if (!file.exists()) {
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(data);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        StorageReference pathRef = storageRef.child(pictureUri);
        UploadTask uploadTask = pathRef.putBytes(data);
        return uploadTask;

    }

    public FileDownloadTask get(String path, File file) {


        File parent = new File(file.getParent());
        if (!parent.exists()) {
            boolean createdParent = parent.mkdir();
            if (createdParent) {
                LogHelper.log("Created directory: " + parent.getPath());
            } else {
                LogHelper.log("Failed to create directory: " + parent.getPath());
            }
        }


        file = new File(parent.getPath() + "/" + file.getName());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StorageReference pathRef = storageRef.child(path);
        return pathRef.getFile(file);
    }
}
