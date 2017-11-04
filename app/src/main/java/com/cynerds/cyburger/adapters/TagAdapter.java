package com.cynerds.cyburger.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.models.view.TagModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 13/10/2017.
 */


public class TagAdapter extends BaseAdapter implements Filterable {
    final List<TagModel> listItems;
    final List<TagModel> suggestions = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    private Filter filter = new CustomFilter();

    public TagAdapter(Context context, List<TagModel> listItems) {
        this.listItems = listItems;
        this.suggestions.addAll(listItems);
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int position) {
        return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(android.R.layout.simple_list_item_1, null);
         v.setBackgroundColor(ContextCompat.getColor(context, R.color.redishBlack));

        TextView title = v.findViewById(android.R.id.text1);

        title.setText(suggestions.get(position).getDescription());
        title.setTag(suggestions.get(position));
        return v;
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    private class CustomFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();


            if (listItems != null && constraint != null) {
                for (int i = 0; i < listItems.size(); i++) {
                    if (listItems.get(i).getDescription().toLowerCase().replaceAll("\\W+", "")
                            .contains(constraint.toString().replaceAll("\\W+", ""))) {
                        suggestions.add(listItems.get(i));
                    }
                }
            }

            if (constraint.toString().isEmpty()) {
                suggestions.addAll(listItems);
            }

            FilterResults results = new FilterResults();
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}