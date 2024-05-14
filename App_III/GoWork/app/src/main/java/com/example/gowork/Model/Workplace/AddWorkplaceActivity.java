package com.example.gowork.Model.Workplace;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gowork.Controllers.WorkplaceController;
import com.example.gowork.R;

import java.io.Serializable;
import java.util.List;

public class AddWorkplaceActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtWorkplaceName, txtWorkplaceLongitude, txtWorkplaceLatitude;
    Button btnCreateWorkplace, btnBackWorkplace;
    Workplace wp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workplace);

        wp = (Workplace) getIntent().getSerializableExtra("currentWP");

        initGui();

    }

    void initGui() {
        txtWorkplaceName = findViewById(R.id.txtWorkplaceName);
        txtWorkplaceLongitude = findViewById(R.id.txtWorkplaceLongitude);
        txtWorkplaceLatitude = findViewById(R.id.txtWorkplaceLatitude);
        btnCreateWorkplace = findViewById(R.id.btnCreateWorkplace);
        btnBackWorkplace = findViewById(R.id.btnBackWorkplace);

        txtWorkplaceName.setText(wp.getName());
        txtWorkplaceLongitude.setText(String.valueOf(wp.getLongitude()));
        txtWorkplaceLatitude.setText(String.valueOf(wp.getLatitude()));

        btnCreateWorkplace.setOnClickListener(this);
        btnBackWorkplace.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCreateWorkplace) {
            double longitude;
            double latitude;
            String name = txtWorkplaceName.getText().toString();
            String strLat = txtWorkplaceLatitude.getText().toString();
            String strLong = txtWorkplaceLongitude.getText().toString();


            //! Check if string is empty, if so, use current location passed from MainActivity.
            // Nearly superfluous at this point, but keep in to avoid user error (deleting a coordinate and not entering a new one.)
            latitude = (!strLat.isEmpty()) ? Double.parseDouble(strLat) : wp.getLatitude();
            longitude = (!strLat.isEmpty()) ? Double.parseDouble(strLong) : wp.getLongitude();


            wp.setLatitude(latitude);
            wp.setLongitude(longitude);
            wp.setName(name);

            Integer i = WorkplaceController.addWorkplace(wp);
        } else if (v == btnBackWorkplace) {
            Intent intent = new Intent();
            if (wp != null) {
                intent.putExtra("newWP", (Serializable) wp);
                setResult(Activity.RESULT_OK, intent);
            } else {
                setResult(Activity.RESULT_CANCELED, intent);
            }
            finish();
        }
    }
}
