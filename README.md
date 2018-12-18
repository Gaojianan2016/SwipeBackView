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
    implementation 'com.github.Gaojianan2016:SwipeBackView:1.0.1'
}
```

# 基本使用

5.0以下 请加入style
```
<item name="android:windowIsTranslucent">true</item>
```

BaseActivity
```
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
```

MainActivity **建议不要在主Activity下加入滑动关闭**
```
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
```

Senc
```
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

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Senc.this, LoginActivity.class));
            }
        });

    }
}
```
LoginActivity
```
package com.gjn.swipebackview;

import android.os.Bundle;

public class LoginActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getID() {
        return R.layout.activity_login;
    }

}
```
