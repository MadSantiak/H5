package com.example.spoiletown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spoiletown.controllers.ApiLayer;
import com.example.spoiletown.toilet.AddToiletActivity;
import com.example.spoiletown.toilet.Toilet;
import com.example.spoiletown.toilet.ToiletAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listToilets;
    ToiletAdapter toiletAdapter;
    List<Toilet> toilets = new ArrayList<>();

    Button btnAddToilet;

    ActivityResultLauncher<Intent> addToiletActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listToilets = findViewById(R.id.listToilet);
        toilets = ApiLayer.getAllToilet();

        if (toilets == null) {
            toilets = new ArrayList<>();
        }

        addToiletActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Intent res = o.getData();
                            Toilet p = (Toilet) res.getSerializableExtra("newToilet");
                            toilets.add(p);
                            toiletAdapter.notifyDataSetChanged();
                        }
                    }
                });

        toiletAdapter = new ToiletAdapter(toilets, this);
        listToilets.setAdapter(toiletAdapter);

        btnAddToilet = findViewById(R.id.btnAddToilet);
        btnAddToilet.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddToiletActivity.class);
            addToiletActivityLauncher.launch(intent);
        });


    }

}