package com.example.newentryclear.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.newentryclear.AlarmasMedic;
import com.example.newentryclear.BatteryWarnings;
import com.example.newentryclear.DeviceManager;
import com.example.newentryclear.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.newentryclear.webdb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.BATTERY_SERVICE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    DatabaseReference reff;
    DatabaseReference reffDevices;
    DatabaseReference reffDevicesWar;

    AlarmasMedic alarmasMedic;
    DeviceManager deviceManager;
    BatteryWarnings batteryWarnings;

    String tabletName;
    String username;
    String idDevice;
    webdb db_action;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.findViewById(R.id.btn_blue).setOnClickListener(this::onClick);
        root.findViewById(R.id.btn_green).setOnClickListener(this::onClick);
        root.findViewById(R.id.btn_yellow).setOnClickListener(this::onClick);
        root.findViewById(R.id.btn_red).setOnClickListener(this::onClick);
        db_action.pruebas(getContext());
        return root;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmasMedic = new AlarmasMedic();
        deviceManager = new DeviceManager();
        batteryWarnings = new BatteryWarnings();
        db_action = new webdb();

    }

    @Override
    public void onStart() {
        super.onStart();

        changeStatus("Aplicación Abierta");
    }

    @Override
    public void onStop() {
        super.onStop();

        changeStatus("Aplicación Abierta");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_blue:

                Log.i("click", "el boton blue funciona");
                sendWarningToFirebase("Alarma Azul");
                Toast.makeText(getContext(), "Alarma azul enviada", Toast.LENGTH_LONG).show();
                db_action.update_entry("Azul");

                break;

            case R.id.btn_green:

                Log.i("click", "el boton  green funciona");
                sendWarningToFirebase("Alarma Verde");
                Toast.makeText(getContext(), "Alarma verde enviada", Toast.LENGTH_LONG).show();
                db_action.update_entry("Verde");

                break;

            case R.id.btn_yellow:

                Log.i("click", "el boton yellow funciona");
                sendWarningToFirebase("Alarma Amarilla");

                Toast.makeText(getContext(), "Alarma amarilla enviada", Toast.LENGTH_LONG).show();
                db_action.create_entry("Amarilla");

                break;

            case R.id.btn_red:

                Log.i("click", "el boton red funciona");
                sendWarningToFirebase("Alarma Roja");

                Toast.makeText(getContext(), "Alarma roja enviada", Toast.LENGTH_LONG).show();
                db_action.create_entry("Roja");

                break;

            default:
                Log.i("click", "id no registrado en onclick");


        }
    }

    public static String timeDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    public static String timeDisplayDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
    }

    public static String timeDisplayHours() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentDate = format.format(calendar.getTime());
        return currentDate;
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

    public static boolean isPlugged(Context context) {
        boolean isPlugged = false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }

    public void changeStatus(String status) {
        SharedPreferences prefs = getContext().getSharedPreferences(
                "com.example.newentry", Context.MODE_PRIVATE);
        if (isPlugged(getContext())) {
            prefs.edit().putString("chargerConnected", "Conectado").apply();
        } else if (!isPlugged(getContext())) {
            prefs.edit().putString("chargerConnected", "Desconectado").apply();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        tabletName = sharedPreferences.getString("tabletName", "Tablet B1");
        idDevice = sharedPreferences.getString("tabletID", "0");

        String latestAction = sharedPreferences.getString("latestAction", null);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        BatteryManager bm = (BatteryManager) getActivity().getSystemService(BATTERY_SERVICE);
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

    private void sendWarningToFirebase(String typeOfWarning) {

        SharedPreferences prefs = getContext().getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String database = sharedPreferences.getString("name_db", "Database");
        int actualBattery = prefs.getInt("percentageBattery", -1);
        String batteryConnected = prefs.getString("chargerConnected", "defaultStringIfNothingFound");
        tabletName = sharedPreferences.getString("tabletName", "Tablet B1");
        idDevice = sharedPreferences.getString("tabletID", "0");
        username = sharedPreferences.getString("ActiveUser", null);
        reff = FirebaseDatabase.getInstance().getReference().child(database).child(timeDisplayDay()).child("Log " + timeDisplayHours());

        //crea objeto alarma medica
        alarmasMedic.setNom_tablet(tabletName);
        alarmasMedic.setID_tablet(idDevice);
        alarmasMedic.setTipo_Alarma(typeOfWarning);
        alarmasMedic.setTime(timeDisplay());
        alarmasMedic.setNom_user(username);

        //crea objeto device manager
        deviceManager.setDevice_charger(batteryConnected);
        deviceManager.setLast_check(timeDisplay());
        deviceManager.setApp_status("Aplicación Abierta");
        //stopLockTask();

        reffDevicesWar = FirebaseDatabase.getInstance().getReference().child("Other Warnings").child(tabletName);
        reffDevicesWar.setValue(null);

        BatteryManager bm = (BatteryManager) getActivity().getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        CheckingBattery(percentage);

        reffDevices = FirebaseDatabase.getInstance().getReference().child("Devices Status").child(tabletName);

        reffDevices.setValue(deviceManager);
        reff.setValue(alarmasMedic);
    }
}