package com.simplify.android.sdk.view;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.simplify.android.sdk.Utils;
import com.simplify.android.sdk.model.Card;

public class CardCvcEditText extends SimplifyEditText
{
    Card.Type mType = Card.Type.UNKNOWN;

    public CardCvcEditText(Context context)
    {
        super(context);
        init();
    }

    public CardCvcEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CardCvcEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        addTextChangedListener(mTextWatcher);
        setOnFocusChangeListener(mFocusChangeListener);
    }

    public void setType(Card.Type type)
    {
        mType = type;
        mTextWatcher.afterTextChanged(getText());
    }

    private TextWatcher mTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
            // clean the number
            String text = editable.toString();

            // prevent too many digits
            if (text.length() > mType.getCvcLength()) {
                text = text.substring(0, mType.getCvcLength());
            }

            // replace text with formatted
            removeTextChangedListener(this);
            setText(text);
            setSelection(text.length());
            addTextChangedListener(this);

            // set the view state (changes background color)
            if (Utils.validateCardCvc(text, mType)) {
                setState(State.Valid);
            }
            else {
                setState(State.Neutral);
            }
        }
    };

    private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            if (!hasFocus) {
                String text = getText().toString();
                if (text.length() == 0) {
                    setState(State.Neutral);
                }
                else {
                    if (Utils.validateCardCvc(text, mType)) {
                        setState(State.Valid);
                    }
                    else {
                        setState(State.Invalid);
                    }
                }
            }
        }
    };
}
