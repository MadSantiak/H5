package com.example.gowork;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gowork.Controllers.ConfigHelper;
import com.example.gowork.Controllers.WeatherApiService;
import com.example.gowork.Controllers.WorkperiodController;
import com.example.gowork.Controllers.WorkplaceController;
import com.example.gowork.Model.WeatherResponse.WeatherResponse;
import com.example.gowork.Model.Workperiod.Workperiod;
import com.example.gowork.Model.Workperiod.WorkperiodAdapter;
import com.example.gowork.Model.Workplace.AddWorkplaceActivity;
import com.example.gowork.Model.Workplace.Workplace;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.*;

public class MainActivity extends AppCompatActivity implements SpeedDialView.OnActionSelectedListener {

    //region Permission variables
    AlertDialog alertDialog;
    String[] permissionsStr = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    ArrayList<String> permissionsList = new ArrayList<>(Arrays.asList(permissionsStr));
    //endregion

    //region View variables
    Button btnWork;
    TextView txtDistanceToWork, txtCurrentTemperature, txtWorkplaceName;
    ListView listWorkPeriods;
    FloatingActionButton fabAddPeriod;
    SpeedDialView spdMenu;
    //endregion

    //region Fields
    WorkperiodAdapter workPeriodAdapter;
    List<Workperiod> periods = new ArrayList<>();
    //! Assign standard value
    Workplace workplace = new Workplace(1, "Default", 8.69491F, 56.95523F);
    private double latitude;
    private double longitude;
    //endregion

    //region GPS variables
    FusedLocationProviderClient flpc;
    Location location;
    //endregion

    //region Weather threads
    private HandlerThread weatherThread;
    private Handler weatherHandler;
    //endregion

    //region ActivityLaunchers
    ActivityResultLauncher<Intent> addWorkplaceActivityLauncher;
    //endregion

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

        Workplace tempWorkplace = WorkplaceController.getWorkplaceById(1);
        workplace = (tempWorkplace != null) ? tempWorkplace : workplace;
        WorkplaceController.addWorkplace(workplace);

