package com.cynerds.cyburger.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.List;

/**
 * Created by fabri on 05/07/2017.
 */

public class DashboardCardAdapter extends ArrayAdapter<DashboardCardViewItem> {


    public DashboardCardAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<DashboardCardViewItem> objects) {
        super(context, resource, objects);
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        final DashboardCardViewItem dashboardCardViewItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_card_view, parent, false);
        }
        // Lookup view for data population
        ImageView cardIcon = convertView.findViewById(R.id.cardIcon);
        TextView cardTitle = convertView.findViewById(R.id.cardTitle);
        TextView cardContent = convertView.findViewById(R.id.cardContent);
        ImageView cardManageIcon = convertView.findViewById(R.id.cardManageIcon);
        ConstraintLayout baseComponentContainer = convertView.findViewById(R.id.dashboard_cardview);
        View.OnClickListener onCardViewClickListener = dashboardCardViewItem.getOnCardViewClickListener();
        View.OnClickListener onManageClickListener = dashboardCardViewItem.getOnManageClickListener();

        cardManageIcon.setImageResource(dashboardCardViewItem.getActionIconId());


        if (dashboardCardViewItem.getTitleColor() > 0) {
            cardTitle.setTextColor(ContextCompat.getColor(getContext(), dashboardCardViewItem.getTitleColor()));
        }

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
