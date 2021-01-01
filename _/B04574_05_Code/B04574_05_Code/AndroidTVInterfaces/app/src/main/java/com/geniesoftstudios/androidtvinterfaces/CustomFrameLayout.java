package com.geniesoftstudios.androidtvinterfaces;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Steven Daniel on 12/06/2015.
 */
public class CustomFrameLayout extends FrameLayout {

    public CustomFrameLayout(Context context) {
        this(context, null, 0);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}