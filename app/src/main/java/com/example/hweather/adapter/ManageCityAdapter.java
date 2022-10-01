package com.example.hweather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hweather.R;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IDeleteItemListener;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCityAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<String> nameCities;
    DataWeatherCity dataWeatherCity;
    private String apiKey = "efe98dfe0c15a78dfcd196813c195be2"; //b52f8385cdf7f4dab2f09a10d9b9d967   efe98dfe0c15a78dfcd196813c195be2
    IWeatherServices iWeatherServices;
    boolean activate = false;
    IDeleteItemListener iDeleteItemListener;

    public ManageCityAdapter(Context mContext, ArrayList<String> nameCities) {
        this.mContext = mContext;
        this.nameCities = nameCities;
    }

    public void setiDeleteItemListener(IDeleteItemListener iDeleteItemListener) {
        this.iDeleteItemListener = iDeleteItemListener;
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
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.custom_manage_listview, null);
        }
        String name = nameCities.get(i);
        TextView nameCity = view.findViewById(R.id.tvNameCityManage);
        TextView Temp = view.findViewById(R.id.tvTempCurrentManage);
        TextView desc = view.findViewById(R.id.tvDescManage);
        ImageView imgDes = view.findViewById(R.id.imgDes);
        TextView tempMin = view.findViewById(R.id.tvMinTempManage);
        TextView tempMax = view.findViewById(R.id.tvMaxTempManage);
        TextView tvModeTemp = view.findViewById(R.id.tvModeTemp);
        CheckBox cbDelItem = view.findViewById(R.id.cbDelItem);
        TextView tv2 = view.findViewById(R.id.tv2);
        TextView tv3 = view.findViewById(R.id.tv3);
        ProgressBar progressBar = view.findViewById(R.id.progress_circular);
        RelativeLayout relativeLayout = view.findViewById(R.id.rlLoad);
        String lang = MySharedPreferences.getLanguage(mContext);
        int unit = MySharedPreferences.getTempUnit(mContext);
        if (unit == 0)
        {
            tvModeTemp.setText("°C");
            tv2.setText("°C / ");
            tv3.setText("°C");
        }
        else
        {
            tvModeTemp.setText("°F");
            tv2.setText("°F / ");
            tv3.setText("°F");
        }
        relativeLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByNameCity(name, lang, apiKey).enqueue(new Callback<DataWeatherCity>() {
            @Override
            public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                if (response.code() == 200) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    dataWeatherCity = response.body();
                    nameCity.setText(dataWeatherCity.getCity().getName());
                    int Unit = MySharedPreferences.getTempUnit(mContext);
                    desc.setText(dataWeatherCity.getList().get(2).getWeather().get(0).getDescription());
                    Glide.with(mContext).load(dataWeatherCity.getList().get(2).getWeather().get(0).getIcon()).into(imgDes);
                    //TODO getTempMin
                    float tempMinManage = dataWeatherCity.getList().get(2).getMain().getTempMin();
                    for (int n = 3; n <= 9; n++) {
                        if (tempMinManage > dataWeatherCity.getList().get(n).getMain().getTempMin()) {
                            tempMinManage = dataWeatherCity.getList().get(n).getMain().getTempMin();
                        }

                    }

                    //TODO getTempMax

                    float tempMaxManage = dataWeatherCity.getList().get(2).getMain().getTempMax();
                    for (int m = 3; m <= 9; m++) {
                        if (tempMaxManage < dataWeatherCity.getList().get(m).getMain().getTempMax()) {
                            tempMaxManage = dataWeatherCity.getList().get(m).getMain().getTempMax();
                        }

                    }
                    if (Unit == 0)
                    {
                        tempMin.setText(String.valueOf(Math.round(tempMinManage - 273)));
                        tempMax.setText(String.valueOf(Math.round(tempMaxManage - 273)));
                        Temp.setText(String.valueOf(Math.round(dataWeatherCity.getList().get(0).getMain().getTemp() - 273)));
                    }
                    else
                    {
                        tempMin.setText(String.valueOf(Math.round(1.8 * tempMinManage - 459.67)));
                        tempMax.setText(String.valueOf(Math.round(1.8 * tempMaxManage - 459.67)));
                        Temp.setText(String.valueOf(Math.round(1.8 * dataWeatherCity.getList().get(0).getMain().getTemp() - 459.67)));
                    }

                }

            }

            @Override
            public void onFailure(Call<DataWeatherCity> call, Throwable t) {
                Toast.makeText(mContext, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        cbDelItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    iDeleteItemListener.iDeteleItem(i);
                    iDeleteItemListener.iReadMoreItem(1);
                }
                if (!b)
                {
                    iDeleteItemListener.iDelItemUnCheck(i);
                    iDeleteItemListener.iReadMoreItem(-1);
                }
            }
        });
        if (activate) {
            if (i == 0 )
            {
                cbDelItem.setVisibility(View.INVISIBLE);
            }else
            {
                cbDelItem.setVisibility(View.VISIBLE);
            }
            tvModeTemp.setPadding(0,0,150,0);
            desc.setPadding(0,0,150,0);
        }
        else
        {
            cbDelItem.setChecked(false);
            cbDelItem.setVisibility(View.INVISIBLE);
            tvModeTemp.setPadding(0,0,0,0);
            Temp.setPadding(150,0,0,0);
            desc.setPadding(0,0,0,0);
        }
        return view;
    }

    public void activateButtons(boolean activate) {
        this.activate = activate;
        notifyDataSetChanged(); //need to call it for the child views to be re-created with buttons.
    }


}
