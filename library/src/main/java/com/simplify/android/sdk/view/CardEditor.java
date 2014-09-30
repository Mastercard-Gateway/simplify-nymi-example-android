package com.simplify.android.sdk.view;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mastercard.lib.R;
import com.simplify.android.sdk.model.Card;

/**
 * Class representing a card editor view, allowing a user
 * to enter and validate card data and optionally display
 * payment amount and success/error messages
 * <p/>
 * See {@link R.styleable#CardEditor CardEditor Attributes}
 */
public class CardEditor extends LinearLayout
{
    Card mCard;
    private double mAmount;

    CardNumberEditText mCardNumberEditText;
    CardExpirationEditText mCardExpirationEditText;
    CardCvcEditText mCardCvcEditText;

    Button mChargeButton;
    Button mCloseButton;

    private LinearLayout mSuccessOverlay;
    private LinearLayout mErrorOverlay;


    // ----------------------------------
    // Init
    // ----------------------------------

    /**
     * Constructs a new CardEditor view
     *
     * @param context The controlling context
     */
    public CardEditor(Context context)
    {
        super(context);
        init(context, null);
    }

    /**
     * Constructs a new CardEditor view
     *
     * @param context The controlling context
     * @param attrs   The view attributes
     */
    public CardEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Constructs a new CardEditor view
     *
     * @param context  The controlling context
     * @param attrs    The view attributes
     * @param defStyle The default style to apply to this view
     */
    public CardEditor(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        // inflate view
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.simplify_cardeditor, this, true);

        // init buttons
        mChargeButton = (Button) view.findViewById(R.id.simplify_btn_charge);
        mCloseButton = (Button) view.findViewById(R.id.simplify_btn_close);

        mSuccessOverlay = (LinearLayout) view.findViewById(R.id.simplify_ll_successoverlay);
        mErrorOverlay = (LinearLayout) view.findViewById(R.id.simplify_ll_erroroverlay);

        // init card number field
        mCardNumberEditText = (CardNumberEditText) view.findViewById(R.id.simplify_et_cardnumber);
        mCardNumberEditText.addTextChangedListener(mCardNumberTextWatcher);

        // init card expiration field
        mCardExpirationEditText = (CardExpirationEditText) view.findViewById(R.id.simplify_et_cardexpiration);
        mCardExpirationEditText.addTextChangedListener(mCardExpirationTextWatcher);

        // init card cvc field
        mCardCvcEditText = (CardCvcEditText) view.findViewById(R.id.simplify_et_cardcvc);
        mCardCvcEditText.addTextChangedListener(mCardCvcTextWatcher);

