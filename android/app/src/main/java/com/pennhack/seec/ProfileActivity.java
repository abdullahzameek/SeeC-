package com.pennhack.seec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    String URL = "https://seecseec.appspot.com";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 1);

        JSONObject payload = new JSONObject();
        try {
            String custId = getApplicationContext().getSharedPreferences("ABC", 0).getString("custId", null);
            Log.i("cust", custId);
            payload.accumulate("cust_ID", custId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("pay", payload.toString());

        Request request = new Request.Builder()
                .url(URL+"/get-customer-data")
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
                        Toast.makeText(ProfileActivity.this, "FAILED req", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    Log.i("res", "not success response");
                }
                else {
                    Log.i("res", "success response");

                }
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    intent = new Intent(ProfileActivity.this, MainActivity.class);
                    break;
                case R.id.navigation_profile:

                    break;
                case R.id.navigation_coupons:
                    intent = new Intent(ProfileActivity.this, CouponActivity.class);
                    break;
                case R.id.navigation_market:
                    intent = new Intent(ProfileActivity.this, MarketActivity.class);
                    break;

            }
            if (intent!=null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        }
    };

    public void signOutUser(View view) {

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileActivity.this, "Signed Out!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
