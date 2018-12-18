package com.gjn.swipebacklibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    public static final int DEFAULT_SHADOW_COLOR = 0x99000000;
    public static final int BLACK_COLOR = 0xff000000;
    public static final int WHITE_COLOR = 0xffffffff;

    private ViewGroup mDecorView;
    private View mDecorChild;

    private ViewDragHelper mViewDragHelper;
    private SwipeBackListenr swipeBackListenr;

    private int mDistance = 0;
    private int moveDistance;
    private float mScrollWidth;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, new DragCallback());
        //left边缘才能触发
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        //默认移动150dp距离关闭页面
        setMoveDistance(150);
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mDecorChild != null) {
            mDecorChild.layout(mDistance, 0, mDistance + mDecorChild.getMeasuredWidth(),
                    mDecorChild.getMeasuredHeight());
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        //先绘制本身
        boolean result = super.drawChild(canvas, child, drawingTime);
        //绘制阴影判断
        if (child == mDecorChild && mViewDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawScrim(canvas, child);
        }
        return result;
    }

    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (DEFAULT_SHADOW_COLOR & BLACK_COLOR) >>> 24;
        final int alpha = (int) (baseAlpha * (1 - mScrollWidth));
        final int color = alpha << 24;
        canvas.clipRect(0, 0, child.getLeft(), getHeight());
        canvas.drawColor(color);
    }

    public void setMoveDistance(int moveDistance) {
        if (moveDistance > 15) {
            this.moveDistance = (int) (moveDistance * getResources().getDisplayMetrics().density);
        }
    }

    public void setSwipeBackListenr(SwipeBackListenr swipeBackListenr) {
        this.swipeBackListenr = swipeBackListenr;
    }

    public void attachToActivity() {
        initDecorView();
        mDecorView.removeView(mDecorChild);
        addView(mDecorChild);
        mDecorView.addView(this);
    }

    public void initDecorView() {
        mDecorView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
        mDecorChild = mDecorView.getChildAt(0);
        mDecorChild.setBackgroundColor(WHITE_COLOR);
    }

    public void unAttachToActivity() {
        removeAllViews();
        mDecorView.removeView(this);
        mDecorView.addView(mDecorChild);
    }

    public abstract static class SwipeBackListenr {
        public void onStart() {
        }

        public void onFinish() {
        }
    }

    class DragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return false;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            mDistance = Math.max(0, left);
            return mDistance;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            //超过关闭距离
            if (mDistance > moveDistance) {
                //直接移动到最右边
                mViewDragHelper.settleCapturedViewAt(getWidth(), 0);
            } else {
                //回弹
                mViewDragHelper.settleCapturedViewAt(0, 0);
            }
            invalidate();
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            //这里去进行控件捕获
            if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                mViewDragHelper.captureChildView(mDecorChild, pointerId);
                if (swipeBackListenr != null) {
                    swipeBackListenr.onStart();
                }
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            mScrollWidth = Math.abs((float) left / getWidth());
            if (left >= getWidth()) {
                if (swipeBackListenr != null) {
                    swipeBackListenr.onFinish();
                }
            }
            invalidate();
        }
    }
}
