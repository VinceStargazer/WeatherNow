package vince.stargazer.weathernow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final String bgImgDay = "https://images.unsplash.com/photo-1492615921011-dc481c1bee73?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80",
            bgImgNight = "https://images.unsplash.com/photo-1527842891421-42eec6e703ea?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nzl8fG5pZ2h0JTIwc2t5fGVufDB8MXwwfHw%3D&auto=format&fit=crop&w=2000&q=60";
    private final int PERMISSION_CODE = 1;
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityTV, temperatureTV, conditionTV;
    private TextInputEditText cityEdt;
    private ImageView bgImgIV;
    private ImageView iconIV;
    private ArrayList<WeatherRVModal> weatherRVModalList;
    private WeatherRVAdaptor weatherRVAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.RLHome);
        loadingPB = findViewById(R.id.PBLoading);
        cityTV = findViewById(R.id.TVCity);
        temperatureTV = findViewById(R.id.TVTemperature);
        conditionTV = findViewById(R.id.TVCondition);
        RecyclerView weatherRV = findViewById(R.id.RVWeather);
        cityEdt = findViewById(R.id.edtCity);
        bgImgIV = findViewById(R.id.bgImgBlack);
        iconIV = findViewById(R.id.IVIcon);
        ImageView searchIV = findViewById(R.id.IVSearch);
        weatherRVModalList = new ArrayList<>();
        weatherRVAdaptor = new WeatherRVAdaptor(this, weatherRVModalList);
        weatherRV.setAdapter(weatherRVAdaptor);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String cityName = location != null ? getCityName(location.getLongitude(), location.getLatitude()) : "Palo Alto";
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(view -> {
            Editable edt = cityEdt.getText();
            if (edt != null) {
                String city = edt.toString();
                if (city.isEmpty())
                    Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show();
                else
                    getWeatherInfo(city);
            } else {
                Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission granted..", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);
            for (Address adr : addresses) {
                if (adr == null) continue;
                String city = adr.getLocality();
                if (city != null && !city.equals("")) {
                    cityName = city;
                } else {
                    Log.d("TAG", "CITY NOT FOUND");
                    Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=f86140f8c3db4a2181791852231002&q=" + cityName + "&aqi=no";
        cityTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                , response -> {
            loadingPB.setVisibility(View.GONE);
            homeRL.setVisibility(View.VISIBLE);
            weatherRVModalList.clear();
            try {
                JSONObject curr = response.getJSONObject("current");
                String tempC = curr.getString("temp_c");
                temperatureTV.setText(tempC.concat("°C"));

                int isDay = curr.getInt("is_day");
                if (isDay == 1)
                    Picasso.get().load(bgImgDay).into(bgImgIV);
                else
                    Picasso.get().load(bgImgNight).into(bgImgIV);

                JSONObject condition = curr.getJSONObject("condition");
                String condText = condition.getString("text"), condIcon = condition.getString(
                        "icon");
                conditionTV.setText(condText);
                Picasso.get().load("http:".concat(condIcon)).into(iconIV);

                JSONObject forecast = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0);
                JSONArray hourArr = forecast.getJSONArray("hour");
                for (int i = 0; i < hourArr.length(); i++) {
                    JSONObject hourObj = hourArr.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temp = hourObj.getString("temp_c");
                    String img = hourObj.getJSONObject("condition").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    weatherRVModalList.add(new WeatherRVModal(time, temp, img, wind));
                }
                weatherRVAdaptor.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(MainActivity.this, "Please enter valid city name..",
                Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }
}