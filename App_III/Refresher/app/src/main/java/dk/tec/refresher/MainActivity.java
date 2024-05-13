package dk.tec.refresher;

import static android.Manifest.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dk.tec.refresher.Model.ToiletAdapter;
import dk.tec.refresher.Model.ToiletLocation;

public class MainActivity extends AppCompatActivity implements ToiletAdapter.OnDeleteListener {

    public static List<ToiletLocation> toilets;
    Location location;
    FusedLocationProviderClient flpc;
    TextView txt_lon, txt_lat, txt_alt, txt_dir, proximity_warning;
    ListView listToilets;

    ToiletAdapter toiletAdapter;

    private static final double DISTANCE_THRESHOLD_METERS = 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize GUI elements
        initGui();

        // Initialize permissions list
        ArrayList<String> permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));

        // Request necessary permissions
        askForPermissions(permissionsList);

        // Initialize FusedLocationProviderClient
        flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        // Initialize food list and set button click listener
        toilets = new ArrayList<>();
        ReadToiletList();

        listToilets = findViewById(R.id.listToilets);
        toiletAdapter = new ToiletAdapter(this, toilets, this);
        listToilets.setAdapter(toiletAdapter);

        //mrButton.setOnClickListener(view -> foodlist.add(new Food(input.getText().toString())));
    }

    // Method to check if the current location is within a certain distance of any of the listed items
    private int isLocationNearToilet(Location currentLocation) {
        for (int i = 0; i < toilets.size(); i++) {
            ToiletLocation toiletLocation = toilets.get(i);
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                    toiletLocation.getLatitude(), toiletLocation.getLongitude(), results);
            double distance = results[0];

            if (distance <= DISTANCE_THRESHOLD_METERS) {
                // Current location is within the threshold distance of a toilet location
                return i; // Return the index of the toilet
            }
        }
        // Current location is not within the threshold distance of any toilet location
        return -1; // Return -1 if no toilet is within the threshold distance
    }

    // Initialize GUI elements
    private void initGui() {
        txt_lat = findViewById(R.id.txt_lat);
        txt_lon = findViewById(R.id.txt_lon);
        txt_alt = findViewById(R.id.txt_alt);
        txt_dir = findViewById(R.id.txt_dir);
        proximity_warning = findViewById(R.id.proximityWarning);

        findViewById(R.id.btn_savetoiletlocation).setOnClickListener(view -> {
            new ToiletLocation(location);
            SaveToiletList();
            toiletAdapter.notifyDataSetChanged();
        });
    }

    void SaveToiletList() {
        SharedPreferences sharedPref = getSharedPreferences("Spoilets",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();

        String json = gson.toJson(toilets);
        editor.putString("toiletList", json);
        editor.apply();
    }

    void ReadToiletList() {
        SharedPreferences sharedPrefs = getSharedPreferences("Spoilets", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPrefs.getString("toiletList", "");
        Type type = new TypeToken<List<ToiletLocation>>() {}.getType();
        List<ToiletLocation> tempToiletList = gson.fromJson(json, type);
        if (tempToiletList != null) {
            toilets = tempToiletList;
        }
    }
    public void onDelete(int position) {
        // Handle delete action here
        toilets.remove(position);
        toiletAdapter.notifyDataSetChanged();
        SaveToiletList();
    }

    // Request location updates
    private void getUpdates() {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


                // Update UI with location information
                txt_lat.setText("Latitude: " + location.getLatitude());
                txt_lon.setText("Longitude: " + location.getLongitude());
                txt_alt.setText("Altitude: " + location.getAltitude());
                txt_dir.setText("Direction: " + location.getBearing());

                int proximity = isLocationNearToilet(location);
                Log.d("Proximity", String.valueOf(proximity));
                if (proximity > -1) {
                    Toast.makeText(MainActivity.this, String.format("Near toilet #%s", proximity), Toast.LENGTH_LONG).show();

                }

            }
        }, Looper.myLooper());
    }

    // Permissions handling
    ArrayList<String> permissionsList;
    String[] permissionsStr = {
            permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION,
            permission.CAMERA,
            permission.RECORD_AUDIO
    };

    // Register permission request launcher
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

    // Check if permission is granted
    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions
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

    // Show permission dialog
    AlertDialog alertDialog;
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
}
