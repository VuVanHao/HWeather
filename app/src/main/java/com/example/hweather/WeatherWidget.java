package com.example.hweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hweather.datahelper.CityManageHelper;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWidget extends AppWidgetProvider {

    DataWeatherCity dataWeatherCity;
    IWeatherServices iWeatherServices;
    private String apiKey = "b52f8385cdf7f4dab2f09a10d9b9d967";
    CityManageHelper cityManageHelper;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds)
        {
            Intent intent = new Intent(context, MainActivity.class);


            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_widget);

            remoteViews.setOnClickPendingIntent(R.id.tv_name, pendingIntent);

            remoteViews.setOnClickPendingIntent(R.id.tv_temperature, pendingIntent);

            remoteViews.setOnClickPendingIntent(R.id.tv_humidity, pendingIntent);

            remoteViews.setOnClickPendingIntent(R.id.tv_Des, pendingIntent);


            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager= AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_widget);
        ComponentName watchWidget = new ComponentName(context, WeatherWidget.class);
        cityManageHelper = new CityManageHelper(context);

        String nameOld = cityManageHelper.getNameRowOne();

        String lang = MySharedPreferences.getLanguage(context);
        iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByNameCity(nameOld,lang,apiKey).enqueue(new Callback<DataWeatherCity>() {
            @Override
            public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                if (response.code() == 200)
                {
                    dataWeatherCity = response.body();
                    remoteViews.setTextViewText(R.id.tv_name, dataWeatherCity.getCity().getName());
                    int unit = MySharedPreferences.getTempUnit(context);
                    if (unit == 0)
                    {
                        remoteViews.setTextViewText(R.id.tv_temperature, String.valueOf(Math.round(dataWeatherCity.getList().get(2).getMain().getTemp() - 273)) + "°C");
                    }
                    else
                    {
                        remoteViews.setTextViewText(R.id.tv_temperature, String.valueOf(Math.round(1.8 * dataWeatherCity.getList().get(2).getMain().getTemp() - 459.67)) + "°F");
                    }
                    remoteViews.setTextViewText(R.id.tv_humidity,"Độ ẩm: " + String.valueOf(dataWeatherCity.getList().get(2).getMain().getHumidity()) + "%");
                    remoteViews.setTextViewText(R.id.tv_Des, dataWeatherCity.getList().get(2).getWeather().get(0).getDescription());
                    appWidgetManager.updateAppWidget(watchWidget, remoteViews);
                }
            }

            @Override
            public void onFailure(Call<DataWeatherCity> call, Throwable t) {
                remoteViews.setInt(R.id.tv_temperature, "setBackgroundResource", R.drawable.bg_red);
            }
        });



    }

}
