package com.example.hweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hweather.adapter.ManageCityAdapter;
import com.example.hweather.datahelper.CityManageHelper;
import com.example.hweather.models.DataWeatherCity;
import com.example.hweather.retroifit.IDeleteItemListener;
import com.example.hweather.retroifit.IWeatherServices;
import com.example.hweather.retroifit.RetrofitClient;
import com.example.hweather.utils.MySharedPreferences;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Manage_City extends AppCompatActivity implements IDeleteItemListener {

    @BindView(R.id.LvCityManage)
    ListView lvCityManage;
    @BindView(R.id.btnDel)
    Button btnDel;
    @BindView(R.id.btnCancelDel)
    ImageView btnCancelDel;
    @BindView(R.id.btnSearhAdd)
    Button btnAdd;
    @BindView(R.id.tvDel)
    TextView tvDel;
    @BindView(R.id.tvcontent)
    TextView tvContent;
    @BindView(R.id.tvCountDel)
    TextView tvCountDel;
    @BindView(R.id.contentSearch)
    TextView contentSearch;
    @BindView(R.id.tvactionbarSearch)
    TextView tvactionBarSearch;
    @BindView(R.id.btnBackManage)
    ImageView btnBackManage;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View bottomSheet;
    FloatingActionButton floatingActionButton;
    TextView btnCancelSearch;
    private boolean state = true;

    private ArrayAdapter<String> mAdapter;
    EditText edtSearchName;
    ArrayList<String> nameCityManage;
    CityManageHelper cityManageHelper;
    ManageCityAdapter manageCityAdapter;
    DataWeatherCity dataWeatherCity;
    private final String apiKey = "91aea4d275c0cd1c8e86c00186ceed9c";
    IWeatherServices iWeatherServices;
    final String[] textCity = {"Hà Nội","Bắc Ninh","Vĩnh Phúc","Đà Nẵng","Tuyên Quang","Bắc Giang","Cà Mau","New York","Seoul"};
    ArrayList<String> listItemDel;
    int count = 0;
    String lang = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);
        ButterKnife.bind(this);
        cityManageHelper = new CityManageHelper(this);
        nameCityManage = new ArrayList<>();
        listItemDel = new ArrayList<>();
        lang = MySharedPreferences.getLanguage(getApplicationContext());
        intiView();
        initData();

    }

    @SuppressLint("SetTextI18n")
    private void initLangTemp() {
        if(Objects.equals(lang, "vi"))
        {
            tvContent.setText("Thêm thành phố");
            tvactionBarSearch.setText("Quản lí thành phố");
            tvDel.setText("Đã chọn");
            btnDel.setText("Xóa");
            btnCancelSearch.setText("Hủy");
            btnAdd.setText("Thêm");
            edtSearchName.setHint("Tên thành phố");
            contentSearch.setText("Thành phố hàng đầu");
        }
        else
        {
            tvContent.setText("Add city");
            tvactionBarSearch.setText("Manage city");
            tvDel.setText("Selected");
            btnDel.setText("Remove");
            btnCancelSearch.setText("Cancel");
            btnAdd.setText("Add");
            edtSearchName.setHint("Name city");
            contentSearch.setText("List top city");
        }
    }

    private void initData() {
        nameCityManage.clear();
        nameCityManage.addAll(cityManageHelper.getListCityName());
        manageCityAdapter = new ManageCityAdapter(this,nameCityManage);
        manageCityAdapter.setiDeleteItemListener(this);
        lvCityManage.setAdapter(manageCityAdapter);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameCityManageedt = edtSearchName.getText().toString().trim();
                if (nameCityManage.isEmpty())
                {
                    Toast.makeText(Manage_City.this, "Vui nhập tên thành phố", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
                    iWeatherServices.getWeatherByNameCity(nameCityManageedt,"vi",apiKey)
                            .enqueue(new Callback<DataWeatherCity>() {
                                @Override
                                public void onResponse(Call<DataWeatherCity> call, Response<DataWeatherCity> response) {
                                    if (response.code() == 200)
                                    {
                                        dataWeatherCity = response.body();
                                        ArrayList<String> listNameCity = cityManageHelper.getListCityName();
                                        if (listNameCity.contains(dataWeatherCity.getCity().getName()))
                                        {
                                            cityManageHelper.deleteSearchCityName(dataWeatherCity.getCity().getName());
                                        }
                                        cityManageHelper.addSearchCity(dataWeatherCity.getCity().getName());
                                        nameCityManage.clear();
                                        nameCityManage.addAll(cityManageHelper.getListCityName());
                                        manageCityAdapter = new ManageCityAdapter(getApplicationContext(),nameCityManage);
                                        manageCityAdapter.setiDeleteItemListener(Manage_City.this);
                                        lvCityManage.setAdapter(manageCityAdapter);
                                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        state = true;
                                        edtSearchName.setText("");
                                        try {
                                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(edtSearchName.getWindowToken(), 0);
                                        } catch (Exception e) {

                                        }
                                        manageCityAdapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        Toast.makeText(Manage_City.this, "Không tồn tại thành phố", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<DataWeatherCity> call, Throwable t) {

                                }
                            });
                }
            }
        });
        lvCityManage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Manage_City.this,MainActivity.class);
                intent.putExtra("POS",i);
                startActivity(intent);
            }
        });
        lvCityManage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                manageCityAdapter.activateButtons(true);
                btnDel.setVisibility(View.VISIBLE);
                btnCancelDel.setVisibility(View.VISIBLE);
                tvDel.setVisibility(View.VISIBLE);
                count = 0;
                tvCountDel.setText(String.valueOf(count));
                tvCountDel.setVisibility(View.VISIBLE);
                tvactionBarSearch.setVisibility(View.INVISIBLE);
                btnBackManage.setVisibility(View.INVISIBLE);
                floatingActionButton.setVisibility(View.INVISIBLE);
                btnCancelDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvactionBarSearch.setVisibility(View.VISIBLE);
                        btnBackManage.setVisibility(View.VISIBLE);
                        manageCityAdapter.activateButtons(false);
                        count = 0;
                        tvCountDel.setText(String.valueOf(count));
                        btnDel.setVisibility(View.INVISIBLE);
                        btnCancelDel.setVisibility(View.INVISIBLE);
                        tvDel.setVisibility(View.INVISIBLE);
                        tvCountDel.setVisibility(View.INVISIBLE);
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                });
                return false;
            }
        });
    }

    private void intiView() {
        edtSearchName = findViewById(R.id.edtSearchName);
        btnDel.setVisibility(View.INVISIBLE);
        btnCancelDel.setVisibility(View.INVISIBLE);
        tvDel.setVisibility(View.INVISIBLE);
        tvCountDel.setText(String.valueOf(count));
        tvCountDel.setVisibility(View.INVISIBLE);
        CancelSearch();
        BackManage();
        initFloatAction();
        initGridView();
        initLangTemp();

    }

    private void CancelSearch() {
        btnCancelSearch = findViewById(R.id.btnCancelSearch);
        btnCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                edtSearchName.setText("");
                state = true;
            }
        });
    }

    public void BackManage()
    {
        btnBackManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Manage_City.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(Manage_City.this, listItemDel.size() + "", Toast.LENGTH_SHORT).show();
                for (int i = 1;i < listItemDel.size();i++)
                {
                    String nameCityDel = nameCityManage.get(Integer.parseInt(listItemDel.get(i)));
                    cityManageHelper.deleteSearchCityName(nameCityDel);
                    nameCityManage.remove(nameCityDel);
                    manageCityAdapter.notifyDataSetChanged();
                }
                tvactionBarSearch.setVisibility(View.VISIBLE);
                btnBackManage.setVisibility(View.VISIBLE);
                manageCityAdapter.activateButtons(false);
                count = 0;
                tvCountDel.setText(String.valueOf(count));
                btnDel.setVisibility(View.INVISIBLE);
                btnCancelDel.setVisibility(View.INVISIBLE);
                tvDel.setVisibility(View.INVISIBLE);
                tvCountDel.setVisibility(View.INVISIBLE);
                floatingActionButton.setVisibility(View.VISIBLE);
                listItemDel.clear();
            }
        });
    }
    public void initFloatAction()
    {
        floatingActionButton = findViewById(R.id.flButton);
        bottomSheet = findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    state = true;
                }
            }
        });
    }
    public void initGridView()
    {
        GridView gridView = findViewById(R.id.gvListCity);
        mAdapter = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,textCity);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0 :
                        edtSearchName.setText(textCity[0]);
                        break;
                    case 1 :
                        edtSearchName.setText(textCity[1]);
                        break;
                    case 2 :
                        edtSearchName.setText(textCity[2]);
                        break;
                    case 3 :
                        edtSearchName.setText(textCity[3]);
                        break;
                    case 4 :
                        edtSearchName.setText(textCity[4]);
                        break;
                    case 5 :
                        edtSearchName.setText(textCity[5]);
                        break;
                    case 6 :
                        edtSearchName.setText(textCity[6]);
                        break;
                    case 7 :
                        edtSearchName.setText(textCity[7]);
                        break;
                    case 8 :
                        edtSearchName.setText(textCity[8]);
                        break;
                }
            }
        });
    }


    @Override
    public void iDeteleItem(int i) {

        if (listItemDel.size() == 0)
        {
            listItemDel.add(i+ "");
        }
        if (!listItemDel.contains(i))
        {
            listItemDel.add(i+"");
        }
    }

    @Override
    public void iReadMoreItem(int i) {
        count += i;
        tvCountDel.setText(String.valueOf(count));
    }

    @Override
    public void iDelItemName(String name) {
    }

    @Override
    public void iDelItemUnCheck(int i) {
        listItemDel.remove(i+"");
    }

}