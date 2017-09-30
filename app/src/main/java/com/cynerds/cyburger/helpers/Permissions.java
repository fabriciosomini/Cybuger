package com.cynerds.cyburger.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by fabri on 15/07/2017.
 */

public class Permissions {
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_PREFERENCES = 0;
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_PREFERENCES = 1;


    private Activity context;

    public Permissions(Activity context) {
        this.context = context;

    }

    public boolean isPemissionGranted(String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);

        } else {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }

    }

    public void requestPermissionForExternalStorage() {
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_PREFERENCES);

        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_PREFERENCES);
    }

    public boolean isPermissionForExternalStorageGranted() {
        return isPemissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && isPemissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
}
