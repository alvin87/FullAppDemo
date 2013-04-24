package com.app43.appclient.module.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class HeaderGallery extends Gallery {
    public HeaderGallery(Context paramContext) {
	super(paramContext);
    }

    public HeaderGallery(Context paramContext, AttributeSet paramAttributeSet) {
	super(paramContext, paramAttributeSet);
    }

    public HeaderGallery(Context paramContext, AttributeSet paramAttributeSet,
	    int paramInt) {
	super(paramContext, paramAttributeSet, paramInt);
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
	return e2.getX() > e1.getX();
    }

    // 用户按下触摸屏、快速移动后松开
    public boolean onFling(MotionEvent paramMotionEvent1,
	    MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
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
    }

    // 用户按下屏幕并拖动 重载
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
	    float distanceY) {
	return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
