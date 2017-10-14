package com.cynerds.cyburger.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.adapters.TagAdapter;
import com.cynerds.cyburger.models.Tag;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagInput extends ConstraintLayout {


    private final Context context;
    private List<Tag> tagList;
    private List<Tag> selectedTags;
    private int measuredHeight = -1;

    public TagInput(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
        this.context = context;
        tagList = new ArrayList<>();
        selectedTags = new ArrayList<>();
    }

    public List<Tag> getSelectedTags() {
        return selectedTags;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_tag_input, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            this.measuredHeight = getMeasuredHeight();


    }

    public void setFilterableList(List<Tag> tagList){

        this.tagList.clear();
        this.tagList.addAll(tagList);

        final AutoCompleteTextView searchTagItemBox = findViewById(R.id.searchTagItemBox);
        TagAdapter adapter = new TagAdapter(tagList, getContext());

        searchTagItemBox.setThreshold(1);
        searchTagItemBox.setInputType(InputType.TYPE_CLASS_TEXT);
        searchTagItemBox.setAdapter(adapter);
        searchTagItemBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                generateTag((Tag) view.getTag());
            }
        });


    }

    private void generateTag(final Tag tag) {
        final FlexboxLayout flexboxLayoutAddedItems = findViewById(R.id.addedTagItemsContainer);
        flexboxLayoutAddedItems.setFlexDirection(FlexDirection.ROW);

        selectedTags.add(tag);
        int accentColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        int white = ContextCompat.getColor(getContext(), R.color.white);

        Drawable newBackground = new TagItem(getContext()).getTextView().getBackground().getConstantState().newDrawable();
        newBackground.setColorFilter(accentColor, PorterDuff.Mode.ADD);

        Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_action_close);
        mDrawable.setColorFilter(white, PorterDuff.Mode.SRC_IN);

        final TagItem topTagItem = new TagItem(context);
        topTagItem.setText(tag.getDescription());
        topTagItem.getTextView().setBackground(newBackground);
        topTagItem.getTextView().setTextColor(white);
        topTagItem.getTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mDrawable, null);

        topTagItem.setTagItemStateChangeListener(new TagItem.TagItemStateChangeListener() {
            @Override
            public void onTagItemStateChanged() {
                flexboxLayoutAddedItems.removeView(topTagItem.getTextView());
                selectedTags.remove(tag);
              
            }
        });


        flexboxLayoutAddedItems.addView(topTagItem.getTextView());


    }


}
