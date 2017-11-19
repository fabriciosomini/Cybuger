package com.cynerds.cyburger.components;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.models.general.MessageType;

/**
 * Created by fabri on 25/10/2017.
 */

public class MessageToast extends ConstraintLayout {
    private View view;
    private TextView toastTextView;
    private String text;

    public MessageToast(Context context) {
        super(context);

        initializeViews(context);

    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.component_toast, this);
        toastTextView = (TextView) view.findViewById(R.id.toastTextView);
    }



    public void setText(String text) {

        toastTextView.setText(text);
    }

    public void setType(MessageType messageType) {

        View innerContainer = view.findViewById(R.id.toastInnerContainer);
        switch (messageType) {

            case ERROR:
                innerContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.coralred));
                break;

            case SUCCESS:
                innerContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
                break;

            case INFO:
                innerContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.redishYellow));
                break;
        }
    }
}
