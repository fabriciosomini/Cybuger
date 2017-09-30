package com.cynerds.cyburger.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.components.behavior.DashboardCardViewItem;

import java.util.List;

/**
 * Created by fabri on 05/07/2017.
 */

public class DashboardCardAdapter extends ArrayAdapter<DashboardCardViewItem> {


    public DashboardCardAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DashboardCardViewItem> objects) {
        super(context, resource, objects);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DashboardCardViewItem dashboardCardViewItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_card_view, parent, false);
        }
        // Lookup view for data population
        ImageView cardIcon =   (ImageView) convertView.findViewById(R.id.cardIcon);
        TextView cardTitle =   (TextView) convertView.findViewById(R.id.cardTitle);
        TextView cardContent = (TextView) convertView.findViewById(R.id.cardContent);
        ImageView cardManageIcon = (ImageView) convertView.findViewById(R.id.cardManageIcon);
        ConstraintLayout baseComponentContainer = (ConstraintLayout) convertView.findViewById(R.id.baseComponentContainer);
        View.OnClickListener onCardViewClickListener = dashboardCardViewItem.getOnCardViewClickListener();
        View.OnClickListener onManageClickListener = dashboardCardViewItem.getOnManageClickListener();

        cardManageIcon.setImageResource(dashboardCardViewItem.getActionIconId());




        if(onCardViewClickListener !=null)
        {
            baseComponentContainer.setOnClickListener(onCardViewClickListener);

        }

        if(onManageClickListener !=null)
        {
            cardManageIcon.setOnClickListener(onManageClickListener);

        }

        cardIcon.setImageResource(dashboardCardViewItem.getHeaderIconId());
        cardTitle.setText(dashboardCardViewItem.getTitle());
        cardContent.setText(dashboardCardViewItem.getContent());

        return convertView;
    }



}
