package com.example.hweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hweather.R;
import com.example.hweather.WeatherHours;
import com.example.hweather.utils.MySharedPreferences;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeatherHoursRCAdapter extends RecyclerView.Adapter<WeatherHoursRCAdapter.WeatherHoursViewHolder> {

    ArrayList<WeatherHours> weatherHours;
    Context context;

    public WeatherHoursRCAdapter(ArrayList<WeatherHours> weatherHours, Context context) {
        this.weatherHours = weatherHours;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherHoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_weatherhours,parent,false);
        return new WeatherHoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHoursViewHolder holder, int position) {
        WeatherHours weather = weatherHours.get(position);
        Glide.with(context).load(weather.getImgWeather()).into(holder.imgWeather);
        holder.time.setText(weather.getTime());
        holder.humidity.setText(String.valueOf(weather.getHumidity()));
        int unit = MySharedPreferences.getTempUnit(context);
        if (unit == 0)
        {
            holder.temp.setText(String.valueOf(Math.round(weather.getTemp() - 273)));
        }
        else
        {
            holder.temp.setText(String.valueOf(Math.round(1.8 * weather.getTemp() - 459)));
        }
    }

    @Override
    public int getItemCount() {
        return weatherHours.size();
    }

    public class WeatherHoursViewHolder extends RecyclerView.ViewHolder {
        TextView time,humidity,temp;
        ImageView imgWeather;
        public WeatherHoursViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.tvHours);
            humidity = itemView.findViewById(R.id.humidity);
            temp = itemView.findViewById(R.id.tvTempcurrent);
            imgWeather = itemView.findViewById(R.id.imgWeatherHours);
        }
    }
}
