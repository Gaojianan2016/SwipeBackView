package com.gjn.swipebackview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gjn.statusbarlibrary.StatusBarUtils;

public class MainActivity extends BaseActivity {

    @Override
    protected void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.tv_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Senc.class));
            }
        });
    }

    @Override
    protected int getID() {
        return R.layout.activity_main;
    }
}
