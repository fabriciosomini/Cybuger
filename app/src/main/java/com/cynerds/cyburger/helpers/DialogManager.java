package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by fabri on 08/07/2017.
 */

public class DialogManager {

    public static DialogResult Result;
    DialogAction dialogAction;
    private AlertDialog alertDialog;

    public DialogManager() {

        dialogAction = new DialogAction();
    }

    public void showDialog(Context context, String message, DialogType dialogType, final DialogAction dialogAction) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialogAction.getPositiveAction().run();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogAction.getNegativeAction().run();
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        dialogAction.getNeutralAction().run();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        switch (dialogType) {

            case OK:
                if (dialogAction.getPositiveAction() != null) {
                    alertDialog = builder.setMessage(message).setPositiveButton("OK", dialogClickListener).show();
                    break;
                }
            case OK_CANCEL:
                if (dialogAction.getPositiveAction() != null && dialogAction.getNeutralAction() != null) {
                    alertDialog = builder.setMessage(message).setPositiveButton("OK", dialogClickListener)
                            .setNegativeButton("Cancelar", dialogClickListener).show();
                    break;
                }
            case SAVE_NO_CANCEL:
                if (dialogAction.getPositiveAction() != null && dialogAction.getNegativeAction() != null
                        && dialogAction.getNeutralAction() != null) {
                    alertDialog = builder.setMessage(message).setPositiveButton("Salvar", dialogClickListener)
                            .setNegativeButton("Não salvar", dialogClickListener)
                            .setNeutralButton("Cancelar", dialogClickListener).show();
                    break;
                }
            case YES_NO:
                if (dialogAction.getPositiveAction() != null && dialogAction.getNegativeAction() != null) {
                    alertDialog = builder.setMessage(message).setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();
                    break;
                }

        }


    }

    public void closeDialog() {

        if (alertDialog != null) {

            alertDialog.cancel();

        }
    }

    public enum DialogType {
        YES_NO, OK, OK_CANCEL, SAVE_NO_CANCEL
    }

    public enum DialogResult {
        YES, NO, CANCEL
    }
}