        // customize view
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardEditor, 0, 0);
        boolean closeable = a.getBoolean(R.styleable.CardEditor_closeable, true);
        float amount = a.getFloat(R.styleable.CardEditor_amount, 0f);
        int themeColor = a.getColor(R.styleable.CardEditor_themeColor, getResources().getColor(R.color.simplify_orange));

        // set customizations
        setCloseable(closeable);
        setAmount(amount);
        setThemeColor(themeColor);

        // init blank card
        reset();
    }


    // ----------------------------------
    // Getters / Setters
    // ----------------------------------

    /**
     * Returns the Card object associated with the view
     *
     * @return A card object
     */
    public Card getCard()
    {
        return mCard;
    }

    /**
     * Sets the associated card object
     *
     * @param card A populated Card object
     */
    public void setCard(Card card)
    {
        if (card == null) {
            return;
        }

        mCard = card;

        mCardNumberEditText.removeTextChangedListener(mCardNumberTextWatcher);
        mCardNumberEditText.setText(card.getNumber());
        mCardNumberEditText.addTextChangedListener(mCardNumberTextWatcher);

        mCardExpirationEditText.removeTextChangedListener(mCardExpirationTextWatcher);
        mCardExpirationEditText.setText(card.getExpiration());
        mCardExpirationEditText.addTextChangedListener(mCardExpirationTextWatcher);

        mCardCvcEditText.removeTextChangedListener(mCardCvcTextWatcher);
        mCardCvcEditText.setText(card.getCvc());
        mCardCvcEditText.addTextChangedListener(mCardCvcTextWatcher);

        // toggle charge button if card is valid
        mChargeButton.setEnabled(mCard.isValid());
    }

    /**
     * Registers a click listener on the charge button
     *
     * @param listener A click listener for the view
     */
    public void setOnChargeClickListener(OnClickListener listener)
    {
        mChargeButton.setOnClickListener(listener);
    }

    /**
     * Registers a click listener on the close button
     *
     * @param listener A click listener for the view
     */
    public void setOnCloseClickListener(OnClickListener listener)
    {
        mCloseButton.setOnClickListener(listener);
    }

    /**
     * Resets the view to a blank state
     */
    public void reset()
    {
        setCard(new Card());
        hideOverlays();
    }


    // ----------------------------------
    // Customization
    // ----------------------------------

    /**
     * Toggles the close button's visibility
     *
     * @param show If set to true, displays the close button (defaults to true)
     * @return The current CardEditor object
     */
    public CardEditor setCloseable(boolean show)
    {
        mCloseButton.setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * @return The amount
     */
    public double getAmount()
    {
        return mAmount;
    }

    /**
     * Toggles the displayed amount to be charged
     *
     * @param amount The payment amount
     * @return The current CardEditor object
     */
    public CardEditor setAmount(double amount)
    {
        mAmount = amount;

        String text;
        if (amount <= 0d) {
            text = getResources().getString(R.string.simplify_charge_card);
        }
        else {
            text = String.format(getResources().getString(R.string.simplify_charge), DecimalFormat.getCurrencyInstance().format(amount));
        }

        mChargeButton.setText(text);

        return this;
    }

    /**
     * Sets the primary theme color used in the view
     *
     * @param color The color to use
     * @return The current CardEditor object
     */
    public CardEditor setThemeColor(int color)
    {
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable(color));
        sld.addState(new int[]{-android.R.attr.state_enabled}, new ColorDrawable(Color.TRANSPARENT));

        mChargeButton.setBackgroundDrawable(sld);

        ((TextView) findViewById(R.id.simplify_tv_exp)).setTextColor(color);
        ((TextView) findViewById(R.id.simplify_tv_cvc)).setTextColor(color);
        ((TextView) findViewById(R.id.simplify_tv_success)).setTextColor(color);
        ((TextView) findViewById(R.id.simplify_tv_failure)).setTextColor(color);

        return this;
    }


    // ----------------------------------
    // Overlays
    // ----------------------------------

    /**
     * Displays the success overlay with a custom message
     *
     * @param message A custom message to display
     */
    public void showSuccessOverlay(String message)
    {
        ((TextView) mSuccessOverlay.findViewById(R.id.simplify_tv_successmessage)).setText(message);

        mSuccessOverlay.setVisibility(View.VISIBLE);
        mErrorOverlay.setVisibility(View.GONE);
    }

    /**
     * Displays the error overlay with a custom message
     *
     * @param message A custom message to display
     */
    public void showErrorOverlay(String message)
    {
        ((TextView) mErrorOverlay.findViewById(R.id.simplify_tv_failuremessage)).setText(message);

        mSuccessOverlay.setVisibility(View.GONE);
        mErrorOverlay.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the overlays and display the card editor form
     */
    public void hideOverlays()
    {
        mSuccessOverlay.setVisibility(View.GONE);
        mErrorOverlay.setVisibility(View.GONE);
    }


    // ----------------------------------
    // Text Watchers
    // ----------------------------------

    private TextWatcher mCardNumberTextWatcher = new TextWatcher()
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
            mCard.setNumber(editable.toString());

            mCardCvcEditText.setType(mCard.getType());

            // toggle charge button if card is valid
            mChargeButton.setEnabled(mCard.isValid());
        }
    };

    private TextWatcher mCardExpirationTextWatcher = new TextWatcher()
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
            mCard.setMonth(mCardExpirationEditText.getMonth());
            mCard.setYear(mCardExpirationEditText.getYear());

            // toggle charge button if card is valid
            mChargeButton.setEnabled(mCard.isValid());
        }
    };

    private TextWatcher mCardCvcTextWatcher = new TextWatcher()
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
            mCard.setCvc(editable.toString());

            // toggle charge button if card is valid
            mChargeButton.setEnabled(mCard.isValid());
        }
    };
}

