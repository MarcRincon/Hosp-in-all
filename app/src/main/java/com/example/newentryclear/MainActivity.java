package com.example.newentryclear;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;


import com.example.newentryclear.ui.hexadecimalgen.HexaFragment;
import com.example.newentryclear.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.example.newentryclear.ui.home.HomeFragment.isPlugged;
import static com.example.newentryclear.ui.home.HomeFragment.timeDisplay;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    static ImageView imageView;
    static TextView headerText;
    static TextView subheaderText;
    static SharedPreferences prefs;
    Integer userID;
    private HomeViewModel homeViewModel;
    DatabaseReference reff;
    DatabaseReference reffDevices;
    DatabaseReference reffDevicesWar;

    AlarmasMedic alarmasMedic;
    DeviceManager deviceManager;
    BatteryWarnings batteryWarnings;
    NavigationView navigationView;

    Context context;


    String tabletName;
    String username;
    String idDevice;
    Integer logComplete;

    webdb db_action;

    private AppBarConfiguration mAppBarConfiguration;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarmasMedic = new AlarmasMedic();
        deviceManager = new DeviceManager();
        batteryWarnings = new BatteryWarnings();
        //XAMPP
        db_action = new webdb();
        Context context = getApplicationContext();
        db_action.pruebas(context);

//batery and fix screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        registerReceiver(LowBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
// #####


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.imageLog);
        headerText = (TextView) header.findViewById(R.id.navHeader);
        subheaderText = (TextView) header.findViewById(R.id.navHeaderSub);
        prefs = this.getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        prefs.edit().putString("loggedIn", "notLogged").apply();

        context = this;
    }

    public void changeStatus(String status) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Conectado").apply();
        } else if (!isPlugged(this)) {
            prefs.edit().putString("chargerConnected", "Desconectado").apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tabletName = sharedPreferences.getString("tabletName", "Tablet B1");
        idDevice = sharedPreferences.getString("tabletID", "0");

        String latestAction = sharedPreferences.getString("latestAction", null);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        BatteryManager bm = (BatteryManager) this.getSystemService(BATTERY_SERVICE);
        assert bm != null;
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        reffDevices = FirebaseDatabase.getInstance().getReference().child("Devices Status").child(tabletName);
        deviceManager.setNom_tablet(tabletName);
        deviceManager.setID_tablet(idDevice);
        deviceManager.setUltima_Accion(latestAction);
        deviceManager.setApp_status(status);
        deviceManager.setLast_check(timeDisplay());
        deviceManager.setBattery_lvl(percentage);
        deviceManager.setDevice_charger(batteryConnected);
        CheckingBattery(percentage);


        reffDevices.setValue(deviceManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        changeStatus("Aplicación Abierta");
        Menu menu = navigationView.getMenu();
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        String user = prefs.getString("loggedIn","notLogged");
        if (user.equals("notLogged")){
            menu.findItem(R.id.nav_home).setVisible(false);
            menu.findItem(R.id.nav_send).setVisible(false);
            menu.findItem(R.id.nav_share).setVisible(false);
            menu.findItem(R.id.nav_tools).setVisible(false);
        } else {
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.nav_send).setVisible(true);
            menu.findItem(R.id.nav_share).setVisible(true);
            menu.findItem(R.id.nav_tools).setVisible(true);
        }
        db_action.create_entry_d("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();

        changeStatus("Aplicación Pausada");
        db_action.update_entry_d("onStop");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db_action.update_entry_d("onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAppInLockTaskMode()){
            startLockTask();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // If the screen is off then the device has been locked
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = powerManager.isInteractive();
        } else {
            isScreenOn = powerManager.isScreenOn();
        }
        if (!isScreenOn) {

            ProcessPhoenix.triggerRebirth(context);

        }
    }

    public void CheckingBattery(int battPercentage) {
        if (battPercentage <= 30) {
            reffDevicesWar = FirebaseDatabase.getInstance().getReference().child("Other Warnings").child(tabletName);
            batteryWarnings.setBattery_lvl(battPercentage);
            batteryWarnings.setId_tablet(idDevice);
            batteryWarnings.setLast_check(timeDisplay());
            batteryWarnings.setNom_tablet(tabletName);
            batteryWarnings.setWarning_type("Low Battery");
            reffDevicesWar.setValue(batteryWarnings);
        } else {
            reffDevicesWar = FirebaseDatabase.getInstance().getReference().child("Other Warnings").child(tabletName);
            reffDevicesWar.setValue(null);
        }
    }


    public void setImageView() {
        String mDrawableName = prefs.getString("ActiveUser","def");
        int resID = Resources.getSystem().getIdentifier(mDrawableName, "drawable", "android");
        imageView.setImageResource(resID);
        headerText.setText(mDrawableName);
        subheaderText.setText(mDrawableName.toLowerCase());
    }
    public static void removeImageView() {
        imageView.setImageResource(R.mipmap.ic_launcher);
        headerText.setText(prefs.getString("ActiveUser","def"));
        subheaderText.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }


    // ########### old
    private BroadcastReceiver LowBatteryReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(Intent.ACTION_CALL);
            intent1.setData(Uri.parse("tel:122"));
            startActivity(intent1);
        }
    };

    public void lockEscape(View view) {
    }

    public boolean isAppInLockTaskMode() {
        ActivityManager activityManager;

        activityManager = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activityManager.getLockTaskModeState()
                    != ActivityManager.LOCK_TASK_MODE_NONE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode();
        }

        return false;
    }
}
