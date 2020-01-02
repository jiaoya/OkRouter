package com.albert.okrouter.module1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.albert.okrouter.annotation.Route;
import com.albert.okrouter.core.OkRouter;

@Route(adress = "router://module1/Model1TestActivity")
public class Model1TestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model1_test);
        findViewById(R.id.btnClick).setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        String a = bundle.getString("age", "00000") + bundle.getString("weight", "50");
        ((TextView) findViewById(R.id.tvTitle)).setText(a);
    }

    @Override
    public void onClick(View v) {
        OkRouter.getInstance().build("/app/Main3Activity").navigation(this);
    }
}
