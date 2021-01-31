package com.ansdoship.golly.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

public class SoftKeyBoardStateChangeObserver {

    private View rootView;
    private int rootViewVisibleHeight;

    public interface OnSoftKeyBoardStateChangeListener {
        void stateChanged(boolean isShow);
    }

    private OnSoftKeyBoardStateChangeListener mListener;

    public SoftKeyBoardStateChangeObserver(@NonNull Activity activity){
        init(activity);
    }

    private void init(@NonNull Activity activity) {
        rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            int visibleHeight = rect.height();
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            if (rootViewVisibleHeight == visibleHeight) {
                return;
            }
            int ignoreHeight = ActivityUtils.getNavigationBarHeight();
            if (ActivityUtils.isStatusBarShown(activity)) {
                ignoreHeight += ActivityUtils.getStatusBarHeight();
            }
            if (rootViewVisibleHeight - visibleHeight > ignoreHeight) {
                if (mListener != null) {
                    mListener.stateChanged(true);
                }
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            if (visibleHeight - rootViewVisibleHeight > ignoreHeight) {
                if (mListener != null) {
                    mListener.stateChanged(false);
                }
                rootViewVisibleHeight = visibleHeight;
            }
        });
    }

    public void setOnSoftKeyBoardStateChangeListener(OnSoftKeyBoardStateChangeListener onSoftKeyBoardStateChangeListener) {
        mListener = onSoftKeyBoardStateChangeListener;
    }

}
