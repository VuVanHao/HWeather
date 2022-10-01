package com.example.hweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hweather.adapter.SearchDetailsAdapter;
import com.example.hweather.datahelper.CitySearchHelper;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IDeleteItemListener;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search_City extends AppCompatActivity implements IDeleteItemListener {

    DataWeatherCity dataWeatherCity;
    private String apiKey = "efe98dfe0c15a78dfcd196813c195be2";
    IWeatherServices iWeatherServices;
    ArrayAdapter<String> adapter;
    private ImageView btnBackSearch;
    @BindView(R.id.btnSearchSpeed)
    ImageView btnSearchSpeed;
    @BindView(R.id.edtSearchName)
    EditText edtSearchName;
    @BindView(R.id.lvHistorySearch)
    ListView lvHistorySearch;
    @BindView(R.id.imgCancel) ImageView imgCancel;
    @BindView(R.id.tvactionbarSearch)
    TextView tvActionBar;
    @BindView(R.id.contentHistory)
    TextView history;
    ArrayList<String> arrNameCity;
    SearchDetailsAdapter searchDetailsAdapter;
    CitySearchHelper citySearchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        ButterKnife.bind(this);
        initView();
        arrNameCity = new ArrayList<>();
        citySearchHelper = new CitySearchHelper(this);
        arrNameCity.clear();
        arrNameCity.addAll(citySearchHelper.getListCityName());
        searchDetailsAdapter = new SearchDetailsAdapter(arrNameCity);
        searchDetailsAdapter.setiDeleteItemListener(this);
        lvHistorySearch.setAdapter(searchDetailsAdapter);
        btnSearchSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = edtSearchName.getText().toString();
                iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
                iWeatherServices.getWeatherByNameCity(cityName,"vi",apiKey).enqueue(new Callback<DataWeatherCity>() {
                    @Override
                    public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                        if (response.code() == 200)
                        {
                            dataWeatherCity = response.body();
                            ArrayList<String> listNameCity = citySearchHelper.getListCityName();
                            if (listNameCity.contains(dataWeatherCity.getCity().getName())) {
                                citySearchHelper.deleteSearchCityName(dataWeatherCity.getCity().getName());
                            }
                            citySearchHelper.addSearchCity(dataWeatherCity.getCity().getName());
                            arrNameCity.clear();
                            arrNameCity.addAll(citySearchHelper.getListCityName());
                            lvHistorySearch.setAdapter(searchDetailsAdapter);
                            edtSearchName.setText("");
                            String nameCity = dataWeatherCity.getCity().getName();
                            Intent intent = new Intent(Search_City.this,CityWeatherDetails.class);
                            intent.putExtra("CITY_NAME",nameCity);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(Search_City.this, "Tên thành phố không thể tìm thấy", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataWeatherCity> call, Throwable t) {

                    }
                });
            }
        });

        lvHistorySearch.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchDetailsAdapter.activateButtons(true);
                imgCancel.setVisibility(View.VISIBLE);
                tvActionBar.setVisibility(View.INVISIBLE);
                btnBackSearch.setVisibility(View.INVISIBLE);
                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchDetailsAdapter.activateButtons(false);
                        imgCancel.setVisibility(View.INVISIBLE);
                        tvActionBar.setVisibility(View.VISIBLE);
                        btnBackSearch.setVisibility(View.VISIBLE);
                    }
                });
                return false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        imgCancel.setVisibility(View.INVISIBLE);
        buttonBack();
        String lang = MySharedPreferences.getLanguage(getApplicationContext());
        if (Objects.equals(lang, "vi"))
        {
            tvActionBar.setText("Tìm kiếm");
            edtSearchName.setHint("Tên thành phố");
            history.setText("Lịch sử tìm kiếm");
        }
        else
        {
            tvActionBar.setText("Search city");
            edtSearchName.setHint("Name city");
            history.setText("Search history");
        }
    }

    private void buttonBack() {
        btnBackSearch = findViewById(R.id.btnBackSearch);
        btnBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search_City.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void iDeteleItem(int i) {
        String nameCityDel = arrNameCity.get(i);
        citySearchHelper.deleteSearchCityName(nameCityDel);
        arrNameCity.remove(i);
        searchDetailsAdapter.notifyDataSetChanged();
        searchDetailsAdapter.activateButtons(false);
        imgCancel.setVisibility(View.INVISIBLE);
        tvActionBar.setVisibility(View.VISIBLE);
        btnBackSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void iReadMoreItem(int i) {
        String nameCity = arrNameCity.get(i);
        Intent intent = new Intent(Search_City.this,CityWeatherDetails.class);
        intent.putExtra("CITY_NAME",nameCity);
        startActivity(intent);
    }

    @Override
    public void iDelItemName(String name) {

    }

    @Override
    public void iDelItemUnCheck(int i) {

    }

}