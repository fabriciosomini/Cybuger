package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by fabri on 19/11/2017.
 */

public class FileDialogHelper {
    private static final String PARENT_DIR = "..";
    private final String TAG = getClass().getName();
    private String[] fileList;
    private File currentPath;
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    private interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialogHelper.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialogHelper.DirectorySelectedListener>();
    private final Activity activity;
    private boolean selectDirectoryOption;
    private String[] fileEndsWith;

    /**
     * @param activity
     * @param initialPath
     */
    private FileDialogHelper(Activity activity, File initialPath) {
        this(activity, initialPath, null);
    }

    private FileDialogHelper(Activity activity, File initialPath, String[] fileEndsWith) {
        this.activity = activity;
        setFileEndsWith(fileEndsWith);
        if (!initialPath.exists()) initialPath = Environment.getExternalStorageDirectory();
        loadFileList(initialPath);
    }

    /**
     * @return file dialog
     */
    private Dialog createFileDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select directory", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, currentPath.getPath());
                    fireDirectorySelectedEvent(currentPath);
                }
            });
        }

        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                } else fireFileSelectedEvent(chosenFile);
            }
        });

        dialog = builder.show();
        return dialog;
    }


    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    private void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    private void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    private void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    private void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */
    private void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new ListenerList.FireHandler<DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead()) return false;
                    if (selectDirectoryOption) return sel.isDirectory();
                    else {
                        boolean endsWith = false;
                        if(fileEndsWith!=null){
                            for (String str :
                                    fileEndsWith) {
                                if(filename.toLowerCase().endsWith(str))
                                {
                                    endsWith = true;
                                    break;
                                }
                            }
                        }

                        return endsWith || sel.isDirectory();
                    }
                }
            };
            String[] fileList1 = path.list(filter);
            for (String file : fileList1) {
                r.add(file);
            }
        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    private void setFileEndsWith(String[] fileEndsWith) {
        this.fileEndsWith = fileEndsWith;
    }

    public static FileDialogHelper showFileDialog(Activity activity, String initialDir, String[] extensions){


        File mPath = new File(initialDir);
        FileDialogHelper fileDialog = new FileDialogHelper(activity, mPath, extensions);
        fileDialog.addFileListener(new FileDialogHelper.FileSelectedListener() {
            public void fileSelected(File file) {
                //  Log.d(getClass().getName(), "selected file " + file.toString());
            }
        });
        //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
        //  public void directorySelected(File directory) {
        //      Log.d(getClass().getName(), "selected dir " + directory.toString());
        //  }
        //});
        //fileDialog.setSelectDirectoryOption(false);
        fileDialog.showDialog();

        return fileDialog;
    }
}

class ListenerList<L> {
    private List<L> listenerList = new ArrayList<L>();

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }

    public void add(L listener) {
        listenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<L>(listenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }

    public void remove(L listener) {
        listenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return listenerList;
    }
}
