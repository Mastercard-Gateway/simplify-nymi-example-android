package com.simplify.android.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.simplify.android.sdk.Utils;


class CardExpirationEditText extends SimplifyEditText
{
    public CardExpirationEditText(Context context)
    {
        super(context);
        init();
    }

    public CardExpirationEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    @SuppressLint("Instantiatable")
	public CardExpirationEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }


    private void init()
    {
        addTextChangedListener(mTextWatcher);
        setOnFocusChangeListener(mFocusChangeListener);
    }


    public String getMonth()
    {
        String text = getText().toString();
        if (text.contains("/")) {
            return text.substring(0, text.indexOf("/"));
        }
        return text;
    }

    public String getYear()
    {
        String text = getText().toString();
        if (text.contains("/")) {
            return text.substring(text.indexOf("/") + 1);
        }
        return "";
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

        /**
         * Formats the input text by placing a slash at the appropriate index
         * to separate month and year.
         * @param s
         */
        @Override
        public void afterTextChanged(Editable s)
        {
            // remove all non-digits and multiple leading zero's from the text
            String text = s.toString().replaceAll("[^\\d]+", "").replaceAll("^0{2,}", "0");

            // prepend '0' if first digit greater than 0
            if (text.length() > 0 && text.length() < 4 && Integer.parseInt(text.substring(0, 1)) > 0) {
                text = "0" + text;
            }

            // if there are more than 4 digits, allow a fifth only if text starts with a '0'
            if (text.length() > 4) {
                text = text.substring(0, text.startsWith("0") ? 5 : 4);
            }

            // init slash index
            int slashIndex = 0;

            // offset the slash based on # of characters and prepending '0'
            if (text.length() == 3) {
                if (text.startsWith("0")) {
                    slashIndex = 2;
                }
                else {
                    slashIndex = 1;
                }
            }
            else if (text.length() == 4) {
                // drop last digit if invalid month
                if (Integer.parseInt(text.substring(0, 2)) > 12) {
                    text = text.substring(0, 3);
                    slashIndex = 1;
                }
                else {
                    slashIndex = 2;
                }
            }
            else if (text.length() == 5) {
                // drop last digit if invalid month
                if (Integer.parseInt(text.substring(1, 3)) > 12) {
                    text = text.substring(0, 4);
                }
                // drop the leading '0'
                else {
                    text = text.substring(1, 5);
                }
                slashIndex = 2;
            }

            // format the text with slash
            if (slashIndex > 0) {
                text = text.substring(0, slashIndex) + "/" + text.substring(slashIndex);
            }

            // set the text
            removeTextChangedListener(this);
            setText(text);
            setSelection(text.length());
            addTextChangedListener(this);

            // update the validation state
            if (Utils.validateCardExpiration(getMonth(), getYear())) {
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
                String text = getText().toString().trim();
                if (text.length() == 0) {
                    setState(State.Neutral);
                }
                else if (Utils.validateCardExpiration(getMonth(), getYear())) {
                    setState(State.Valid);
                }
                else {
                    setState(State.Invalid);
                }
            }
        }
    };
}
