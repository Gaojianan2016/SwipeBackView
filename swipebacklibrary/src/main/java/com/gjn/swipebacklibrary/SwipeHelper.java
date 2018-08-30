package com.gjn.swipebacklibrary;

import android.app.Activity;
import android.support.annotation.NonNull;
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


    public SwipeHelper(Activity act) {
        activity = act;
    }

    public void bindActivity(){
        swipeBackLayout = new SwipeBackLayout(activity);

        if (swipeBackListenr == null) {
            swipeBackLayout.setSwipeBackListenr(new SwipeBackLayout.SwipeBackListenr() {
                @Override
                public void onFinish() {
                    activity.finish();
                    activity.overridePendingTransition(0,0);
                }
            });
        }else {
            swipeBackLayout.setSwipeBackListenr(swipeBackListenr);
        }
        if (activity.isTaskRoot()) {
            swipeBackLayout.initDecorViewChild();
        }else {
            swipeBackLayout.attachToActivity();
        }
    }

    public void unBindActivity(){
        if (!activity.isTaskRoot()) {
            swipeBackLayout.unAttachToActivity();
        }
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    public SwipeHelper setSwipeBackListenr(SwipeBackLayout.SwipeBackListenr swipeBackListenr) {
        this.swipeBackListenr = swipeBackListenr;
        return this;
    }
}
