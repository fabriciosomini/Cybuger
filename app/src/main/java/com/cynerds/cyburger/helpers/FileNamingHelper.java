package com.cynerds.cyburger.helpers;

import android.os.Environment;

import java.io.File;

/**
 * Created by fabri on 19/11/2017.
 */

public class FileNamingHelper {

    public static String getFileName(String path) {
        File file = new File(path);
        return file.getName();
    }

    public static String getFolderPath(String path) {
        File file = new File(path);
        return file.getParent();
    }

    public static String getStoragePath(String path) {


        return Environment.getExternalStorageDirectory() + "/" + path;
    }

    public static String getFirebasePictureStoragePath(String fileName) {


        return FirebaseStorageConstants.PICTURE_FOLDER + "/" + fileName;
    }
}

