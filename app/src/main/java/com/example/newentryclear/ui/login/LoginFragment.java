package com.example.newentryclear.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.newentryclear.MainActivity;
import com.example.newentryclear.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.newentryclear.webdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment {
    EditText nombre, contrasenya;
    Button signIn, signOut;
    ImageView userImage;
    DatabaseReference reff;
    String passwordDB = "", usercheck;
    boolean correctUser = false;
    boolean correctPassword = false;
    List<String> userList = new ArrayList<String>();
    private LoginViewModel loginViewModel;
    NavigationView navigationView;
    webdb db_action;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        // View main = inflater.inflate(R.layout.content_main, container, false);
        nombre = root.findViewById(R.id.edit_username);
        userImage = root.findViewById(R.id.imageView);
        // navigationView = main.findViewById(R.id.nav_view);
        navigationView = Objects.requireNonNull(getActivity()).findViewById(R.id.nav_view);
        contrasenya = root.findViewById(R.id.edit_passwordUser);
        signIn = root.findViewById(R.id.btn_logIn);
        signOut = root.findViewById(R.id.btn_logOut);

        SharedPreferences prefs = getContext().getSharedPreferences("com.example.newentry", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String database = sharedPreferences.getString("name_db", "Database");

        reff = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        usercheck = reff.getKey();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        if (prefs.getString("loggedIn", "notLogged").equals("notLogged")) {
            signIn.setEnabled(true);
            signOut.setEnabled(false);
        } else {
            signIn.setEnabled(false);
            signOut.setEnabled(true);
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    userList.add(snap.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = nombre.getText().toString();

                for (String s : userList) {
                    if (s.equals(userName)) {
                        correctUser = true;
                    }
                }
                if (!correctUser) {
                    Toast.makeText(getContext(), "Nombre de usuario inexistente", Toast.LENGTH_LONG).show();
                } else {
                    reff.child(userName).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            passwordDB = Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString();

                            if (passwordDB.equals(contrasenya.getText().toString())) {
                                Toast.makeText(getContext(), "Log-In completado !", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("ActiveUser", userName);
                                editor.apply();
                                //   nombre.getText().clear();
                                //   contrasenya.getText().clear();
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.setImageView();
                                prefs.edit().putString("loggedIn", "logged").apply();
                                Menu menu = navigationView.getMenu();
                                if (userName.equals("Admin")) {
                                    menu.findItem(R.id.nav_home).setVisible(true);
                                    menu.findItem(R.id.nav_send).setVisible(true);
                                    menu.findItem(R.id.nav_share).setVisible(true);
                                    menu.findItem(R.id.nav_tools).setVisible(true);
                                } else {
                                    menu.findItem(R.id.nav_home).setVisible(true);
                                    menu.findItem(R.id.nav_send).setVisible(true);
                                    menu.findItem(R.id.nav_share).setVisible(false);
                                    menu.findItem(R.id.nav_tools).setVisible(false);
                                }
                                signIn.setEnabled(false);
                                signOut.setEnabled(true);
                                nombre.setEnabled(false);
                                contrasenya.setEnabled(false);
                            } else {
                                Toast.makeText(getContext(), "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d("sig In", userName + " ," + contrasenya.getText().toString());
                    //userImage.setImageResource(R.drawable.ic_person_black_24dp);

                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ActiveUser", "No Usuario");
                editor.apply();
                MainActivity.removeImageView();
                nombre.getText().clear();
                contrasenya.getText().clear();
                prefs.edit().putString("loggedIn", "logged").apply();
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.nav_home).setVisible(false);
                menu.findItem(R.id.nav_send).setVisible(false);
                menu.findItem(R.id.nav_share).setVisible(false);
                menu.findItem(R.id.nav_tools).setVisible(false);
                signIn.setEnabled(true);
                signOut.setEnabled(false);
                nombre.setEnabled(true);
                contrasenya.setEnabled(true);
                Toast.makeText(getContext(), "User logged out", Toast.LENGTH_LONG).show();

            }
        });
        db_action = new webdb();
        db_action.pruebas(getContext());
        return root;
    }

}