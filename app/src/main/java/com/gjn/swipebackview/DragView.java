package com.gjn.swipebackview;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author gjn
 * @time 2018/8/30 11:02
 */

public class DragView extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private Point point = new Point();
    private View red;

    private boolean isOpen;

    public DragView(@NonNull Context context) {
        this(context, null);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        viewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                point.x = left;
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                point.y = top;
                return top;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                viewDragHelper.smoothSlideViewTo(red, getWidth() - red.getWidth() , 0);
                invalidate();
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                if (ViewDragHelper.EDGE_RIGHT == edgeFlags) {
                    viewDragHelper.captureChildView(red, pointerId);
                }
            }
        });

        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
