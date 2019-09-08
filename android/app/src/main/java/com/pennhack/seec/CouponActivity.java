package com.pennhack.seec;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.GsonBuilder;

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

public class CouponActivity extends AppCompatActivity {

    String URL = "https://seecseec.appspot.com";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    RecyclerView marketRecycler;
    MyCouponsAdapter myCouponsAdapter;
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 2);

        client = new OkHttpClient();

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
                .url(URL+"/get-customer-coupons")
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
                        Toast.makeText(CouponActivity.this, "FAILED req", Toast.LENGTH_SHORT).show();
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

                    Coupon[] coupons = new GsonBuilder().create().fromJson(response.body().string(), Coupon[].class);
//                    CustomerData data = new GsonBuilder().create().fromJson(response.body().string(), CustomerData.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupRecyclerView(coupons);
                        }
                    });

                }
            }
        });



    }

    private void setupRecyclerView(Coupon[] coupons) {

        List<Coupon> couponList = Arrays.asList(coupons);

        marketRecycler = findViewById(R.id.market_recycler_view);

        myCouponsAdapter = new MyCouponsAdapter(couponList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        marketRecycler.setLayoutManager(layoutManager);
        marketRecycler.setItemAnimator(new DefaultItemAnimator());
        marketRecycler.setAdapter(myCouponsAdapter);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                Coupon coupon = couponList.get(position);
                AlertDialog alerter = new AlertDialog.Builder(CouponActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Your coupon code")
                        .setMessage("This code is only valid one time: "+coupon.code)
                        .create();
                alerter.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alerter.dismiss();
                    }
                });
                alerter.show();
            }
        };
        myCouponsAdapter.setOnItemClickListener(onClickListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    intent = new Intent(CouponActivity.this, MainActivity.class);
                    break;
                case R.id.navigation_profile:
                    intent = new Intent(CouponActivity.this, ProfileActivity.class);
                    break;
                case R.id.navigation_coupons:

                    break;
                case R.id.navigation_market:
                    intent = new Intent(CouponActivity.this, MarketActivity.class);
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

}
