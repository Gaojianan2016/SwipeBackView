package com.gjn.swipebacklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author gjn
 * @time 2018/8/29 16:06
 */

public class SwipeBackLayout extends FrameLayout {
    private static final String TAG = "SwipeBackLayout";

    public static final int SWIPE_WIDTH = 15;

    private ViewDragHelper mViewDragHelper;
    private Point curPoint = new Point();
    private SwipeBackListenr swipeBackListenr;

    private ViewGroup mDecorView;
    private View mDecorChild;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, new DragCallback());
        //left边缘才能触发
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

        mDecorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        mDecorChild = mDecorView.getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //固定写法
        if (ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //固定写法
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        //固定写法
        //此方法用于自动滚动,比如自动回滚到默认位置.
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    public void setSwipeBackListenr(SwipeBackListenr swipeBackListenr) {
        this.swipeBackListenr = swipeBackListenr;
    }

    public void attachToActivity() {
        initDecorViewChild();
        mDecorView.removeView(mDecorChild);
        addView(mDecorChild);
        mDecorView.addView(this);
    }

    public void unAttachToActivity(){
        removeAllViews();
        mDecorView.removeView(this);
        mDecorView.addView(mDecorChild);
    }

    public void initDecorViewChild(){
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        int background = typedArray.getResourceId(0,0);
        typedArray.recycle();
        mDecorChild.setBackgroundResource(background);
        mDecorChild.setBackgroundColor(Color.parseColor("#ffffffff"));
    }

    class DragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return false;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if(left < 0){
                left = 0;
            }
            curPoint.x = left;
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            curPoint.y = top;
            return 0;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            //超过1/2
            if (curPoint.x > getWidth() / 2){
                //直接移动到最右边
                mViewDragHelper.settleCapturedViewAt(getWidth(), 0);
            }else {
                //回弹
                mViewDragHelper.settleCapturedViewAt(0, 0);
            }
            curPoint.x = 0;
            curPoint.y = 0;
            invalidate();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            //这里去进行控件捕获
            if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                mViewDragHelper.captureChildView( getChildAt(0), pointerId);
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            if (left >= getWidth()) {
                if (swipeBackListenr != null) {
                    swipeBackListenr.onFinish();
                }
            }
        }
    }

    public interface SwipeBackListenr{
        void onFinish();
    }
}
