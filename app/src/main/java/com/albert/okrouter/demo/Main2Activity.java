package com.albert.okrouter.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.albert.okrouter.annotation.Route;

@Route(adress = "/Main2Activity")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
