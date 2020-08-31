package com.kandi.dell.nscarlauncher.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomRoTextViewRo extends TextView
{

    public CustomRoTextViewRo(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    //重写设置字体方法
    @Override
    public void setTypeface(Typeface tf)
    {
        tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Helvetica-Narrow.ttf");
        super.setTypeface(tf);
    }
}