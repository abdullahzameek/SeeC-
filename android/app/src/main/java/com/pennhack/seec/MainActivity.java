package com.pennhack.seec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final int RC_SIGN_IN = 123;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView resultText, locationText;

    BarcodeDetector barcodeDetector;

    FusedLocationProviderClient locationProviderClient;

    OkHttpClient client;

    LocationManager locationManager;
    String provider;

    double currentLat, currentLon;

    String URL = "https://navyasuri.pythonanywhere.com";

    OnSuccessListener successfulLocationReceived = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            if (location == null)
                Toast.makeText(MainActivity.this, "location null", Toast.LENGTH_SHORT).show();
            else {
                String displayString = "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude();
                locationText.setText(displayString);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }

        client = new OkHttpClient();
        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 0);


        surfaceView = findViewById(R.id.camera_surface_view);
        resultText = findViewById(R.id.result_text);
        locationText = findViewById(R.id.location_text);

        enableLocationSettings();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        provider = locationManager.getBestProvider(criteria, true);
        if (provider == null)
            Toast.makeText(this, "No location provider", Toast.LENGTH_SHORT).show();

        else {
            locationManager.requestLocationUpdates(provider,
                    1000, 5, this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(480, 480)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 1001);
                    return;
                }

                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    resultText.setText(qrCodes.valueAt(0).displayValue);

                    String coords = qrCodes.valueAt(0).displayValue;
                    double lat, lon;
                    String[] coordsSplit = coords.split("#");
                    lat = Double.parseDouble(coordsSplit[0]);
                    lon = Double.parseDouble(coordsSplit[1]);

                    Location location = getLastKnownLocation();
                    final double dist = distance(lat, location.getLatitude(),lon,  location.getLongitude());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, Math.round(Math.round(dist)) + " is the dist", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    lat = coords.split("#");
                }

            }
        });

    }

    private void enableLocationSettings() {

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse response) {
                        // startUpdatingLocation(...);
//                        Log.i("blue", "onSuccess yay");
//                        locationProviderClient.getLastLocation().addOnSuccessListener(successfulLocationReceived);
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = getLastKnownLocation();
//                        currentLat = location.getLatitude();
//                        currentLon = location.getLongitude();
                        if (location != null) {
                            String displayString = "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude();
                            locationText.setText(displayString);
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        if (ex instanceof ResolvableApiException) {
                            // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                            try {
//                                Toast.makeText(MainActivity.this, "Location failure", Toast.LENGTH_SHORT).show();
                                ResolvableApiException resolvable = (ResolvableApiException) ex;
                                resolvable.startResolutionForResult(MainActivity.this, 1007);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;

            case 1002: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1007:
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = getLastKnownLocation();
                String displayString = "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude();
                locationText.setText(displayString);
                break;

            case RC_SIGN_IN:
                IdpResponse response = IdpResponse.fromResultIntent(data);



                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    JSONObject payload = new JSONObject();
                    try {
                        payload.accumulate("first_name", user.getDisplayName());
                        payload.accumulate("email", user.getEmail());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("ho", payload.toString());
                    Request request = new Request.Builder()
                            .url(URL+"/create-new-customer")
                            .header("Content-Type", "application/json")
                            .post(RequestBody.create(payload.toString(), JSON))
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "FAILED req", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if(!response.isSuccessful()){

                            }
                            else {
                                String custId = response.body().string();
                                Toast.makeText(MainActivity.this, custId, Toast.LENGTH_SHORT).show();
                                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                                prefs.edit()
                                        .putString("custId", custId)
                                        .apply();
                            }
                        }
                    });
//                    Toast.makeText(this, "Hello " + user.getDisplayName(), Toast.LENGTH_SHORT).show();



                    // ...
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    // ...
                }
                break;
        }

    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

//        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return distance;
//        return Math.sqrt(distance);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "yo", Toast.LENGTH_SHORT).show();
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        String displayString = "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude();
        locationText.setText(displayString);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.navigation_scan:

                    break;
                case R.id.navigation_profile:
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    break;
                case R.id.navigation_coupons:
                    intent = new Intent(MainActivity.this, CouponActivity.class);
                    break;
                case R.id.navigation_market:
                    intent = new Intent(MainActivity.this, MarketActivity.class);
                    break;

            }
            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        }
    };

    public void locationCheck(View view) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = getLastKnownLocation();
        if (location != null)
            Toast.makeText(this, location.toString() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
    }


    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