        periods.addAll(WorkperiodController.getAllWorkperiod());
        workPeriodAdapter = new WorkperiodAdapter(this, periods);
        workPeriodAdapter.setOnStopClickListener(new WorkperiodAdapter.OnStopClickListener() {
            @Override
            public void onStopClick(int position) {
                Workperiod workPeriod = periods.get(position);
                workPeriod.setStopTime(new Date());
                workPeriodAdapter.notifyDataSetChanged();
            }
        });
        workPeriodAdapter.setOnDeleteClickListener(new WorkperiodAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Workperiod workPeriod = periods.get(position);
                WorkperiodController.delWorkperiod(workPeriod.getId());
                periods.remove(workPeriod);
                workPeriodAdapter.notifyDataSetChanged();
            }
        });

        //! Initialize permissions list for checking.
        askForPermissions(permissionsList);

        //! Initialize GUI
        initGui();

        //! Initialize Weather thread with a delay of 6000
        weatherThread = new HandlerThread("WeatherThread");
        weatherThread.start();
        weatherHandler = new Handler(weatherThread.getLooper());
        weatherHandler.postDelayed(weatherUpdateRunnable, 6000);

        /**
         * Activity launchers so we can fetch/respond to data changes
         */
        addWorkplaceActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent res = o.getData();
                            workplace = (Workplace) res.getSerializableExtra("newWP");

                            txtWorkplaceName.setText(workplace.getName());
                        }
                    }
                }
        );
    }

    /**
     * Initialize GUI by assinging elements to variables:
     */
    private void initGui() {
        btnWork = findViewById(R.id.btnWork);
        btnWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date startTime = new Date();
                Workperiod workPeriod = new Workperiod(latitude, longitude, startTime);
                WorkperiodController.addWorkperiod(workPeriod);
                periods.add(workPeriod);
                workPeriodAdapter.notifyDataSetChanged();
            }
        });

        txtWorkplaceName = findViewById(R.id.txtWorkplaceName);
        txtWorkplaceName.setText(workplace.getName());
        txtDistanceToWork = findViewById(R.id.txtDistanceToWork);
        txtCurrentTemperature = findViewById(R.id.txtCurrentTemperature);
        listWorkPeriods = findViewById(R.id.listWorkPeriods);
        listWorkPeriods.setAdapter(workPeriodAdapter);
        spdMenu = findViewById(R.id.spdMenu);
        spdMenu.addActionItem(new SpeedDialActionItem.Builder(R.id.itemAddWorkplace, R.drawable.account_plus)
                .create());
        spdMenu.setOnActionSelectedListener(this);
    }

    /**
     * Runnable (thread) that calls the getWeatherUpdates() function to fetch current temperature.
     * Before calling itself again, with a delay of 1 minute.
     */
    private Runnable weatherUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            getWeatherUpdates();
            weatherHandler.postDelayed(this, 60000);
        }
    };

    /**
     * Function that gets called every minute (or after 6 seconds on startup)
     * Makes an API call using HTTPS to avoid including our API Key in the call
     * And on succesful response, writes out the current temperature for the location
     * the user is currently at, fetched via getUpdates().
     */
    private void getWeatherUpdates() {
        String apiKey = ConfigHelper.getApiKey(this);

        // Abort it API key is unknown.
        if (apiKey == null) {
            txtCurrentTemperature.setText("Failed to get API Key");
            return;
        }
        // Abort if location isn't known.
        if (location == null) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/") //! Use HTTPS so we don't spill the beans on our API Key.
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService weatherApiService = retrofit.create(WeatherApiService.class);

        weatherApiService.getCurrentWeather(location.getLatitude(), location.getLongitude(), apiKey, "metric")
                .enqueue(new Callback<WeatherResponse>() {
                        @Override
                        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                double temperature = response.body().getMainInfo().getTemperature();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtCurrentTemperature.setText(temperature + "°C");
                                    }
                                });
                            } else {
                                txtCurrentTemperature.setText("");
                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherResponse> call, Throwable t) {
                            Log.e("WeatherAPI", "Failed to fetch weather data: " + t.getMessage(), t);
                            txtCurrentTemperature.setText("");
                        }
                    });
    }



    //region Permission handling
    /**
     * Checks whether permissions has been granted or not.
     * @param context
     * @param permissionStr
     * @return
     */
    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permission based on the result of the Activity (permissionLauncher)
     * @param permissionsList
     */
    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        newPermissionStr = permissionsList.toArray(newPermissionStr);
        if (newPermissionStr.length > 0) {
            // Launch permission request
            permissionsLauncher.launch(newPermissionStr);
        } else {
            // Show dialog to guide user to app settings for enabling permissions
            showPermissionDialog();
        }
    }

    /**
     * Displays an "error" (alert dialog) if permissions are insufficient.
     */
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are needed to be allowed to use this app without any problems.")
                .setPositiveButton("Continue", (dialog, which) -> {
                    dialog.dismiss();
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            int permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                } else if (!hasPermission(MainActivity.this, permissionsStr[i])) {
                                    permissionsCount++;
                                }
                            }
                            if (!permissionsList.isEmpty()) {
                                // Request permissions again for ones that were denied
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                // Show permission dialog for permissions that cannot be requested again
                                showPermissionDialog();
                            } else {
                                // All permissions granted, proceed with necessary actions
                                getUpdates();
                            }
                        }
                    });

    /**
     * Function that, if permissions allow, continuously fetches GPS data.
     */
    private void getUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, handle it here or request permission again
            return;
        }

        flpc = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000).build();

        flpc.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                location = locationResult.getLastLocation();
                float[] results = new float[1];
                if (workplace != null && location != null) {
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            workplace.getLatitude(), workplace.getLongitude(), results);

                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
                double distance = results[0];
                String formattedDistance = String.format("%.2f", distance/1000);
                txtDistanceToWork.setText(formattedDistance + "km");
            }
        }, Looper.myLooper());
    }
    //endregion


    /**
     * We make sure to cancel the thread once we close ("destory") the application.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (weatherThread != null) {
            // Quit the weather thread
            weatherThread.quit();
            weatherThread = null;
            weatherHandler = null;
        }
    }

    @Override
    public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
        if (speedDialActionItem.getId() == R.id.itemAddWorkplace) {
            Intent intent = new Intent(MainActivity.this, AddWorkplaceActivity.class);
            intent.putExtra("currentWP", workplace);
            addWorkplaceActivityLauncher.launch(intent);
        }
        return true;
    }
}