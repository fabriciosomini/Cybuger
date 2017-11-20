package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fabri on 19/11/2017.
 */

public class FileHelper {

    public static String getFileName(String path) {
        File file = new File(path);
        return file.getName();
    }

    public static String getFolderPath(String path) {
        File file = new File(path);
        return file.getParent();
    }

    public static String getStoragePath(String path) {


        return Environment.getDataDirectory() + "/" + path;
    }

    public static String getFirebasePictureStoragePath(String fileName) {


        return FirebaseStorageConstants.PICTURE_FOLDER + "/" + fileName;
    }


    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static String getApplicationDirectory(Context context){
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        PackageInfo p = null;
        try {
            p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            return s;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";

    }
}

