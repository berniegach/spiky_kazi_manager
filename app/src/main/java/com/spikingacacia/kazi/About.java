package com.spikingacacia.kazi;

import android.content.Context;
import androidx.preference.Preference;
import android.util.AttributeSet;

public class About extends Preference
{
    public About(Context context)
    {
        super(context);
        setLayoutResource(R.layout.about);
    }

    public About(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setLayoutResource(R.layout.about);
    }
    public About(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        setLayoutResource(R.layout.about);

    }
}
