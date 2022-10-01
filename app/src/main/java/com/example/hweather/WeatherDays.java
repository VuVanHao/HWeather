package com.example.hweather;

public class WeatherDays {
    String day,description,imgWeatherDay;
    float tempMin,tempMax;

    public WeatherDays(String day, String description, String imgWeatherDay, float tempMin, float tempMax) {
        this.day = day;
        this.description = description;
        this.imgWeatherDay = imgWeatherDay;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public WeatherDays() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgWeatherDay() {
        return imgWeatherDay;
    }

    public void setImgWeatherDay(String imgWeatherDay) {
        this.imgWeatherDay = imgWeatherDay;
    }

    public float getTempMin() {
        return tempMin;
    }

    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }
}
