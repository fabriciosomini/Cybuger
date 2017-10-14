package com.cynerds.cyburger.helpers;

import android.support.annotation.StringRes;
import android.widget.EditText;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 14/10/2017.
 */

public class FieldValidationHelper {


    public static boolean isEditTextValidated(EditText editText) {

        return isEditTextValidated(editText, R.string.general_label_requiredfield);

    }

    public static boolean isEditTextValidated(EditText editText, @StringRes int stringRef) {

        if (editText != null) {
            if (editText instanceof EditText) {
                if (editText.getText().toString().isEmpty()) {

                    setFieldAsInvalid(editText, stringRef);
                } else {


                    setFieldAsValid(editText);
                    return true;
                }
            }
        }
        return false;
    }

    public static void setFieldAsInvalid(EditText editText) {

        editText.setError(editText.getContext().getString(R.string.general_label_requiredfield));
       /* GradientDrawable drawable = (GradientDrawable) editText.getBackground().getConstantState().newDrawable();
        drawable.setStroke(2, Color.RED); // set stroke width and stroke color
        editText.setBackground(drawable);*/
    }

    public static void setFieldAsInvalid(EditText editText, @StringRes int stringRef) {

        editText.setError(editText.getContext().getString(stringRef));
      /*  GradientDrawable drawable = (GradientDrawable) editText.getBackground().getConstantState().newDrawable();
        drawable.setStroke(2, Color.RED); // set stroke width and stroke color
        editText.setBackground(drawable);*/
    }

    public static void setFieldAsValid(EditText editText) {

        editText.setError(null);
       /* GradientDrawable drawable = (GradientDrawable) editText.getBackground().getConstantState().newDrawable();
        drawable.setStroke(2, ContextCompat.getColor(editText.getContext(), R.color.borderColor)); // set stroke width and stroke color
        editText.setBackground(drawable);*/


    }
}

