package com.example.newentryclear;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.newentryclear.UtilityClass.timeDisplay;
import static java.lang.String.valueOf;

public class webdb extends AppCompatActivity {

    private static String ip = "192.168.10.131";
    //private static String ip = "10.0.2.2";
    //com.example.newentryclear.MainActivity@9fcdbfe
    SharedPreferences sharedPreferences,prefs;
    private static String tabletName,id_device,status,latestAction,batteryConnected;
    int percentage;

    public void pruebas(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefs = context.getApplicationContext().getSharedPreferences(
                "com.example.newentryclear", Context.MODE_PRIVATE);
        tabletName = sharedPreferences.getString("tabletName", "Tablet 10");
        id_device = sharedPreferences.getString("tabletID", "00c");
        latestAction = sharedPreferences.getString("latestAction", null);
        batteryConnected = sharedPreferences.getString("chargerConnected", "defaultStringIfNothingFound");

        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

    }

    public void read_entry( String typeWarning){
        servicioWeb("read",typeWarning,false);
    }

    public void delete_entry( String typeWarning){
        servicioWeb("delete",typeWarning,false);
    }

    public void create_entry(String typeWarning){
        servicioWeb("create",typeWarning,false);
    }
    public void create_entry_d(String state){
        status = state;
        servicioWeb("createdevice","null",false);
    }
    public void update_entry_d(String state){
        status = state;
        servicioWeb("updatedevice","null",false);
    }
    public void update_entry(String typeWarning){
        servicioWeb("update",typeWarning,true);
    }

    private void servicioWeb(final String name_serv, String warning,Boolean attended) {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + ip + "/miray/"+name_serv+".php").newBuilder();


            if (name_serv.equals("create")) {
                urlBuilder.addQueryParameter("ID", id_device);
                urlBuilder.addQueryParameter("Name", tabletName);
                urlBuilder.addQueryParameter("Warning", warning);
                urlBuilder.addQueryParameter("Attended", valueOf(attended));
                urlBuilder.addQueryParameter("TimeLog", timeDisplay());
            }else if (name_serv.equals("delete")){
                urlBuilder.addQueryParameter("ID", id_device);
            }else if(name_serv.equals("update")){
                urlBuilder.addQueryParameter("ID", id_device);
                urlBuilder.addQueryParameter("Name", tabletName);
                urlBuilder.addQueryParameter("Warning", warning);
                urlBuilder.addQueryParameter("Attended", valueOf(attended));
                urlBuilder.addQueryParameter("TimeLog", timeDisplay());
            }else if(name_serv.equals("read")){
                urlBuilder.addQueryParameter("ID", id_device);
            }else if(name_serv.equals("createdevice")){
                urlBuilder.addQueryParameter("ID", id_device);
                urlBuilder.addQueryParameter("Name", tabletName);
                urlBuilder.addQueryParameter("Status", status);//
                urlBuilder.addQueryParameter("BTL", valueOf(percentage));//
                urlBuilder.addQueryParameter("TimeLog", timeDisplay());
            }else if(name_serv.equals("updatedevice")){
                urlBuilder.addQueryParameter("ID", id_device);
                urlBuilder.addQueryParameter("Name", tabletName);
                urlBuilder.addQueryParameter("Status", status);//
                urlBuilder.addQueryParameter("BTL", valueOf(percentage));//
                urlBuilder.addQueryParameter("TimeLog", timeDisplay());
            }

            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if(!name_serv.equals("read")){
                                    Log.i("response","Llegamos al response");
                                    Log.i("response",response.body().string());
                                }else{
                                    Log.i("response","Llegamos al response read");
                                    try {
                                        String data = response.body().string();

                                        JSONArray jsonArray = new JSONArray(data);
                                        JSONObject jsonObject;

                                        jsonObject = jsonArray.getJSONObject(0);
                                        String res1,res2,res3,res4;
                                        res1 = jsonObject.getString("id");
                                        res2 = jsonObject.getString("deviceName");
                                        res3 = jsonObject.getString("typeWarning");
                                        res4 = jsonObject.getString("attended");
                                        Log.i("response",res1+", "+res2+", "+res3+", "+res4);
                                    } catch (JSONException e) {
                                        Log.e("Json ERROR",e.getMessage());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
