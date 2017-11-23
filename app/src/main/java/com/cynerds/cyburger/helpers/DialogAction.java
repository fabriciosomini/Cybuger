package com.cynerds.cyburger.helpers;

import android.view.View;

/**
 * Created by fabri on 12/07/2017.
 */

public class DialogAction {
    private View.OnClickListener positiveAction;
    private View.OnClickListener negativeAction;
    private View.OnClickListener neutralAction;


    public View.OnClickListener getPositiveAction() {
        return positiveAction;
    }

    public void setPositiveAction(View.OnClickListener positiveAction) {
        this.positiveAction = positiveAction;
    }

    public View.OnClickListener getNegativeAction() {
        return negativeAction;
    }

    public void setNegativeAction(View.OnClickListener negativeAction) {
        this.negativeAction = negativeAction;
    }

    public View.OnClickListener getNeutralAction() {
        return neutralAction;
    }

    public void setNeutralAction(View.OnClickListener neutralAction) {
        this.neutralAction = neutralAction;
    }
}