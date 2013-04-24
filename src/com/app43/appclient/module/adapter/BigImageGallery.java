package com.app43.appclient.module.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class BigImageGallery extends Gallery {

    public BigImageGallery(Context context) {
        super(context);
    }

    public BigImageGallery(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public BigImageGallery(Context paramContext,
            AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent paramMotionEvent1,
            MotionEvent paramMotionEvent2, float velocityX, float paramFloat2) {
        int kEvent;
        if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2)) {
            // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;

        } else {
            // Otherwise scrolling right
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return super.onFling(paramMotionEvent1, paramMotionEvent2, 0,
                paramFloat2);

        // 每次只滑一张
        // return false;
    }
}
