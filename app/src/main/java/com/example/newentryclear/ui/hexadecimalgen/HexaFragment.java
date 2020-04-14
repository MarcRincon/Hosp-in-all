package com.example.newentryclear.ui.hexadecimalgen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.newentryclear.R;

import java.util.Random;

public class HexaFragment extends Fragment implements View.OnClickListener {
    EditText cajaHex;
    TextView ID;
    private HexaViewModel hexaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        hexaViewModel =
                ViewModelProviders.of(this).get(HexaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        cajaHex = root.findViewById(R.id.insertarID);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // cajaHex.setText(sharedPreferences.getString("tabletID", "0"));
        root.findViewById(R.id.randomIDGen).setOnClickListener(this);
        root.findViewById(R.id.manualIDGen).setOnClickListener(this);
        ID = root.findViewById(R.id.id_disp);
        ID.setText(sharedPreferences.getString("tabletID", "0"));
        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.randomIDGen:

                Log.i("click", "Hex Generado");
                crearHexadecimal();
                break;
            case R.id.manualIDGen:
                if (!hexadecimalCheck()) {
                    Toast.makeText(getContext(), "El valor introducido no es hexadecimal !", Toast.LENGTH_LONG).show();
                } else {
                    String contenido = cajaHex.getText().toString().toUpperCase();
                    if (contenido.length() == 4) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("tabletID", contenido);
                        editor.commit();
                        ID.setText(contenido);
                        cajaHex.getText().clear();

                    } else {
                        Toast.makeText(getContext(), "El valor hexadecimal ha de ser de 4 carácteres !", Toast.LENGTH_LONG).show();
                    }
                }
            default:
                Log.i("click", "id no registrado en onclick");
                break;

        }
    }

    private void crearHexadecimal() {
        Random rand = new Random();
        int myRandomNumber = rand.nextInt(0xfff) + 0xfff; // Generates a random number between 0x10 and 0x20
        //System.out.printf("%x\n",myRandomNumber); // Prints it in hex, such as "0x14" or....
        String result = Integer.toHexString(myRandomNumber); // Random hex number in result
        result = result.toUpperCase();
        ID.setText(result);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("tabletID", result);
        editor.commit();

    }

    private Boolean hexadecimalCheck() {
        String contenido = cajaHex.getText().toString();
        Boolean isHex;
        try {
            int testCont = Integer.parseInt(contenido.toLowerCase(), 16);
            isHex = true;
        } catch (NumberFormatException e) {
            isHex = false;
        }
        return (isHex);
    }

}