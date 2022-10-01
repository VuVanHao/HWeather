package com.example.hweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hweather.utils.MySharedPreferences;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Setting extends AppCompatActivity {

    @BindView(R.id.UnitTemp)
    TextView unitTemp;
    @BindView(R.id.UnitLang)
    TextView unitLang;
    @BindView(R.id.tvactionbar)
    TextView actionBar;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tvUnit)
    TextView tvUnit;
    @BindView(R.id.tvLang)
    TextView tvLang;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tvVersion)
    TextView version;
    @BindView(R.id.tvPermission)
    TextView tvPermission;
    private ImageView btnBackSetting;
    private LinearLayout settingUnit;
    private LinearLayout settingLang;
    View custom_view,custom_view_lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        int unit = MySharedPreferences.getTempUnit(getApplicationContext());
        if (unit == 0)
        {
            unitTemp.setText("C°");
        }
        else
        {
            unitTemp.setText("F°");
        }

        String lang = MySharedPreferences.getLanguage(getApplicationContext());
        if (Objects.equals(lang, "vi"))
        {
            tvPermission.setText("Chỉnh sửa quyền");
            unitLang.setText("Vietnamese");
            actionBar.setText("Cài đặt");
            tv.setText("Cài đặt chung");
            tvUnit.setText("Đơn vị nhiệt độ");
            tvLang.setText("Ngôn ngữ");
            tv3.setText("Khác");
            version.setText("Phiên bản");
        }
        else
        {
            tvPermission.setText("Setting permission");
            unitLang.setText("English");
            actionBar.setText("Setting");
            tv.setText("General setting");
            tvUnit.setText("Unit temp");
            tvLang.setText("Language");
            tv3.setText("Other");
            version.setText("Version");
        }
    }

    private void initView() {
        btnBackSetting = findViewById(R.id.btnBackSetting);
        settingUnit = findViewById(R.id.settingUnit);
        settingLang = findViewById(R.id.settingLang);
        btnBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this,MainActivity.class);
                startActivity(intent);
            }
        });
        tvPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        settingUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = Setting.this.getLayoutInflater();
                custom_view = inflater.inflate(R.layout.dialog_unit,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                builder.setView(custom_view);
                TextView dialogUnit = custom_view.findViewById(R.id.tvDiaLogUnit);
                RadioGroup rgDiaLogUnit = custom_view.findViewById(R.id.rgDiaLogUnit);
                RadioButton rbDiaLogC = custom_view.findViewById(R.id.rbDiaLogC);
                RadioButton rbDiaLogF = custom_view.findViewById(R.id.rbDiaLogF);
                String lang = MySharedPreferences.getLanguage(getApplicationContext());
                if (Objects.equals(lang, "vi"))
                {
                    dialogUnit.setText("Đơn vị độ");
                }
                else
                {
                    dialogUnit.setText("Unit Temp");
                }
                int unit = MySharedPreferences.getTempUnit(getApplicationContext());
                if (unit == 0)
                {
                    rbDiaLogC.setChecked(true);
                }
                else
                {
                    rbDiaLogF.setChecked(true);
                }
                builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (rbDiaLogC.isChecked())
                        {
                            MySharedPreferences.setTempUnit(getApplicationContext(),0);
                            unitTemp.setText("C°");
                        }
                        else
                        {
                            MySharedPreferences.setTempUnit(getApplicationContext(),1);
                            unitTemp.setText("F°");
                        }
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });

        settingLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = Setting.this.getLayoutInflater();
                custom_view_lang = inflater.inflate(R.layout.dialog_lang,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                builder.setView(custom_view_lang);
                TextView dialogLang = custom_view_lang.findViewById(R.id.tvDiaLogLang);
                RadioGroup rgDiaLogLang = custom_view_lang.findViewById(R.id.rgDiaLogLang);
                RadioButton rbDiaLogVi = custom_view_lang.findViewById(R.id.rbLangVi);
                RadioButton rbDiaLogEn = custom_view_lang.findViewById(R.id.rbLangEn);
                String lang = MySharedPreferences.getLanguage(getApplicationContext());
                if (Objects.equals(lang, "vi"))
                {
                    rbDiaLogVi.setChecked(true);
                    dialogLang.setText("Ngôn ngữ");
                }
                else
                {
                    rbDiaLogEn.setChecked(true);
                    dialogLang.setText("Language");
                }
                builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (rbDiaLogVi.isChecked())
                        {
                            MySharedPreferences.setLanguage(getApplicationContext(),"vi");
                            unitLang.setText("Vietnamese");
                        }
                        else
                        {
                            MySharedPreferences.setLanguage(getApplicationContext(),"en");
                            unitLang.setText("English");
                        }
                        Intent intent = new Intent(Setting.this,Setting.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });


    }

}