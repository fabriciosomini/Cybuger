package com.cynerds.cyburger.helpers;

/**
 * Created by fabri on 12/07/2017.
 */

public class DialogAction {
    private Runnable positiveAction;
    private Runnable negativeAction;
    private Runnable neutralAction;

    public Runnable getPositiveAction() {
        return positiveAction;
    }

    public void setPositiveAction(Runnable positiveAction) {
        this.positiveAction = positiveAction;
    }

    public Runnable getNegativeAction() {
        return negativeAction;
    }

    public void setNegativeAction(Runnable negativeAction) {
        this.negativeAction = negativeAction;
    }

    public Runnable getNeutralAction() {
        return neutralAction;
    }

    public void setNeutralAction(Runnable neutralAction) {
        this.neutralAction = neutralAction;
    }
}