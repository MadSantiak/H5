package com.example.spoiletown.toilet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.spoiletown.controllers.ApiLayer;
import com.example.spoiletown.R;

import java.io.Serializable;
import java.util.List;

public class AddToiletActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtId;
    CheckBox isFavorite;
    Button btnCreate, btnBack;
    Toilet t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_toilet);

        txtId = findViewById(R.id.txtId);
        isFavorite = findViewById(R.id.isFavorite);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);

        btnCreate.setOnClickListener(this);
        btnBack.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == btnCreate) {
            boolean favorite = isFavorite.isChecked();

            t = new Toilet(favorite);
            Integer i = ApiLayer.addToilet(t);

            txtId.setText(i.toString());

        }
        else if (v == btnBack) {
            Intent intent = new Intent();
            if (t != null) {
                intent.putExtra("newToilet", (Serializable) p);
                setResult(Activity.RESULT_OK, intent);
            }
            else {
                setResult(Activity.RESULT_CANCELED, intent);
            }
            finish();
        }

    }
}