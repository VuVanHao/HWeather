package com.example.hweather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hweather.adapter.CityWeatherViewPager;
import com.example.hweather.datahelper.CityManageHelper;
import com.example.hweather.fragment.FragmentNote;
import com.example.hweather.utils.MySharedPreferences;
import com.example.hweather.utils.PreferencesManager;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MainActivity extends AppCompatActivity {

    ImageView imgSetting, imgSearch, imgManageCity,imgSearchMap;
    String lat = "";
    String lon = "";
    ArrayList<String> mListCityName;
    ViewPager vpCity;
    CityWeatherViewPager cityWeatherViewPager;
    CityManageHelper cityManageHelper;
    Geocoder geocoder;
    CircleIndicator indicator;
    int pos = 0;
    FragmentNote fragmentNote;
    PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityManageHelper = new CityManageHelper(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        mListCityName = new ArrayList<>();
        indicator = findViewById(R.id.circle_indicator);
        imgSearchMap = findViewById(R.id.imgSearchMap);
        imgManageCity = findViewById(R.id.imgManageCity);
        imgSearch = findViewById(R.id.imgSearch);
        imgSetting = findViewById(R.id.imgSetting);
        preferencesManager = new PreferencesManager(this);
        TapTargetFirstTime();
        addPosCurrent();
        if (checkNetwork())
        {
            initView();
            initData();
        }
        else
        {
            fragmentNote = FragmentNote.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fg_note_reset,fragmentNote)
                    .commit();
        }
    }

    private void TapTargetFirstTime() {
        if (preferencesManager.isFirstTimeLaunch())
        {
            preferencesManager.setFirstTimeLaunch(false);
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(imgSetting)
                    .setPrimaryText("Đây là cài đặt")
                    .setSecondaryText("Bấm vào đây để chỉnh sửa các cài đặt cho ứng dụng của bạn.")
                    .setPromptFocal(new RectanglePromptFocal())
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                            {
                                goto2();
                            }
                        }
                    })
                    .show();
        }
    }




    private void initData() {
        pos = getIntent().getIntExtra("POS",0);
        vpCity = findViewById(R.id.vpCity);
        mListCityName.clear();
        mListCityName.addAll(cityManageHelper.getListCityName());
        cityWeatherViewPager = new CityWeatherViewPager(getSupportFragmentManager(), mListCityName);
        vpCity.setAdapter(cityWeatherViewPager);
        indicator.setViewPager(vpCity);
        if (pos != 0)
        {
            vpCity.setCurrentItem(pos);
        }
        cityWeatherViewPager.registerDataSetObserver(indicator.getDataSetObserver());
        cityWeatherViewPager.notifyDataSetChanged();
    }

    public void addPosCurrent() {
        int count = cityManageHelper.CountRecord();
        get_GPs();
        if (count == 0) {
            cityManageHelper.addSearchCity("Hà nội");
        } else {
            get_GPs();
            if (!Objects.equals(lat, "")) {
                String name = getNameCity(lat, lon);
                if (name.length() > 1) {
                    if (Objects.equals(name, "Ha Tay")) {
                        name = "Hà nội";
                    }
                    String nameOld = cityManageHelper.getNameRowOne();
                    cityManageHelper.updateCityName(nameOld, name);
                    Toast.makeText(this, "Đã cập nhật lại vị trí", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getPosition() {
        get_GPs();
        if (!Objects.equals(lat, "")) {
            String name = getNameCity(lat, lon);
            if (name.length() > 1) {
                if (Objects.equals(name, "Ha Tay")) {
                    name = "Hà nội";
                }
                String nameOld = cityManageHelper.getNameRowOne();
                cityManageHelper.updateCityName(nameOld, name);
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getPosition();

    }

    private void initView() {
        checkPermissionAgain();
        getPosition();
        bManageCity();
        bSearch();
        bSetting();
        bMap();
    }

    private void bMap() {
        imgSearchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Search_Map.class);
                startActivity(intent);
            }
        });
    }

    public void checkPermissionAgain()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                    99);
        }
    }


    public void get_GPs() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                    99);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                lat = location.getLatitude() + "";
                lon = location.getLongitude() + "";
            }

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                    0, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            lat = location.getLatitude() + "";
                            lon = location.getLongitude() + "";
                        }
                    });
        }
    }


    public String getNameCity(String lat, String lon) {
        String nameCity = "";
        List<Address> addresses = new ArrayList<>();
        try {
            if (!Objects.equals(lat, "") && !Objects.equals(lon, "")) {
                addresses = geocoder.getFromLocation(Float.parseFloat(lat), Float.parseFloat(lon), 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses.size() > 0) {
            nameCity = addresses.get(0).getAdminArea();
        }
        return nameCity;
    }

    public void bManageCity() {
        imgManageCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Manage_City.class);
                startActivity(intent);
            }
        });
    }

    public void bSearch() {
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Search_City.class);
                startActivity(intent);
            }
        });
    }

    public void bSetting() {
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || dataMobile.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void goto2() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(imgSearchMap)
                .setPrimaryText("Tìm kiếm thời tiết bằng bản đồ")
                .setSecondaryText("Bấm vào để tìm kiếm thời tiết các khu vực trên bản đồ.")
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {
                            goto3();
                        }
                    }
                })
                .show();
    }

    private void goto3() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(imgManageCity)
                .setPrimaryText("Quản lí thành phố")
                .setSecondaryText("Bấm vào để xem danh sách và quản lí các thành phố của bạn")
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {
                            goto4();
                        }
                    }
                })
                .show();
    }

    private void goto4() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(imgSearch)
                .setPrimaryText("Tìm kiếm nhanh")
                .setSecondaryText("Bấm vào để tìm kiếm tiết dựa trên tên thành phố")
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                        {
                        }
                    }
                })
                .show();
    }

}