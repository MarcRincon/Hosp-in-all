package com.example.newentryclear.ui.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.newentryclear.Preferences;
import com.example.newentryclear.R;

import java.util.Objects;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    Button openPreferences, stopLock;
    String usuarioText, dbName, dbUrlText, dispText, dispIDText;
    TextView usuario, nombre_database, url_database, nombre_dispositivo, id_dispositivo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        openPreferences = root.findViewById(R.id.preferenceOpen);
        stopLock = root.findViewById(R.id.desfijar);
        usuario = root.findViewById(R.id.actualUser);
        nombre_database = root.findViewById(R.id.nombreDB);
        url_database = root.findViewById(R.id.URLdb);
        nombre_dispositivo = root.findViewById(R.id.nombreDisp);
        id_dispositivo = root.findViewById(R.id.id_disp);

        openPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.preferenceOpen:
                        startActivity(new Intent(getContext(), Preferences.class));
                }
            }
        });
        stopLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.desfijar:
                        Objects.requireNonNull(getActivity()).stopLockTask();
                        Toast.makeText(getContext(), "Aplicación desfijada", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;


    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        usuarioText = prefs.getString("ActiveUser", null);
        dbName = sharedPreferences.getString("name_db", null);
        dbUrlText = sharedPreferences.getString("url_db", null);
        dispText = sharedPreferences.getString("tabletName", null);
        dispIDText = sharedPreferences.getString("tabletID", null);


        usuario.setText(usuarioText);
        nombre_database.setText(dbName);
        url_database.setText(dbUrlText);
        nombre_dispositivo.setText(dispText);
        id_dispositivo.setText(dispIDText);

    }
}