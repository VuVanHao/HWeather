package com.example.hweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hweather.adapter.WeatherDaysRCAdapter;
import com.example.hweather.adapter.WeatherHoursRCAdapter;
import com.example.hweather.databinding.FragmentCityBinding;
import com.example.hweather.fragment.FragmentNote;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCity extends Fragment {

    DataWeatherCity dataWeatherCity;
    IWeatherServices iWeatherServices;
    private String apiKey = "91aea4d275c0cd1c8e86c00186ceed9c";
    String name = "";
    ImageView imgWeather;
    WeatherHours weatherHours;
    ArrayList<WeatherHours> arrayListWeatherHours = new ArrayList<>();
    WeatherHoursRCAdapter weatherHoursRCAdapter;
    RecyclerView rvHours;

    WeatherDays weatherDays;
    ArrayList<WeatherDays> arrayListWeatherDays = new ArrayList<>();
    WeatherDaysRCAdapter weatherDaysRCAdapter;
    RecyclerView rvDays;
    float tempMin = 0;
    float tempMax = 0;
    TextView tvTempMin,tvTempMax,tvdo1,tvTypeDeg,tv2,tv3,tvnote;
    TextView tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12,tv13,tv14;
    int temp = 0;
    private ArrayList<BarEntry> dataTempMax = new ArrayList<>();
    private ArrayList<BarEntry> dataTempMin = new ArrayList<>();
    int countData = 0;
    int countDataHours = 1;
    BarChart mpBarChart;
    LineChart mpLineChart;
    ArrayList<Entry> dataTempHours = new ArrayList<>();
    ArrayList<Entry> dataTempHoursHumidity = new ArrayList<>();
    ArrayList<Entry> dataTempHoursCloud = new ArrayList<>();
    ScrollView scrollView;
    ProgressBar progressBar;
    TextView tvLoad;
    RelativeLayout relativeDetails;
    RelativeLayout relativeabc;
    Button hide;
    boolean state = false;



    public FragmentCity() {
        // Required empty public constructor
    }


    public static FragmentCity newInstance(DataWeatherCity dataWeatherCity) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String data = gson.toJson(dataWeatherCity,DataWeatherCity.class);
        FragmentCity fragmentCity = new FragmentCity();
        args.putString("CITY_DATA",data);
        fragmentCity.setArguments(args);
        return fragmentCity;
    }

    public static FragmentCity newInstance(String name)
    {
        Bundle args = new Bundle();
        FragmentCity fragmentCity = new FragmentCity();
        args.putString("NAME_DATA",name);
        fragmentCity.setArguments(args);
        return fragmentCity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("NAME_DATA",null);
            if (name != null)
            {
                iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
            }
        }
    }

    private void loadCityByName(String city,FragmentCityBinding binding,String lang)
    {
        scrollView.setVisibility(View.INVISIBLE);
        tvLoad.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        iWeatherServices.getWeatherByNameCity(city,lang,apiKey).enqueue(new Callback<DataWeatherCity>() {
            @Override
            public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {

                if (response.code() == 200)
                {
                    scrollView.setVisibility(View.VISIBLE);
                    tvLoad.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (getActivity() != null)
                    {
                        dataWeatherCity = response.body();
                        binding.setDataWeatherCity(dataWeatherCity);
                        Glide.with(getContext()).load(dataWeatherCity.getList().get(2).getWeather().get(0).getIcon()).into(imgWeather);
                        arrayListWeatherDays.clear();
                        arrayListWeatherHours.clear();
                        dataTempMin.clear();
                        dataTempMax.clear();
                        dataTempHours.clear();
                        dataTempHoursHumidity.clear();
                        dataTempHoursCloud.clear();
                        getData();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Error Loaded !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DataWeatherCity> call, Throwable t) {
                Log.d("Loi ne : ", t.getMessage());
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCityBinding binding = DataBindingUtil.inflate(inflater,R.layout.fragment_city,container,false);
        View view  = binding.getRoot();
        imgWeather = view.findViewById(R.id.imgWeather);
        rvHours = view.findViewById(R.id.rvTimeInDay);
        rvDays = view.findViewById(R.id.rvDayInWeek);
        tvTempMin = view.findViewById(R.id.tvMaxTemp);
        tvTempMax = view.findViewById(R.id.tvMinTemp);
        initLang(view);
        mpBarChart = view.findViewById(R.id.line_chart_5days);
        mpLineChart = view.findViewById(R.id.line_chart_hours);
        String lang = MySharedPreferences.getLanguage(getContext());
        loadCityByName(name,binding,lang);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initLang(View view) {
        tvnote = view.findViewById(R.id.tvnote);
        tvdo1 = view.findViewById(R.id.tvdo1);
        tvTypeDeg = view.findViewById(R.id.tvTypeDeg);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        tv4 = view.findViewById(R.id.tv4);
        tv5 = view.findViewById(R.id.tv5);
        tv6 = view.findViewById(R.id.tv6);
        tv7 = view.findViewById(R.id.tv7);
        tv8 = view.findViewById(R.id.tv8);
        tv9 = view.findViewById(R.id.tv9);
        tv10 = view.findViewById(R.id.tv10);
        tv11 = view.findViewById(R.id.tv11);
        tv12 = view.findViewById(R.id.tv12);
        tv13 = view.findViewById(R.id.tv13);
        tv14 = view.findViewById(R.id.tv14);
        progressBar = view.findViewById(R.id.progress_circular);
        tvLoad = view.findViewById(R.id.tvLoad);
        scrollView = view.findViewById(R.id.scroll);
        relativeDetails = view.findViewById(R.id.details);
        relativeabc = view.findViewById(R.id.abc);
        hide = view.findViewById(R.id.btnReadmore);
        tvnote.setVisibility(View.GONE);
        relativeDetails.setVisibility(View.GONE);
        relativeabc.setVisibility(View.GONE);
        state = false;
        String lang1 = MySharedPreferences.getLanguage(getContext());
        if (Objects.equals(lang1, "vi"))
        {
            hide.setText("Thêm");
            tvLoad.setText("Đang tải...");
        }
        else
        {
            hide.setText("Read more");
            tvLoad.setText("Loading...");
        }
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!state)
                {
                    tvnote.setVisibility(View.VISIBLE);
                    relativeDetails.setVisibility(View.VISIBLE);
                    relativeabc.setVisibility(View.VISIBLE);
                    String lang1 = MySharedPreferences.getLanguage(getContext());
                    if (Objects.equals(lang1, "vi"))
                    {
                        hide.setText("Ẩn");
                    }
                    else
                    {
                        hide.setText("Hide");
                    }
                    state = true;
                }
                else
                {
                    tvnote.setVisibility(View.GONE);
                    relativeDetails.setVisibility(View.GONE);
                    relativeabc.setVisibility(View.GONE);
                    String lang1 = MySharedPreferences.getLanguage(getContext());
                    if (Objects.equals(lang1, "vi"))
                    {
                        hide.setText("Thêm");
                    }
                    else
                    {
                        hide.setText("Read more");
                    }
                    state = false;
                }

            }
        });
        //progressBar = view.findViewById(R.id.progress_circular);
        int Unit = MySharedPreferences.getTempUnit(getContext());
        String lang = MySharedPreferences.getLanguage(getContext());
        if (Unit == 0)
        {
            tvTypeDeg.setText("C");
            tv2.setText("°C / ");
            tv3.setText("°C");
        }
        else
        {
            tvTypeDeg.setText("F");
            tv2.setText("°F / ");
            tv3.setText("°F");
        }

        if (Objects.equals(lang, "vi"))
        {
            tvdo1.setText("độ ");
            tvnote.setText("Thông tin chi tiết");
            tv4.setText("Độ ẩm");
            tv5.setText("Áp suất");
            tv6.setText("Tốc độ gió");
            tv7.setText("Hướng gió");
            tv8.setText("Tầm nhìn");
            tv9.setText("Độ bao phủ");
            tv10.setText("Bình minh");
            tv11.setText("Hoàng hôn");
            tv12.setText("Lượng mưa");
            tv13.setText("Múi giờ");
            tv14.setText("Quốc gia");
        }
        else
        {
            tvdo1.setText("unit ");
            tvnote.setText("Information Details");
            tv4.setText("Humidity");
            tv5.setText("Pressuare");
            tv6.setText("Wind Speed");
            tv7.setText("Wind Direction");
            tv8.setText("Visibility");
            tv9.setText("Cloud");
            tv10.setText("Sunrise");
            tv11.setText("Sunset");
            tv12.setText("Having Rain");
            tv13.setText("Timezone");
            tv14.setText("Country");
        }

    }

    public void getData()
    {
        int Unit = MySharedPreferences.getTempUnit(getContext());
        //TODO getTempMin
        tempMin = dataWeatherCity.getList().get(2).getMain().getTempMin();
        for(int n = 3;n <=9;n++)
        {
            if (tempMin > dataWeatherCity.getList().get(n).getMain().getTempMin())
            {
                tempMin = dataWeatherCity.getList().get(n).getMain().getTempMin();
            }

        }
        if (Unit == 0)
        {
            tvTempMin.setText(String.valueOf(Math.round(tempMin - 273)));
        }
        else
        {
            tvTempMin.setText(String.valueOf(Math.round(1.8 * tempMin - 459.67)));
        }


        //TODO getTempMax

        tempMax = dataWeatherCity.getList().get(2).getMain().getTempMax();
        for(int m = 3;m <=9;m++)
        {
            if (tempMax < dataWeatherCity.getList().get(m).getMain().getTempMax())
            {
                tempMax = dataWeatherCity.getList().get(m).getMain().getTempMax();
            }

        }
        if (Unit == 0)
        {
            tvTempMax.setText(String.valueOf(Math.round(tempMax - 273)));
        }
        else
        {
            tvTempMax.setText(String.valueOf(Math.round(1.8 * tempMax - 459.67)));
        }
        dataTempHours.add(new Entry(0,Float.parseFloat(FormatData.convertToStringTemp(dataWeatherCity.getList().get(2).getMain().getTemp()))));
        dataTempHoursHumidity.add(new Entry(0,dataWeatherCity.getList().get(2).getMain().getHumidity()));
        dataTempHoursCloud.add(new Entry(0,dataWeatherCity.getList().get(2).getClouds().getAll()));
        for (int i = 2; i <= 9;i++)
        {
            weatherHours = new WeatherHours();
            weatherHours.setTime(cutString(dataWeatherCity.getList().get(i).getDtTxt()));
            weatherHours.setImgWeather(dataWeatherCity.getList().get(i).getWeather().get(0).getIcon());
            weatherHours.setHumidity(dataWeatherCity.getList().get(i).getMain().getHumidity());
            weatherHours.setTemp(dataWeatherCity.getList().get(i).getMain().getTemp());
            arrayListWeatherHours.add(weatherHours);
            dataTempHours.add(new Entry(countDataHours,Float.parseFloat(FormatData.convertToStringTemp(weatherHours.getTemp()))));
            dataTempHoursHumidity.add(new Entry(countDataHours,weatherHours.getHumidity()));
            dataTempHoursCloud.add(new Entry(countDataHours,dataWeatherCity.getList().get(i).getClouds().getAll()));
            countDataHours++;
        }
        //Log.d("Size ne : ",arrayListWeatherHours.get(0).getTemp() + "");
        weatherHoursRCAdapter = new WeatherHoursRCAdapter(arrayListWeatherHours,getContext());
        rvHours.setAdapter(weatherHoursRCAdapter);
        String TempChart = "";
        String HumidityChart = "";
        String Cloud = "";
        String langChart = MySharedPreferences.getLanguage(getContext());
        if (Objects.equals(langChart, "vi"))
        {
            TempChart = "Nhiệt độ";
            HumidityChart = "Độ ẩm";
            Cloud = "Độ bao phủ";
        }
        else
        {
            TempChart = "Temp";
            HumidityChart = "Humidity";
            Cloud = "Cloud";
        }
        LineDataSet lineDataSet = new LineDataSet(dataTempHours,TempChart); // Drawing mode for this line dataset
        LineDataSet lineDataSet1 = new LineDataSet(dataTempHoursHumidity,HumidityChart);
        LineDataSet lineDataSet2 = new LineDataSet(dataTempHoursCloud,Cloud);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>(); // Returns the drawing mode for this line dataset
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        mpLineChart.setNoDataText("No Data");
        mpLineChart.setNoDataTextColor(Color.RED);

        Description description1 = new Description();
        description1.setText("");
        description1.setTextColor(Color.BLUE);
        description1.setTextSize(15);
        mpLineChart.setDescription(description1);

        //Style Border
        mpLineChart.setDrawBorders(true);
        mpLineChart.setBorderColor(Color.RED);
        mpLineChart.setBorderWidth(1f);


        lineDataSet.setLineWidth(2f);
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet.setColor(Color.RED);
        lineDataSet1.setColor(Color.BLACK);

        String[] Hours = new String[]{" ",arrayListWeatherHours.get(0).getTime(),
                arrayListWeatherHours.get(1).getTime(),
                arrayListWeatherHours.get(2).getTime(),
                arrayListWeatherHours.get(3).getTime(),
                arrayListWeatherHours.get(4).getTime(),
                arrayListWeatherHours.get(5).getTime(),
                arrayListWeatherHours.get(6).getTime(),
                arrayListWeatherHours.get(7).getTime()};

        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(Hours));
        xAxis.setCenterAxisLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);



        LineData dataHours = new LineData(dataSets); //Data object that encapsulates all data associated with a LineChart.
        mpLineChart.setData(dataHours);
        mpLineChart.invalidate();


        int position = 0;
        for (int i = 0; i < dataWeatherCity.getList().size();i++)
        {
            if (Objects.equals(cutString(dataWeatherCity.getList().get(i).getDtTxt()), "12:00"))
            {
                position = i;
                break;
            }
        }
        for (int j = position ;j < dataWeatherCity.getList().size();j = j + 8)
        {
            weatherDays = new WeatherDays();
            if (j < 2)
            {
                weatherDays.setDay(ConvertDay(dataWeatherCity.getList().get(j).getDt()));
                weatherDays.setDescription(dataWeatherCity.getList().get(j).getWeather().get(0).getDescription());
                if (Unit == 0)
                {
                    weatherDays.setTempMin(Math.round(dataWeatherCity.getList().get(0).getMain().getTempMin() - 273));
                }
                else
                {
                    weatherDays.setTempMin(Math.round(1.8 * dataWeatherCity.getList().get(0).getMain().getTempMin() - 459.67));
                }

            }
            else
            {
                weatherDays.setDay(ConvertDay(dataWeatherCity.getList().get(j-2).getDt()));
                weatherDays.setDescription(dataWeatherCity.getList().get(j).getWeather().get(0).getDescription());
                if (Unit == 0)
                {
                    weatherDays.setTempMin(Math.round(dataWeatherCity.getList().get(j-2).getMain().getTempMin() - 273));
                }
                else
                {
                    weatherDays.setTempMin(Math.round(1.8 * dataWeatherCity.getList().get(j-2).getMain().getTempMin() - 459.67));
                }
            }
            if (Unit == 0)
            {
                weatherDays.setTempMax(Math.round(dataWeatherCity.getList().get(j).getMain().getTempMax() - 273));
            }
            else
            {
                weatherDays.setTempMax(Math.round(1.8 * dataWeatherCity.getList().get(j).getMain().getTempMax() - 459.67));
            }
            weatherDays.setImgWeatherDay(dataWeatherCity.getList().get(j).getWeather().get(0).getIcon());
            arrayListWeatherDays.add(weatherDays);
            dataTempMin.add(new BarEntry(countData,weatherDays.getTempMin()));
            dataTempMax.add(new BarEntry(countData,weatherDays.getTempMax()));
            countData++;
        }
        String language = MySharedPreferences.getLanguage(getContext());
        if (Objects.equals(language, "vi"))
        {
            arrayListWeatherDays.get(0).setDay("Hôm nay");
            arrayListWeatherDays.get(1).setDay("Ngày mai");
        }
        else
        {
            arrayListWeatherDays.get(0).setDay("Today");
            arrayListWeatherDays.get(1).setDay("Tomorrow");
        }

        weatherDaysRCAdapter = new WeatherDaysRCAdapter(arrayListWeatherDays,getContext());
        rvDays.setAdapter(weatherDaysRCAdapter);
        String desMin = "";
        String desMax = "";
        String desChart = "";
        String lang = MySharedPreferences.getLanguage(getContext());
        if (Objects.equals(lang, "vi"))
        {
            desMin = "Nhiệt độ thấp nhất";
            desMax = "Nhiệt độ cao nhất";
            desChart = "Biểu đồ nhiệt độ 5 ngày tới";
        }
        else
        {
            desMax = "Maximum temperature";
            desMin = "Minimum temperature";
            desChart = "TempChart for the next 5 days";
        }
        BarDataSet barDataSet1 = new BarDataSet(dataTempMax,desMin);
        barDataSet1.setColor(Color.RED);
        BarDataSet barDataSet2 = new BarDataSet(dataTempMin,desMax);
        barDataSet2.setColor(Color.BLACK);

        BarData data = new BarData(barDataSet1,barDataSet2);

        Description description = new Description();
        description.setText(desChart);
        description.setTextColor(Color.BLUE);
        description.setTextSize(15);
        mpBarChart.setDescription(description);

        mpBarChart.setData(data);
        String[] days = new String[]{arrayListWeatherDays.get(0).getDay(),
                arrayListWeatherDays.get(1).getDay(),
                arrayListWeatherDays.get(2).getDay(),
                arrayListWeatherDays.get(3).getDay(),
                arrayListWeatherDays.get(4).getDay()};

        XAxis xAxis1 = mpBarChart.getXAxis();
        xAxis1.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis1.setCenterAxisLabels(true);
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setGranularity(1);
        xAxis1.setGranularityEnabled(true);

        mpBarChart.setDragEnabled(true);
        mpBarChart.setVisibleXRangeMaximum(3);

        float barSpace = 0.08f;
        float groupSpace = 0.45f;
        data.setBarWidth(0.20f);

        mpBarChart.getXAxis().setAxisMinimum(0);
        mpBarChart.getXAxis().setAxisMaximum(0 + mpBarChart.getBarData().getGroupWidth(groupSpace,barSpace) * 7);
        mpBarChart.getAxisLeft().setAxisMinimum(0);

        mpBarChart.groupBars(0,groupSpace,barSpace);

        mpBarChart.invalidate();

    }

    public String cutString(String str)
    {
        str = str.substring(11,16);
        return str;
    }

    public static String ConvertDay(long input)
    {
        String formattedDtm = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern("E");
            formattedDtm  = Instant.ofEpochSecond(input)
                    .atZone(ZoneId.of("GMT-4"))
                    .format(formatter);
        }
        return formattedDtm;
    }

    public static class FormatData{

        public static String convertToStringTemp(float input)
        {
            int Unit = MySharedPreferences.getTempUnit(null);
            double output;
            if (Unit == 0)
            {
                output = (int) Math.round(input) - 273;
            }
            else {
                output = 1.8 * (int) Math.round(input) - 459.67;
            }

            return Math.round(output) + "";
        }


        public static String convertToString(float input)
        {
            return input + "";
        }


        public static String convertNameCity(String name,String country)
        {
            return name + ", " + country;
        }
    }

    public static class FormatTime {
        public static String ConvertTime(long input)
        {
            String formattedDtm = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter;
                formatter = DateTimeFormatter.ofPattern("HH:mm");
                formattedDtm  = Instant.ofEpochSecond(input)
                        .atZone(ZoneId.of("GMT-4"))
                        .format(formatter);
            }
            return formattedDtm;
        }




        public static String ConvertDayTime()
        {
            DateTimeFormatter dtf = null;
            LocalDateTime now;
            String current = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                dtf = DateTimeFormatter.ofPattern("E, dd/MM/yyyy");
                now = LocalDateTime.now();
                current = dtf.format(now);
            }

            return current;
        }

    }
}