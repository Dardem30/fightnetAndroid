package com.roman.fightnet.ui.activities.profileActivities;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.roman.fightnet.R;
import com.roman.fightnet.databinding.ActivityProfileBinding;
import com.roman.fightnet.requests.models.AppUser;
import com.roman.fightnet.requests.models.Invite;
import com.roman.fightnet.requests.service.UserService;
import com.roman.fightnet.requests.service.util.UtilService;
import com.roman.fightnet.ui.activities.profileActivities.fragments.FightFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.InviteFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.MapFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.MessageFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.OverviewFragment;
import com.roman.fightnet.ui.activities.profileActivities.fragments.SearchFragment;
import com.tiper.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.roman.fightnet.IConstants.fightStyles;
import static com.roman.fightnet.IConstants.storage;

public class ProfileActivity extends AppCompatActivity {

    private UserService userService = UtilService.getUserService();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Toolbar toolbar;
    private Fragment fragment = new OverviewFragment();
    private MenuItem menuItem;
    private String timeForFight;
    private String dateForFight;
    private ActivityProfileBinding activityProfileBinding;
    private Invite invite = new Invite();
    private double latitude;
    private double longitude;
    private String preferredKind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.activity_profile);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        NavigationView navigationView = findViewById(R.id.nv);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.navigation_menu);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.overview:
                    fragment = new OverviewFragment();
                    break;
                case R.id.message:
                    fragment = new MessageFragment();
                    break;
                case R.id.search:
                    fragment = new SearchFragment();
                    break;
                case R.id.map:
                    fragment = new MapFragment();
                    break;
                case R.id.invites:
                    fragment = new InviteFragment();
                    break;
                case R.id.fights:
                    fragment = new FightFragment();
                    break;
                default:
                    return true;
            }
            item.setChecked(true);
            if (menuItem != null) {
                menuItem.setChecked(false);
            }
            menuItem = item;
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            drawerLayout.closeDrawers();
            return true;
        });
        final MaterialSpinner preferableFightStyleSpinner = findViewById(R.id.fightStyleInvite);
        preferableFightStyleSpinner.setAdapter(UtilService.setupStringAdapter(fightStyles, this));
        preferableFightStyleSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner materialSpinner) {

            }

            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, View view, int position, long l) {
                preferredKind = fightStyles.get(position);

            }
        });

        final EditText dateTimeField = findViewById(R.id.fightDate);
        @SuppressLint("SetTextI18n")
        final CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = new CalendarDatePickerDialogFragment()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setDoneText("Select")
                .setCancelText("Cancel")
                .setOnDateSetListener((dialog, year, monthOfYear, dayOfMonth) -> setDateForFight(year + "-" + (monthOfYear > 9 ? monthOfYear : "0" + monthOfYear) + "-" + (dayOfMonth > 9 ? dayOfMonth : "0" + dayOfMonth)))
                .setOnDismissListener(dialogInterface -> new TimePickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .addTimePickerDialogHandler((reference, hourOfDay, minute) -> {
                            setTimeForFight((hourOfDay > 9 ? String.valueOf(hourOfDay) : "0" + hourOfDay) + ":" + (minute > 9 ? String.valueOf(minute) : "0" + minute));
                            dateTimeField.setText(getDateForFight() + " " + getTimeForFight());
                        })
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .show());
        dateTimeField.setOnClickListener(v -> calendarDatePickerDialogFragment.show(getSupportFragmentManager(), "Invite calendar"));
        findViewById(R.id.createInvite).setOnClickListener(v -> {
            invite.setLatitude(latitude);
            invite.setLongitude(longitude);
            invite.setFightStyle(preferredKind);
            final String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date());
            invite.setDate(getDateForFight() + "T" + getTimeForFight() + ":00.000" + date.substring(date.length() - 5));
            userService.invite(invite, storage.getToken());
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.invite) {
            drawerLayout.openDrawer(findViewById(R.id.nv_invite));
        } else if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public String getTimeForFight() {
        return timeForFight;
    }

    public void setTimeForFight(String timeForFight) {
        this.timeForFight = timeForFight;
    }

    public String getDateForFight() {
        return dateForFight;
    }

    public void setDateForFight(String dateForFight) {
        this.dateForFight = dateForFight;
    }

    public void initInvite(final AppUser invitedUser) {
        toolbar.getMenu().findItem(R.id.invite).setVisible(true);
        invite.setFighterInvited(invitedUser);
        invite.setFighterInviter(new AppUser(storage.getEmail(), storage.getUserName(), storage.getUserSurName()));
        invite.setAccepted(false);
        activityProfileBinding.setInvite(invite);
    }
    public void setCoordinate(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
