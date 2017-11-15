package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.parameters.Parameters;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
        final Button saveParametersBtn = findViewById(R.id.saveItemBtn);
        final Parameters loadedParameters = (Parameters) getExtra(Parameters.class);

        bonusPointsBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        bonusAmountBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        bonusAmountExchangeBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        bonusPointsExchangeBaseTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());

        if (loadedParameters != null) {

            bonusPointsBaseTxt.setText(String.valueOf(loadedParameters.getBasePoints()));
            bonusAmountBaseTxt.setText(String.valueOf(loadedParameters.getBaseAmount()));
            bonusAmountExchangeBaseTxt.setText(String.valueOf(loadedParameters.getBaseExchangeAmount()));
            bonusPointsExchangeBaseTxt.setText(String.valueOf(loadedParameters.getBaseExchangePoints()));

        }

        saveParametersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FieldValidationHelper.isEditTextValidated(bonusAmountBaseTxt) &&
                        FieldValidationHelper.isEditTextValidated(bonusPointsBaseTxt) &&
                        FieldValidationHelper.isEditTextValidated(bonusPointsExchangeBaseTxt) &&
                        FieldValidationHelper.isEditTextValidated(bonusAmountExchangeBaseTxt)) {

                    saveParametersBtn.setEnabled(false);
                    showBusyLoader(true);

                    float bonusAmountBase = Float.valueOf(bonusAmountBaseTxt.getText().toString().trim());
                    int bonusPointsBase = Integer.valueOf(bonusPointsBaseTxt.getText().toString().trim());
                    int bonusPointsExchangeBase = Integer.valueOf(bonusPointsExchangeBaseTxt.getText().toString().trim());
                    float  bonusAmountExchangeBase = Float.valueOf(bonusAmountExchangeBaseTxt.getText().toString().trim());

                   Parameters parameters = loadedParameters == null? new Parameters(): loadedParameters;
                   parameters.setBaseAmount(bonusAmountBase);
                   parameters.setBasePoints(bonusPointsBase);
                   parameters.setBaseExchangePoints(bonusPointsExchangeBase);
                   parameters.setBaseExchangeAmount(bonusAmountExchangeBase);


                    if (loadedParameters == null) {

                        firebaseDatabaseHelper.insert(parameters).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MessageHelper.show(ManageParametersActivity.this,
                                            MessageType.SUCCESS,
                                            "Paramêtros adicionados");
                                    saveParametersBtn.setEnabled(true);
                                    finish();
                                } else {
                                    MessageHelper.show(ManageParametersActivity.this,
                                            MessageType.ERROR,
                                            "Erro ao adicionar paramêtros");
                                    saveParametersBtn.setEnabled(true);
                                    showBusyLoader(false);
                                }
                            }
                        });
                    } else {

                        firebaseDatabaseHelper.update(parameters).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MessageHelper.show(ManageParametersActivity.this,
                                            MessageType.SUCCESS,
                                            "Paramêtros atualizados");
                                    saveParametersBtn.setEnabled(true);
                                    finish();
                                } else {
                                    MessageHelper.show(ManageParametersActivity.this,
                                            MessageType.ERROR,
                                            "Erro ao atualizar paramêtros");
                                    saveParametersBtn.setEnabled(true);
                                    showBusyLoader(false);
                                }
                            }
                        });
                    }

                }


            }
        });


    }
}
