package com.gjn.swipebacklibrary;

import android.app.Activity;
import android.util.Log;

/**
 * @author gjn
 * @time 2018/8/29 17:23
 */

public class SwipeHelper {
    private static final String TAG = "SwipeHelper";

    private Activity activity;
    private SwipeBackLayout swipeBackLayout;
    private SwipeBackLayout.SwipeBackListenr swipeBackListenr;


    public SwipeHelper(Activity activity) {
        this.activity = activity;
    }

    public void onActivityCreate(){
        swipeBackLayout = new SwipeBackLayout(activity);
        swipeBackLayout.setSwipeBackListenr(swipeBackListenr);
//        swipeBackLayout.attachToActivity();
        if (activity.isTaskRoot()) {
            Log.d(TAG, activity.getClass().getSimpleName()+"不绑定");
            swipeBackLayout.initDecorViewChild();
        }else {
            Log.d(TAG, activity.getClass().getSimpleName()+"绑定侧滑");
            swipeBackLayout.attachToActivity();
        }
    }

    public SwipeHelper setSwipeBackListenr(SwipeBackLayout.SwipeBackListenr swipeBackListenr) {
        this.swipeBackListenr = swipeBackListenr;
        return this;
    }
}
