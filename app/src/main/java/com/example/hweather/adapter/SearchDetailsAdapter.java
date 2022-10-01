package com.example.hweather.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hweather.R;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IDeleteItemListener;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailsAdapter extends BaseAdapter {

    ArrayList<String> nameCities;
    DataWeatherCity dataWeatherCity;
    private String apiKey = "efe98dfe0c15a78dfcd196813c195be2";
    IWeatherServices iWeatherServices;
    ImageView imgDelete;
    boolean activate;
    IDeleteItemListener iDeleteItemListener;

    public void setiDeleteItemListener(IDeleteItemListener iDeleteItemListener) {
        this.iDeleteItemListener = iDeleteItemListener;
    }

    public SearchDetailsAdapter(ArrayList<String> nameCities) {
        this.nameCities = nameCities;
    }

    @Override
    public int getCount() {
        return nameCities.size();
    }

    @Override
    public Object getItem(int i) {
        return nameCities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
        {
            view = View.inflate(viewGroup.getContext(), R.layout.custom_search_listview,null);
        }
        String name = nameCities.get(i);
        TextView nameCity = view.findViewById(R.id.tvNameCityDetail);
        TextView Temp = view.findViewById(R.id.tvTempCurrentDetail);
        TextView desc = view.findViewById(R.id.tvDescDetails);
        imgDelete = view.findViewById(R.id.imgDelete);
        ImageView readMore = view.findViewById(R.id.imgReadMore);
        TextView modeTemp = view.findViewById(R.id.tvModeTemp);
        ProgressBar progressBar = view.findViewById(R.id.progress_circular);
        RelativeLayout lnDetails = view.findViewById(R.id.lnDetails);
        imgDelete.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        lnDetails.setVisibility(View.INVISIBLE);
        String lang = MySharedPreferences.getLanguage(view.getContext());
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByNameCity(name,lang,apiKey).enqueue(new Callback<DataWeatherCity>() {
            @Override
            public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                if (response.code() == 200)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    lnDetails.setVisibility(View.VISIBLE);
                    dataWeatherCity = response.body();
                    nameCity.setText(dataWeatherCity.getCity().getName());
                    desc.setText(dataWeatherCity.getList().get(2).getWeather().get(0).getDescription());
                    int Unit = MySharedPreferences.getTempUnit(nameCity.getContext());
                    if (Unit == 0)
                    {
                        Temp.setText(String.valueOf(Math.round(dataWeatherCity.getList().get(0).getMain().getTemp() - 273)));
                        modeTemp.setText("°C");
                    }
                    else
                    {
                        Temp.setText(String.valueOf(Math.round(1.8 * dataWeatherCity.getList().get(0).getMain().getTemp() - 459.67)));
                        modeTemp.setText("°F");
                    }
                }
            }

            @Override
            public void onFailure(Call<DataWeatherCity> call, Throwable t) {

            }
        });

        if (activate) {
            imgDelete.setVisibility(View.VISIBLE);
        } else {
            imgDelete.setVisibility(View.INVISIBLE);
        }

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iDeleteItemListener.iDeteleItem(i);
            }
        });

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iDeleteItemListener.iReadMoreItem(i);
            }
        });
        return view;
    }

    public void activateButtons(boolean activate) {
        this.activate = activate;
        notifyDataSetChanged(); //need to call it for the child views to be re-created with buttons.
    }


}
