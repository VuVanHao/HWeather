package com.example.hweather;

public class WeatherHours {
    String time,imgWeather;
    int humidity;
    float temp;

    public WeatherHours(String time, String imgWeather, int humidity, float temp) {
        this.time = time;
        this.imgWeather = imgWeather;
        this.humidity = humidity;
        this.temp = temp;
    }

    public WeatherHours() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgWeather() {
        return imgWeather;
    }

    public void setImgWeather(String imgWeather) {
        this.imgWeather = imgWeather;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }
}
