package com.gjn.swipebacklibrary;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author gjn
 * @time 2018/8/29 17:23
 */

public class SwipeHelper {
    private static final String TAG = "SwipeHelper";

    private Activity activity;
    private SwipeBackLayout swipeBackLayout;
    private SwipeBackLayout.SwipeBackListenr swipeBackListenr;

    private Class translucentConversionListenerClass;
    private Object translucentConversionListener;
    private boolean isTranslucent;

    public SwipeHelper(Activity activity) {
        this.activity = activity;
        this.swipeBackLayout = new SwipeBackLayout(activity);
        if (!activity.isTaskRoot()) {
            bindActivity();
        }
    }

    public SwipeHelper(Activity activity, SwipeBackLayout swipeBackLayout) {
        this.activity = activity;
        this.swipeBackLayout = swipeBackLayout;
    }

    public void bindActivity(){
        if (swipeBackListenr == null) {
            swipeBackLayout.setSwipeBackListenr(new SwipeBackLayout.SwipeBackListenr() {
                @Override
                public void onStart() {
                    hideKeyboard();
                }

                @Override
                public void onFinish() {
                    if (!activity.isFinishing()) {
                        activity.finish();
                        //去除关闭动画
                        activity.overridePendingTransition(0,0);
                    }
                }
            });
        }else {
            swipeBackLayout.setSwipeBackListenr(swipeBackListenr);
        }
        swipeBackLayout.attachToActivity();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void unBindActivity(){
        if (!activity.isTaskRoot()) {
            swipeBackLayout.unAttachToActivity();
        }
    }

    public void setSwipeMoveDistance(int dp){
        if (swipeBackLayout != null) {
            swipeBackLayout.setMoveDistance(dp);
        }
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    public SwipeHelper setSwipeBackListenr(SwipeBackLayout.SwipeBackListenr swipeBackListenr) {
        this.swipeBackListenr = swipeBackListenr;
        return this;
    }

    public void translucentWindowsBackground(){
        isTranslucent = false;
        try {
            //获取透明转换回调类
            if (translucentConversionListenerClass == null) {
                Class[] classArray = Activity.class.getDeclaredClasses();
                for (Class clz : classArray) {
                    if (clz.getSimpleName().contains("TranslucentConversionListener")) {
                        translucentConversionListenerClass = clz;
                    }
                }
            }
            //代理透明转换回调
            if (translucentConversionListener == null && translucentConversionListenerClass != null) {
                translucentConversionListener = Proxy.newProxyInstance(translucentConversionListenerClass.getClassLoader(),
                        new Class[]{translucentConversionListenerClass},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                isTranslucent = true;
                                return null;
                            }
                        });
            }
            //利用反射将窗口转为透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Object options = null;
                try {
                    Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                    getActivityOptions.setAccessible(true);
                    options = getActivityOptions.invoke(this);
                } catch (Exception e) {}
                Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                        translucentConversionListenerClass, ActivityOptions.class);
                convertToTranslucent.setAccessible(true);
                convertToTranslucent.invoke(activity, translucentConversionListener, options);
            }else {
                Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent",
                        translucentConversionListenerClass);
                convertToTranslucent.setAccessible(true);
                convertToTranslucent.invoke(activity, translucentConversionListener);
            }
        }catch (Exception e){
            isTranslucent = true;
        }
        if (translucentConversionListenerClass == null) {
            isTranslucent = true;
        }
        //去除窗口背景
        activity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void recoveryWindowsBackground(){
        try{
            Method convertFromTranslucent = Activity.class.getDeclaredMethod("convertFromTranslucent");
            convertFromTranslucent.setAccessible(true);
            convertFromTranslucent.invoke(activity);
            isTranslucent = false;
        }catch (Exception e){}
    }

    public boolean isTranslucent() {
        return isTranslucent;
    }
}
