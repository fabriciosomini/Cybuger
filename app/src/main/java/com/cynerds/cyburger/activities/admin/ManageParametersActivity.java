package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.models.parameters.Parameters;

public class ManageParametersActivity extends BaseActivity {


    private final FirebaseDatabaseHelper<Parameters> firebaseDatabaseHelper;

    public ManageParametersActivity(){
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this, Parameters.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_parameters);

        setUIEvents();
    }

    private void setUIEvents() {


        final EditText bonusAmountBaseTxt = findViewById(R.id.bonusAmountBaseTxt);
        final EditText bonusPointsBaseTxt = findViewById(R.id.bonusPointsBaseTxt);
        final EditText bonusAmountExchangeBaseTxt = findViewById(R.id.bonusAmountExchangeBaseTxt);
        final EditText bonusPointsExchangeBaseTxt = findViewById(R.id.bonusPointsExchangeBaseTxt);
        final Button saveItemBtn = findViewById(R.id.saveItemBtn);
        final Parameters loadedItem = (Parameters) getExtra(Parameters.class);

        bonusPointsBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        bonusAmountBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        bonusAmountExchangeBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        bonusPointsExchangeBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());

        if (loadedItem != null) {

            bonusPointsBaseTxt.setText(String.valueOf(loadedItem.getBasePoints()));
            bonusAmountBaseTxt.setText(String.valueOf(loadedItem.getBaseAmount()));
            bonusAmountExchangeBaseTxt.setText(String.valueOf(loadedItem.getBaseExchangeAmount()));
            bonusPointsExchangeBaseTxt.setText(String.valueOf(loadedItem.getBaseExchangePoints()));

        }

        saveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FieldValidationHelper.isEditTextValidated(bonusAmountBaseTxt) &&
                        FieldValidationHelper.isEditTextValidated(bonusPointsBaseTxt) &&
                        FieldValidationHelper.isEditTextValidated(bonusPointsExchangeBaseTxt) &&
                        FieldValidationHelper.isEditTextValidated(bonusAmountExchangeBaseTxt)) {

                    float bonusAmountBase = Float.valueOf(bonusAmountBaseTxt.getText().toString().trim());
                    int bonusPointsBase = Integer.valueOf(bonusPointsBaseTxt.getText().toString().trim());
                    int bonusPointsExchangeBase = Integer.valueOf(bonusPointsExchangeBaseTxt.getText().toString().trim());
                    float  bonusAmountExchangeBase = Float.valueOf(bonusAmountExchangeBaseTxt.getText().toString().trim());

                   Parameters parameters =new Parameters();
                   parameters.setBaseAmount(bonusAmountBase);
                   parameters.setBasePoints(bonusPointsBase);
                   parameters.setBaseExchangePoints(bonusPointsExchangeBase);
                   parameters.setBaseExchangeAmount(bonusAmountExchangeBase);


                    if (loadedItem == null) {

                        firebaseDatabaseHelper.insert(parameters);
                    } else {

                        firebaseDatabaseHelper.update(parameters);
                    }

                    finish();

                }


            }
        });


    }
}
