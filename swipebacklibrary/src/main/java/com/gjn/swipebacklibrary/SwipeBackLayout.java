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
            ViewCompat.postInvalidateOnAnimation(this);
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
            //child 表示想要滑动的view
            //pointerId 表示触摸点的id, 比如多点按压的那个id
            //返回值表示,是否可以capture,也就是是否可以滑动.可以根据不同的child决定是否可以滑动
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //child 表示当前正在移动的view
            //left 表示当前的view正要移动到左边距为left的地方
            //dx 表示和上一次滑动的距离间隔
            //返回值就是child要移动的目标位置.可以通过控制返回值,从而控制child只能在ViewGroup的范围中移动.
            if(left < 0){
                left = 0;
            }
            curPoint.x = left;
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            //child 表示当前正在移动的view
            //top 表示当前的view正要移动到上边距为top的地方
            //dx 表示和上一次滑动的距离间隔
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
            ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            //这里去进行控件捕获，捕获对象是第一个子布局
            mViewDragHelper.captureChildView( getChildAt(0), pointerId);
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
