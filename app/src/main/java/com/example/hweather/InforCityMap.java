package com.example.hweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hweather.databinding.ActivityInforCityMapBinding;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforCityMap extends AppCompatActivity {

    ImageView btnBack;
    @BindView(R.id.imgWeatherDetails) ImageView imgWeatherDetails;
    @BindView(R.id.tvactionbarSearch)
    TextView tvActionbar;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.tv7)
    TextView tv7;
    @BindView(R.id.tv8)
    TextView tv8;
    @BindView(R.id.tv9)
    TextView tv9;
    @BindView(R.id.tv12)
    TextView tv12;
    @BindView(R.id.tvTempMode)
    TextView tvTempMode;
    @BindView(R.id.tvLat)
    TextView tvLat;
    @BindView(R.id.tvLong)
    TextView tvLon;
    @BindView(R.id.tv11)
    TextView tv11;
    @BindView(R.id.tv22)
    TextView tv22;

    @BindView(R.id.titleDetails)
    RelativeLayout titleDetails;
    @BindView(R.id.details)
    RelativeLayout Details;

    @BindView(R.id.progress_circular)
    ProgressBar processBar;

    DataWeatherCity dataWeatherCity;
    IWeatherServices iWeatherServices;
    private String apiKey = "91aea4d275c0cd1c8e86c00186ceed9c";//91aea4d275c0cd1c8e86c00186ceed9c //b52f8385cdf7f4dab2f09a10d9b9d967
    ActivityInforCityMapBinding binding;
    String lat = "";
    String lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_city_map);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_infor_city_map);
        ButterKnife.bind(this);
        initLangUnit();
        lat = getIntent().getStringExtra("LAT");
        lon = getIntent().getStringExtra("LONG");
        tvLat.setText(lat);
        tvLon.setText(lon);
        initData(binding);
        backButton();
    }

    private void backButton() {
        btnBack = findViewById(R.id.btnBackWeatherDetail);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InforCityMap.this,Search_Map.class);
                startActivity(intent);
            }
        });
    }

    private void initData(ActivityInforCityMapBinding binding) {
        titleDetails.setVisibility(View.INVISIBLE);
        Details.setVisibility(View.INVISIBLE);
        processBar.setVisibility(View.VISIBLE);
        String lang = MySharedPreferences.getLanguage(getApplicationContext());
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByLocation(lat,lon,lang,apiKey).enqueue(new Callback<DataWeatherCity>() {
            @Override
            public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                if (response.code() == 200)
                {
                    titleDetails.setVisibility(View.VISIBLE);
                    Details.setVisibility(View.VISIBLE);
                    processBar.setVisibility(View.INVISIBLE);
                    dataWeatherCity = response.body();
                    binding.setDataWeatherCity(dataWeatherCity);
                    Glide.with(getApplicationContext()).load(dataWeatherCity.getList().get(0).getWeather().get(0).getIcon()).into(imgWeatherDetails);
                }
            }

            @Override
            public void onFailure(Call<DataWeatherCity> call, Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initLangUnit() {
        String lang = MySharedPreferences.getLanguage(getApplicationContext());
        if (Objects.equals(lang, "vi"))
        {
            tv11.setText("Vĩ độ: ");
            tv22.setText("Kinh độ: ");
            tvActionbar.setText("Thông tin thời tiết : ");
            tv1.setText("Nhiệt độ hiện tại : ");
            tv4.setText("Độ ẩm");
            tv5.setText("Áp suất");
            tv6.setText("Tốc độ gió");
            tv7.setText("Hướng gió");
            tv8.setText("Tầm nhìn");
            tv9.setText("Độ bao phủ");
            tv12.setText("Lượng mưa");
        }
        else
        {
            tv11.setText("Latitude: ");
            tv22.setText("Longitude: ");
            tvActionbar.setText("Information");
            tv1.setText("Current temp : ");
            tv4.setText("Humidity");
            tv5.setText("Pressuare");
            tv6.setText("Wind Speed");
            tv7.setText("Wind Direction");
            tv8.setText("Visibility");
            tv9.setText("Cloud");
            tv12.setText("Having Rain");
        }
        int Unit = MySharedPreferences.getTempUnit(getApplicationContext());
        if (Unit == 0)
        {
            tvTempMode.setText("°C");
        }
        else
        {
            tvTempMode.setText("°F");
        }
    }
}