package com.cynerds.cyburger.views;

import android.view.View;

import com.cynerds.cyburger.models.BaseModel;

/**
 * Created by fabri on 05/07/2017.
 */

public class DashboardCardViewItem extends BaseModel {


    private String title;
    private String content;
    private int headerIconId;
    private int actionIconId;
    private View.OnClickListener onManageClickListener;
    private View.OnClickListener onCardViewClickListener;
    private Object extra;

    public View.OnClickListener getOnCardViewClickListener() {
        return onCardViewClickListener;
    }

    public void setOnCardViewClickListener(View.OnClickListener onCardViewClickListener) {
        this.onCardViewClickListener = onCardViewClickListener;
    }

    public View.OnClickListener getOnManageClickListener() {
        return onManageClickListener;
    }

    public void setOnManageClickListener(View.OnClickListener onManageClickListener) {
        this.onManageClickListener = onManageClickListener;
    }

    public int getHeaderIconId() {
        return headerIconId;
    }

    public void setHeaderIconId(int headerIconId) {
        this.headerIconId = headerIconId;
    }

    public int getActionIconId() {
        return actionIconId;
    }

    public void setActionIconId(int actionIconId) {
        this.actionIconId = actionIconId;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}