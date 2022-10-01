package com.example.hweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hweather.databinding.ActivityCityWeatherDetailsBinding;
import com.example.hweather.models.City;
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

public class CityWeatherDetails extends AppCompatActivity {

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

    @BindView(R.id.titleDetails)
    RelativeLayout titleDetails;
    @BindView(R.id.details)
    RelativeLayout Details;
    @BindView(R.id.ln1)
    LinearLayout ln1;
    @BindView(R.id.progress_circular)
    ProgressBar processBar;

    DataWeatherCity dataWeatherCity;
    IWeatherServices iWeatherServices;
    private String apiKey = "b52f8385cdf7f4dab2f09a10d9b9d967";
    ActivityCityWeatherDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather_details);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_city_weather_details);
        ButterKnife.bind(this);
        initLangUnit();
        initData(binding);
        backButton();

    }

    @SuppressLint("SetTextI18n")
    private void initLangUnit() {
        String lang = MySharedPreferences.getLanguage(getApplicationContext());
        if (Objects.equals(lang, "vi"))
        {
            tvActionbar.setText("Thời tiết hiện tại : ");
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
            tvActionbar.setText("Current weather");
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


    private void backButton() {
        btnBack = findViewById(R.id.btnBackWeatherDetail);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityWeatherDetails.this,Search_City.class);
                startActivity(intent);
            }
        });
    }

    private void initData( ActivityCityWeatherDetailsBinding binding) {
        titleDetails.setVisibility(View.INVISIBLE);
        Details.setVisibility(View.INVISIBLE);
        ln1.setVisibility(View.INVISIBLE);
        processBar.setVisibility(View.VISIBLE);
        String nameCity = getIntent().getStringExtra("CITY_NAME");
        String lang = MySharedPreferences.getLanguage(getApplicationContext());
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByNameCity(nameCity,lang,apiKey).enqueue(new Callback<DataWeatherCity>() {
            @Override
            public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                if (response.code() == 200)
                {
                    titleDetails.setVisibility(View.VISIBLE);
                    Details.setVisibility(View.VISIBLE);
                    ln1.setVisibility(View.VISIBLE);
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
}