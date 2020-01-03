package com.albert.okrouter.demo;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.albert.okrouter.annotation.Route;
import com.albert.okrouter.core.OkRouter;

@Route(address = "/Main2Activity")
public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle bundle = getIntent().getExtras();
        ((TextView) findViewById(R.id.tvTitle)).setText(bundle.getString("name", "标题"));

        findViewById(R.id.btn2Click).setOnClickListener(this);
        findViewById(R.id.btn2Click2).setOnClickListener(this);
        findViewById(R.id.btn2Click3).setOnClickListener(this);
        findViewById(R.id.btn2Click4).setOnClickListener(this);
        findViewById(R.id.btn2Click5).setOnClickListener(this);
        findViewById(R.id.btn2Click6).setOnClickListener(this);
        findViewById(R.id.btn2Click7).setOnClickListener(this);
        findViewById(R.id.btn2Click8).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (v.getId()) {
            case R.id.btn2Click:
                OkRouter.getInstance().build("router://module1/Model1TestActivity?name=albert&age=18").navigation();
                break;
            case R.id.btn2Click2:
                Uri uri = Uri.parse("router://module1/Model1TestActivity?name=albert&age=18");
                OkRouter.getInstance().build(uri).navigation();
                break;
            case R.id.btn2Click3:
                OkRouter.getInstance().build("router%3a%2f%2fmodule1%2fModel1TestActivity%3fname%3dalbert%26age%3d18").navigation();
                break;
            case R.id.btn2Click4:
                OkRouter.getInstance().build("router://module1/Model1TestActivity?name=albert&age=18").navigation(this);
                break;
            case R.id.btn2Click5:

                break;
            case R.id.btn2Click6:
                OkRouter.getInstance().build("/WebViewTestActivity").putString("1", "2").navigation();
                break;
            case R.id.btn2Click7:
                OkRouter.getInstance().build("/app/WebViewProcessActivity").navigation();
                break;
            case R.id.btn2Click8:
                OkRouter.getInstance().build("/NotActivityTest").navigation();
                break;
        }
    }
}
