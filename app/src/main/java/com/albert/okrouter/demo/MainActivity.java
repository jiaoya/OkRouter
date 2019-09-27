package com.albert.okrouter.demo;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.albert.okrouter.annotation.Route;
import com.albert.okrouter.core.OkRouter;

@Route(adress = "/MainActivity")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnClick).setOnClickListener(this);
        findViewById(R.id.btnClick1).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (v.getId()) {
            case R.id.btnClick:
                OkRouter.getInstance().build("/Main2Activity").putString("name", "跳转测试").navigation();
                break;
            case R.id.btnClick1:
                OkRouter.getInstance().build("/ProcessTestActivity").navigation();
                break;
        }
    }
}
