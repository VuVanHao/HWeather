package com.example.hweather.retroifit;

import com.example.hweather.models.DataWeatherCity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IWeatherServices {
    @GET("forecast?")
    Call<DataWeatherCity> getWeatherByNameCity(@Query("q") String cityName,@Query("lang") String lang,
                                               @Query("appid") String apiKey);

    @GET("forecast?")
    Call<DataWeatherCity> getWeatherByLocation(@Query("lat") String lat,@Query("lon") String lon,@Query("lang") String lang,
                                               @Query("appid") String apiKey);

}
