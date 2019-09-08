package com.pennhack.seec;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MarketActivity extends AppCompatActivity {

    String URL = "https://seecseec.appspot.com";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    RecyclerView marketRecycler;
    MarketAdapter marketAdapter;
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        client = new OkHttpClient();
        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 3);

//        Coupon[] coupons = new GsonBuilder().create().fromJson(sb, Coupon[].class);
//        setupRecyclerView(coupons);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("coupons");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Coupon> couponList = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    couponList.add(ds.getValue(Coupon.class));
                }
                setupRecyclerView(couponList.toArray(new Coupon[couponList.size()]));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setupRecyclerView(Coupon[] coupons) {

        List<Coupon> couponList = Arrays.asList(coupons);

        marketRecycler = findViewById(R.id.market_recycler_view);

        marketAdapter = new MarketAdapter(couponList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        marketRecycler.setLayoutManager(layoutManager);
        marketRecycler.setItemAnimator(new DefaultItemAnimator());
        marketRecycler.setAdapter(marketAdapter);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                final Coupon coupon = couponList.get(position);

                AlertDialog dialog = new AlertDialog.Builder(MarketActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Confirmation of Purchase")
                        .setMessage("Are you sure you want to purchase this coupon for "+coupon.price+" credits?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                JSONObject payload = new JSONObject();
                                try {
                                    payload.accumulate("cust_ID", getPreferences(MODE_PRIVATE).getString("custId", null));
                                    payload.accumulate("id", coupon. id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Request request = new Request.Builder()
                                        .url(URL+"/make-purchase")
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
                                                Toast.makeText(MarketActivity.this, "FAILED req", Toast.LENGTH_SHORT).show();
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
                                            startActivity(new Intent(MarketActivity.this, CouponActivity.class));
                                        }
                                    }
                                });

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                dialog.show();

            }
        };
        marketAdapter.setOnItemClickListener(onClickListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    intent = new Intent(MarketActivity.this, MainActivity.class);
                    break;
                case R.id.navigation_profile:
                    intent = new Intent(MarketActivity.this, ProfileActivity.class);
                    break;
                case R.id.navigation_coupons:
                    intent = new Intent(MarketActivity.this, CouponActivity.class);
                    break;
                case R.id.navigation_market:

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
