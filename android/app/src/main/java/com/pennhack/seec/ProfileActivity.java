package com.pennhack.seec;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Helpers.getInstance().bottomNavigatior(this, mOnNavigationItemSelectedListener, 1);

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

}
