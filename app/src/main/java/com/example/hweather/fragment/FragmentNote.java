package com.example.hweather.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hweather.R;
import com.example.hweather.utils.MySharedPreferences;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentNote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNote extends Fragment {

    TextView note1,note2;
    Button btnCheck;

    public FragmentNote() {
        // Required empty public constructor
    }

    public static FragmentNote newInstance() {
        FragmentNote fragment = new FragmentNote();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        note1 = view.findViewById(R.id.note1);
        note2 = view.findViewById(R.id.note2);
        btnCheck = view.findViewById(R.id.btnCheck);
        String lang = MySharedPreferences.getLanguage(getContext());
        if (Objects.equals(lang, "vi"))
        {
            btnCheck.setText("Kiểm tra");
            note1.setText("Không có kết nối mạng !!!");
            note2.setText("Vui lòng kiểm tra kết nối mạng và khởi động lại ứng dụng ...");
        }
        else
        {
            btnCheck.setText("Check");
            note1.setText("No network connection !!!");
            note2.setText("Please check the network connection and restart the app ...");
        }
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
            }
        });
        return view;
    }
}