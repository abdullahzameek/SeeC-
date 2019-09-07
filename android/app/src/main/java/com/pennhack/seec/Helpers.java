package com.pennhack.seec;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

class Helpers {
    private static final Helpers ourInstance = new Helpers();

    static Helpers getInstance() {
        return ourInstance;
    }

    private Helpers() {
    }

    public void bottomNavigatior(Activity activity, BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener, int pos){
        BottomNavigationView navView = activity.findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Menu menu = navView.getMenu();
        MenuItem item = menu.getItem(pos);
        item.setChecked(true);
    }
}
