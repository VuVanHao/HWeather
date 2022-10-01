package com.example.hweather.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hweather.R;
import com.example.hweather.WeatherDays;
import com.example.hweather.utils.MySharedPreferences;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeatherDaysRCAdapter extends RecyclerView.Adapter<WeatherDaysRCAdapter.WeatherDaysViewHolder> {

    ArrayList<WeatherDays> weatherDays;
    Context context;

    public WeatherDaysRCAdapter(ArrayList<WeatherDays> weatherDays, Context context) {
        this.weatherDays = weatherDays;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherDaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_weatherdays,parent,false);
        return new WeatherDaysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherDaysViewHolder holder, int position) {
        WeatherDays day = weatherDays.get(position);
        Glide.with(context).load(day.getImgWeatherDay()).into(holder.imgWeatherDay);
        holder.day.setText(day.getDay());
        holder.descriptionDay.setText(day.getDescription());
        holder.tempMin.setText(String.valueOf(day.getTempMin()));
        holder.tempMax.setText(String.valueOf(day.getTempMax()));
        int unit = MySharedPreferences.getTempUnit(context);
        if (unit == 0)
        {
            holder.tv2.setText("°C / ");
            holder.tv3.setText("°C");
        }
        else
        {
            holder.tv2.setText("°F / ");
            holder.tv3.setText("°F");
        }
    }

    @Override
    public int getItemCount() {
        return weatherDays.size();
    }

    public class WeatherDaysViewHolder extends RecyclerView.ViewHolder {
        TextView day,descriptionDay,tempMin,tempMax,tv2,tv3;
        ImageView imgWeatherDay;
        public WeatherDaysViewHolder(@NonNull View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.tvday);
            descriptionDay = itemView.findViewById(R.id.tvDescriptionDay);
            tempMin = itemView.findViewById(R.id.tvMinTempDay);
            tempMax = itemView.findViewById(R.id.tvMaxTempDay);
            imgWeatherDay = itemView.findViewById(R.id.imgWeatherDay);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);
        }
    }
}
