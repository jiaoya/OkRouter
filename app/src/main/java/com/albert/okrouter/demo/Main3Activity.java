package com.albert.okrouter.demo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.albert.okrouter.annotation.Route;

@Route(adress = "/app/Main3Activity")
public class Main3Activity extends AppCompatActivity {

    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tvShow = findViewById(R.id.tvShow);
        Bundle bundle = getIntent().getExtras();
        tvShow.setText("我是被拦截的数据" + bundle.getBoolean("point", true));
    }
}
