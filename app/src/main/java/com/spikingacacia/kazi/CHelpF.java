package com.spikingacacia.kazi;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spikingacacia.kazi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CHelpF extends Preference
{


    public CHelpF(Context context)
    {
        super(context);
        setLayoutResource(R.layout.f_chelp);
    }

    public CHelpF(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setLayoutResource(R.layout.f_chelp);
    }
    public CHelpF(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        setLayoutResource(R.layout.f_chelp);

    }

}
