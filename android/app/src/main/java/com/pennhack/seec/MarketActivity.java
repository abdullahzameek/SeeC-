package com.pennhack.seec;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MarketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 3);

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
