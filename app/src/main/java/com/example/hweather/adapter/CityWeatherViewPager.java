package com.example.hweather.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hweather.FragmentCity;

import java.util.ArrayList;

public class CityWeatherViewPager extends FragmentStatePagerAdapter {

    private ArrayList<FragmentCity> fragmentCities;

    public CityWeatherViewPager(@NonNull FragmentManager fm,ArrayList<String> mListNameCity) {
        super(fm);
        this.fragmentCities = new ArrayList<>();
        for (int i = 0; i < mListNameCity.size(); i++)
        {
            FragmentCity city = FragmentCity.newInstance(mListNameCity.get(i));
            fragmentCities.add(city);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentCities.get(position);
    }

    @Override
    public int getCount() {
        return fragmentCities.size();
    }
}
