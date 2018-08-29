# SwipeBackView
# 侧滑帮助类

- 依赖使用
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}


dependencies {
    implementation 'com.github.Gaojianan2016:SwipeBackView:1.0.0'
}
```

# 基本使用
BaseActivity
```
package com.gjn.swipebackview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gjn.statusbarlibrary.StatusBarUtils;
import com.gjn.swipebacklibrary.SwipeBackLayout;
import com.gjn.swipebacklibrary.SwipeHelper;

/**
 * @author gjn
 * @time 2018/8/29 14:45
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getID());

        StatusBarUtils.statusBarMode(this, Color.DKGRAY);
        StatusBarUtils.setContentViewFitsSystemWindows(this, true);

        SwipeHelper helper = new SwipeHelper(this);
        helper.setSwipeBackListenr(new SwipeBackLayout.SwipeBackListenr() {
            @Override
            public void onFinish() {
                finish();
            }
        }).onActivityCreate();

    }

    protected abstract int getID();
}
```
