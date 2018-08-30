package com.gjn.swipebackview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @author gjn
 * @time 2018/8/29 17:49
 */

public class Senc extends BaseActivity {

    @Override
    protected int getID() {
        return R.layout.act_sec;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        helper.unBindActivity();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Senc.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
