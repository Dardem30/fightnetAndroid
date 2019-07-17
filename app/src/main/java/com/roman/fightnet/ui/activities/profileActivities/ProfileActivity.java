package com.roman.fightnet.ui.activities.profileActivities;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.roman.fightnet.R;
import com.roman.fightnet.ui.activities.profileActivities.fragments.OverviewFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.SearchFragment;

public class ProfileActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragment = new OverviewFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        DrawerLayout drawerLayout = findViewById(R.id.activity_profile);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        NavigationView navigationView = findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.overview:
                    fragment = new OverviewFragment();
                case R.id.message:
                    Toast.makeText(ProfileActivity.this, "message", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.search:
                    fragment = new SearchFragment();
                case R.id.map:
                    Toast.makeText(ProfileActivity.this, "map", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return true;
            }

            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            item.setChecked(true);
            return true;
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
