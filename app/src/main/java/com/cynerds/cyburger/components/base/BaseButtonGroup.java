package com.cynerds.cyburger.components.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 25/07/2017.
 */

public class BaseButtonGroup extends ConstraintLayout {

    private RadioGroup radioGroup;
    private RadioButton firstButton;
    private RadioButton secondButton;
    private RadioButton thirdButton;
    private ButtonGroupType buttonGroupType;
    private String attributeFirstButtonText;
    private String attributeSecondButtonText;
    private String attributeThirdButtonText;
    private Runnable firstButtonAction;
    private Runnable secondButtonAction;
    private Runnable thirdButtonAction;
    private ButtonPosition selected;

    public BaseButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        selected = ButtonPosition.FIRST;
        String namespace = "http://schemas.android.com/apk/res-auto";

        attributeFirstButtonText = attrs.getAttributeValue(namespace, "firstButtonText");
        attributeSecondButtonText = attrs.getAttributeValue(namespace, "secondButtonText");
        attributeThirdButtonText = attrs.getAttributeValue(namespace, "thirdButtonText");


        if (attributeFirstButtonText != null) {

            try {
                attributeFirstButtonText = getContext().getString(Integer.valueOf(attributeFirstButtonText.replace("@", "")));
            } catch (NumberFormatException e) {
            }

        }

        if (attributeSecondButtonText != null) {

            try {
                attributeSecondButtonText = getContext().getString(Integer.valueOf(attributeSecondButtonText.replace("@", "")));
            } catch (NumberFormatException e) {
            }

        }

        if (attributeThirdButtonText != null) {

            try {
                attributeThirdButtonText = getContext().getString(Integer.valueOf(attributeThirdButtonText.replace("@", "")));
            } catch (NumberFormatException e) {
            }

        }
        initializeViews(context);
    }

    public void setSelected(ButtonPosition selected) {
        this.selected = selected;

        switch (selected) {
            case FIRST:
                radioGroup.check(R.id.baseButtonGroupFirstButton);
                return;

            case SECOND:
                radioGroup.check(R.id.baseButtonGroupSecondButton);
                return;

            case THIRD:
                radioGroup.check(R.id.baseButtonGroupThirdButton);
                return;

        }
    }

    public void setFirstButtonAction(Runnable firstButtonAction) {
        this.firstButtonAction = firstButtonAction;
    }

    public void setSecondButtonAction(Runnable secondButtonAction) {
        this.secondButtonAction = secondButtonAction;
    }

    public void setThirdButtonAction(Runnable thirdButtonAction) {
        this.thirdButtonAction = thirdButtonAction;
    }

    public Button getFirstButton() {
        return firstButton;
    }

    public Button getSecondButton() {
        return secondButton;
    }

    public Button getThirdButton() {
        return thirdButton;
    }

    public void setButtonGroupType(ButtonGroupType buttonGroupType) {
        this.buttonGroupType = buttonGroupType;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.buttongroup_base, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        firstButton = (RadioButton) findViewById(R.id.baseButtonGroupFirstButton);
        secondButton = (RadioButton) findViewById(R.id.baseButtonGroupSecondButton);
        thirdButton = (RadioButton) findViewById(R.id.baseButtonGroupThirdButton);

        if (attributeFirstButtonText != null) {
            firstButton.setText(attributeFirstButtonText);

        }
        if (attributeSecondButtonText != null) {
            secondButton.setText(attributeSecondButtonText);
        }

        if (buttonGroupType == ButtonGroupType.THREE) {

            if (attributeThirdButtonText != null) {
                thirdButton.setText(attributeThirdButtonText);
            }
        } else if (buttonGroupType == ButtonGroupType.TWO) {

            thirdButton.setVisibility(GONE);

        }

        radioGroup = (RadioGroup) findViewById(R.id.baseButtonGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    if (checkedRadioButton.equals(firstButton)) {
                        if (firstButtonAction != null) {
                            firstButtonAction.run();
                        }

                    }

                    if (checkedRadioButton.equals(secondButton)) {
                        if (secondButtonAction != null) {
                            secondButtonAction.run();
                        }

                    }

                    if (checkedRadioButton.equals(thirdButton)) {

                        if (thirdButtonAction != null) {
                            thirdButtonAction.run();

                        }

                    }

                }


            }
        });


    }

    public enum ButtonPosition {
        FIRST, SECOND, THIRD
    }


    protected enum ButtonGroupType {
        THREE,
        TWO
    }
}
