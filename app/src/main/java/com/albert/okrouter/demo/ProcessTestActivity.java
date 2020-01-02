package com.albert.okrouter.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.albert.okrouter.annotation.Route;
import com.albert.okrouter.core.OkRouter;
import com.albert.okrouter.provide.ActionCallback;
import com.albert.okrouter.provide.ActionResult;
import com.albert.okrouter.thread.RouterScheduler;


@Route(adress = "/ProcessTestActivity")
public class ProcessTestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProcessTestActivity.class.getSimpleName();
    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_test);

        tvShow = findViewById(R.id.tvShow);
        findViewById(R.id.btnClick).setOnClickListener(this);
        findViewById(R.id.btnClick1).setOnClickListener(this);
        findViewById(R.id.btnClick2).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (v.getId()) {
            case R.id.btnClick:
                OkRouter.getInstance()
                        .bind("com.albert.okrouter.demo", "AppTestAction")
                        .getAction(new ActionCallback() {
                            @Override
                            public void result(ActionResult result) {
                                tvShow.setText("结果：" + result.getStringData());
                            }

                            @Override
                            public void error(Exception e) {
                                Log.e(TAG, e.toString() + "");
                            }
                        });
                break;
            case R.id.btnClick1:
                AppTestAction1 appTestAction1 = (AppTestAction1) OkRouter.getInstance().bind("AppTestAction1").getAction();
                ActionResult result = appTestAction1.invoke(this, null);
                tvShow.setText("获取接口结果：" + result.getStringData());
                Log.e(TAG, result.getStringData());
                break;
            case R.id.btnClick2:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkRouter.getInstance()
                                .bind("com.albert.okrouter.module1", "Model1TestAction")
                                .putString("test", "我是测试2")
                                .callbackOn(RouterScheduler.MAIN)
                                .getAction(new ActionCallback() {
                                    @Override
                                    public void result(ActionResult result) {
                                        Log.e(TAG, result.getStringData());
                                        tvShow.setText("结果：" + result.getStringData());
                                    }

                                    @Override
                                    public void error(Exception e) {
                                        Log.e(TAG, e.toString() + "");
                                    }
                                });
                    }
                }).start();
                break;
        }

    }
}
