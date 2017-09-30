package com.cynerds.cyburger.components.base;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.WorkspaceActivity;
import com.cynerds.cyburger.helpers.StringImporter;


/**
 * Created by fabri on 11/07/2017.
 */

public abstract class BaseComponent<T> extends ConstraintLayout {


    protected final WorkspaceActivity finalWorkspaceActivity;
    protected final BaseComponent baseCompoenent;
    protected String attributeHint;
    protected Object componentValue;
    protected View component;
    protected String namespace;
    boolean attributeValueIsRequired;
    private boolean attributeValueIsMultiline;
    private String attributeMask;
    private String attributeValueLabel;
    private TextView baseComponentLabel;
    private View baseComponentView;
    private String hashId;
    private TextView baseComponentMessages;


    public BaseComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
        WorkspaceActivity workspaceActivity = null;
        if (!this.isInEditMode()) {
            workspaceActivity = (WorkspaceActivity) getContext();

        } else {
            workspaceActivity = new WorkspaceActivity();

        }
        baseCompoenent = this;
        finalWorkspaceActivity = workspaceActivity;


        namespace = "http://schemas.android.com/apk/res-auto";

        attributeHint = StringImporter.getStringFromResource(getContext(), attrs.getAttributeValue(namespace, "hint"));
        attributeValueLabel = attrs.getAttributeValue(namespace, "label");


        attributeValueIsRequired = Boolean.parseBoolean(attrs.getAttributeValue(namespace, "isRequired"));
        attributeValueIsMultiline = Boolean.parseBoolean(attrs.getAttributeValue(namespace, "isMultiline"));


        attributeValueLabel = StringImporter.getStringFromResource(getContext(), attributeValueLabel);

    }


    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_base, this);
    }

    public boolean isRequired() {
        return attributeValueIsRequired;
    }

    public String getHashId() {
        return hashId;
    }



    public void setValidationMessage(String validationMessage) {

        if (baseComponentMessages != null) {
            baseComponentMessages.setText(validationMessage);

        }

    }

    @Override
    protected void onFinishInflate() {


        super.onFinishInflate();
            setFocusableInTouchMode(true);
            String namespace = getClass().getName();


            baseComponentLabel = (TextView) findViewById(R.id.baseComponentLabel);
            baseComponentView = findViewById(R.id.innerbaseDate);
            baseComponentMessages = (TextView) findViewById(R.id.baseComponentMessages);

        if (baseComponentView == null) {

            return;
            }


            ViewGroup.LayoutParams layoutParams = baseComponentView.getLayoutParams();
            ViewGroup viewGroup = ((ViewGroup) findViewById(R.id.baseComponentContainer));
            int baseComponentViewIndex = viewGroup.indexOfChild(baseComponentView);

            if (attributeValueLabel != null) {
                if (!attributeValueLabel.isEmpty()) {
                    baseComponentLabel.setText(attributeValueLabel);
                }

            }


            if (attributeValueIsRequired) {

                baseComponentLabel.setText(baseComponentLabel.getText() + "*");
            }


        if (component instanceof EditText) {


                if (!attributeValueIsMultiline) {
                    ((EditText) component).setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
                }

            if (attributeHint != null) {
                ((EditText) component).setHint(attributeHint);
                }


                ((EditText) component).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (s.length() > 0) {
                            finalWorkspaceActivity.addDirty(baseCompoenent);
                            componentValue = ((EditText) component).getText().toString();

                        } else {

                            finalWorkspaceActivity.removeDirty(baseCompoenent);
                            componentValue = "";
                        }
                    }
                });

            }


            component.setLayoutParams(layoutParams);
            component.requestLayout();

            hashId = String.valueOf(component.hashCode());
            component.setId(R.id.innerbaseDate);
            viewGroup.removeViewAt(baseComponentViewIndex);
            viewGroup.addView(component, baseComponentViewIndex);


            hideValidationMessage();



    }


    protected View inflateExternalComponent(int parentId, int childId) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout innerBaseParent = (ConstraintLayout) inflater.inflate(parentId, null);
        View view = innerBaseParent.findViewById(childId);
        innerBaseParent.removeView(view);

        return view;
    }

    public T getComponent() {
        return (T) component;
    }

    public Object getComponentValue() {

        return componentValue;
    }

    public void setComponentValue(Object componentValue) {
        this.componentValue = componentValue;
    }

    public void showValidationMessage() {
        baseComponentMessages.setVisibility(VISIBLE);

    }

    public void hideValidationMessage() {
        baseComponentMessages.setVisibility(INVISIBLE);
    }




}
