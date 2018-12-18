package com.gjn.swipebackview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gjn.statusbarlibrary.StatusBarUtils;
import com.gjn.swipebacklibrary.SwipeHelper;

/**
 * @author gjn
 * @time 2018/8/29 14:45
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected SwipeHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getID());

        StatusBarUtils.statusBarMode(this, Color.DKGRAY);
        StatusBarUtils.setContentViewFitsSystemWindows(this, true);

        init();
    }

    protected void init() {
        helper = new SwipeHelper(this);
        helper.translucentWindowsBackground();
    }

    protected abstract int getID();
}
