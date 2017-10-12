package com.cynerds.cyburger.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.models.Tag;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagInput extends ConstraintLayout {


    private final Context context;
    private List<Tag> tagList;

    public TagInput(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
        this.context = context;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_tag_input, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        EditText searchTagItemBox = findViewById(R.id.searchTagItemBox);
        searchTagItemBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = s.toString().toLowerCase();
                if (!input.isEmpty()) {

                    List<Tag> temp = new ArrayList<Tag>();
                    for (Tag tag :
                            tagList) {

                        String tagDescription = tag.getDescription().toLowerCase();
                        Pattern p = Pattern.compile("[A-Za-z\\d]+");
                        Matcher mInput = p.matcher(input);
                        Matcher mTagDescription = p.matcher(tagDescription);

                        if (mInput.find()) {
                            input = mInput.group();
                        }

                        if (mTagDescription.find()) {
                            tagDescription = mTagDescription.group();
                        }


                        if (tagDescription.contains(input)) {
                            temp.add(tag);
                        }
                    }
                    generateTags(temp);
                } else {
                    generateTags(tagList);
                }

            }
        });
    }

    public void setFilterableList(List<Tag> tagList){

        this.tagList = tagList;
        generateTags(tagList);

    }

    private void generateTags(List<Tag> tags) {
        final FlexboxLayout flexboxLayoutAddedItems = findViewById(R.id.addedTagItemsContainer);
        FlexboxLayout flexboxLayout =  findViewById(R.id.tagItemsContainer);
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.removeAllViews();

        for (final Tag tag :
                tags) {


            final TagItem bottomTagItem = new TagItem(context);
            bottomTagItem.setText(tag.getDescription());
            bottomTagItem.setTagItemStateChangeListener(new TagItem.TagItemStateChangeListener() {
                @Override
                public void onTagItemStateChanged(TagItem item) {

                    int accentColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
                    Drawable newBackground = item.getTextView().getBackground().getConstantState().newDrawable();
                    newBackground.setColorFilter(accentColor, PorterDuff.Mode.ADD);

                    int white = ContextCompat.getColor(getContext(), R.color.white);

                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_action_close);
                    mDrawable.setColorFilter(white, PorterDuff.Mode.SRC_IN);
                    final TagItem topTagItem = new TagItem(context);
                    topTagItem.setText(tag.getDescription());
                    topTagItem.getTextView().setBackground(newBackground);
                    topTagItem.getTextView().setTextColor(white);
                    topTagItem.getTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mDrawable, null);

                    topTagItem.setTagItemStateChangeListener(new TagItem.TagItemStateChangeListener() {
                        @Override
                        public void onTagItemStateChanged(TagItem item) {
                            flexboxLayoutAddedItems.removeView(topTagItem.getTextView());
                        }
                    });

                    flexboxLayoutAddedItems.addView(topTagItem.getTextView());

                }
            });

            flexboxLayout.addView(bottomTagItem.getTextView());

        }
    }
}
