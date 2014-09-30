package com.simplify.android.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.mastercard.lib.R;


class SimplifyEditText extends EditText
{
    private State mState;

    public SimplifyEditText(Context context)
    {
        super(context);
        init();
    }

    public SimplifyEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SimplifyEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        setState(State.Neutral);
    }

    protected void setState(State state)
    {
        mState = state;

        // update background color
        setBackgroundColor(getResources().getColor(getBackgroundColor()));

        invalidate();
    }

    public int getBackgroundColor()
    {
        switch (mState) {
            case Valid:
                return R.color.simplify_green_light;
            case Invalid:
                return R.color.simplify_red_light;
            default:
                return android.R.color.white;
        }
    }


    public enum State
    {
        Neutral, Valid, Invalid
    }
}
