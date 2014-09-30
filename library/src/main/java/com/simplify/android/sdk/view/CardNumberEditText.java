package com.simplify.android.sdk.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.mastercard.lib.R;
import com.simplify.android.sdk.Utils;
import com.simplify.android.sdk.model.Card;


public class CardNumberEditText extends SimplifyEditText
{
    public CardNumberEditText(Context context)
    {
        super(context);
        init();
    }

    public CardNumberEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CardNumberEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        addTextChangedListener(mTextWatcher);
        setOnFocusChangeListener(mFocusChangeListener);
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
            String text = editable.toString().replaceAll("[^\\d]+", "");

            // detect the card type
            Card.Type type = Card.Type.detect(text);

            // prevent too many digits
            if (text.length() > type.getMaxLength()) {
                text = text.substring(0, type.getMaxLength());
            }

            // format the number
            String value = type.format(text);

            // replace text with formatted
            removeTextChangedListener(this);
            setText(value);
            setSelection(value.length());
            addTextChangedListener(this);

            // fetch the drawable id
            int drawableId = R.drawable.simplify_cardtype_unknown;
            switch (type) {
                case MASTERCARD:
                    drawableId = R.drawable.simplify_cardtype_mastercard;
                    break;
                case VISA:
                    drawableId = R.drawable.simplify_cardtype_visa;
                    break;
                case AMERICAN_EXPRESS:
                    drawableId = R.drawable.simplify_cardtype_amex;
                    break;
                case DISCOVER:
                    drawableId = R.drawable.simplify_cardtype_discover;
                    break;
                case DINERS:
                    drawableId = R.drawable.simplify_cardtype_diners;
                    break;
                case JCB:
                    drawableId = R.drawable.simplify_cardtype_jcb;
                    break;
            }

            // change the drawable to match type
            setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawableId), null, null, null);

            // set the view state (changes background color)
            if (Utils.validateCardNumber(text, type)) {
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
                String text = getText().toString().replaceAll("[^\\d]+", "");
                if (text.length() == 0) {
                    setState(State.Neutral);
                }
                else {
                    Card.Type type = Card.Type.detect(text);
                    if (Utils.validateCardNumber(text, type)) {
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
