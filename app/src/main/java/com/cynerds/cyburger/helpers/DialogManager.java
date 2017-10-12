package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 08/07/2017.
 */

public class DialogManager {

    public static DialogResult Result;
    private final Context context;
    private DialogType dialogType;
    private DialogAction dialogAction;
    private AlertDialog alertDialog;
    private int layoutResId = -1;
    private View contentView;
    public DialogManager(Context context, DialogType dialogType, DialogAction dialogAction) {

        this.context = context;
        this.dialogType = dialogType;
        this.dialogAction = dialogAction;
    }


    public DialogManager(Context context) {

        this.context = context;


    }

    public void showDialog(String message) {

        showDialog("", message);
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(@LayoutRes int layoutResID) {
        this.layoutResId = layoutResID;
    }

    public void showDialog(String title, String message) {


        final Context dialogContext = context;

        DialogInterface.OnClickListener dialogClickListener;
        if (dialogAction != null) {
            dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (dialogAction.getPositiveAction() != null) {
                            dialogAction.getPositiveAction().onClick(((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE));
                            break;
                        }

                    case DialogInterface.BUTTON_NEGATIVE:
                        if (dialogAction.getNegativeAction() != null) {
                            dialogAction.getNegativeAction().onClick(((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE));
                            break;
                        }

                    case DialogInterface.BUTTON_NEUTRAL:
                        if (dialogAction.getNeutralAction() != null) {
                            dialogAction.getNeutralAction().onClick(((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL));
                            break;
                        }

                }
            }
        };
        } else {
            //Se você não definir as ações do click, o comportamento será o padrão
            dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };

        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = inflater.inflate(R.layout.alert_dialog_title, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        alertDialog = builder.create();
        title = title == null ? "" : title;
        message = message == null ? "" : message;

        if (layoutResId > -1) {

           
            contentView = inflater.inflate(layoutResId, null);

            alertDialog.setCustomTitle(titleView);
            TextView titleText = titleView.findViewById(R.id.alertTitleText);
            titleText.setText(title);
            alertDialog.setView(contentView);

        } else {
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);

        }


        if (dialogType != null) {

            switch (dialogType) {

                case OK:
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", dialogClickListener);

                case OK_CANCEL:
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", dialogClickListener);

                case SAVE_NO_CANCEL:

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Salvar", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", dialogClickListener);
                    break;

                case YES_NO:

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", dialogClickListener);
                    break;


                case SAVE_CANCEL:

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Salvar", dialogClickListener);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", dialogClickListener);
                    break;


            }
        } else {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", dialogClickListener);

        }


        alertDialog.show();


    }

    public void closeDialog() {

        if (alertDialog != null) {

            alertDialog.cancel();

        }
    }

    public enum DialogType {
        YES_NO, OK, OK_CANCEL, SAVE_NO_CANCEL, SAVE_CANCEL
    }

    public enum DialogResult {
        YES, NO, CANCEL
    }
}
