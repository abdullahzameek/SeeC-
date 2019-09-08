package com.pennhack.seec;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class CouponActivity extends AppCompatActivity {

    String sb = "[" +
            "        {" +
            "            \"id\":1," +
            "            \"code\":\"gfJgefnk859\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/kayak%20Cropped.png?alt=media&token=1d8e76a9-a264-4f2c-8fce-37eb367baa1e\"," +
            "            \"message\":\"20% off Kayaktour\"," +
            "            \"price\":43," +
            "            \"vendor\":\"KayakAdventure\"" +
            "        }," +
            "        {" +
            "            \"id\":2," +
            "            \"code\":\"gsjhs83HJD6\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/smoothie%20Cropped.jpg?alt=media&token=ba36cdd8-1fa8-4df3-8576-0ba807a65911\"," +
            "            \"message\":\"Buy 1=2!\"," +
            "            \"price\":25," +
            "            \"vendor\":\"JuiceBar\"" +
            "        }," +
            "        {" +
            "            \"id\":3," +
            "            \"code\":\"k568IgFTG47\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/subway1%20Cropped.jpg?alt=media&token=0f493653-2319-4ca4-bd63-d2574a27f498\"," +
            "            \"message\":\"1 free ride\"," +
            "            \"price\":10," +
            "            \"vendor\":\"MTR\"" +
            "        }," +
            "        {" +
            "            \"id\":4," +
            "            \"code\":\"kstu56K8bet\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/subway1%20Cropped.jpg?alt=media&token=0f493653-2319-4ca4-bd63-d2574a27f498\"," +
            "            \"message\":\"50% off next ride\"," +
            "            \"price\":8," +
            "            \"vendor\":\"MTR\"" +
            "        }," +
            "        {" +
            "            \"id\":5," +
            "            \"code\":\"ki98GTB56fg\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/starbucks%20Cropped.jpg?alt=media&token=d940e2e0-0850-423b-b95d-53cb1cb6eb9a\"," +
            "            \"message\":\"30% off\"," +
            "            \"price\":11," +
            "            \"vendor\":\"Starbucks\"" +
            "        }," +
            "        {" +
            "            \"id\":6," +
            "            \"code\":\"kti83gbHY46\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/garden%20Cropped.jpg?alt=media&token=df261f91-005f-4e24-92c5-cc7d9e309c45\"," +
            "            \"message\":\"Free GardFest entrance\"," +
            "            \"price\":35," +
            "            \"vendor\":\"GardFest\"" +
            "        }," +
            "        {" +
            "            \"id\":7," +
            "            \"code\":\"kgiebs893hU\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/bagle%20Cropped.jpg?alt=media&token=60f7dd1c-c2e1-4660-97d7-4fec9b1ba442\"," +
            "            \"message\":\"Free breakfast bagel\"," +
            "            \"price\":19," +
            "            \"vendor\":\"BagelTruck\"" +
            "        }," +
            "        {" +
            "            \"id\":8," +
            "            \"code\":\"koe67BW17uD\"," +
            "            \"image\":\"https://firebasestorage.googleapis.com/v0/b/seec-pennapps.appspot.com/o/marathon%20Cropped%20(1).jpg?alt=media&token=370314a8-2736-414a-81e1-0410f2f655a5\"," +
            "            \"message\":\"Free participation\"," +
            "            \"price\":50," +
            "            \"vendor\":\"MRTH\"" +
            "        }" +
            "    ]";

    RecyclerView marketRecycler;
    MyCouponsAdapter myCouponsAdapter;
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 2);

        Coupon[] coupons = new GsonBuilder().create().fromJson(sb, Coupon[].class);
        setupRecyclerView(coupons);

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
