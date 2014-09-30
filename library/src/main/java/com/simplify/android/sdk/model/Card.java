package com.simplify.android.sdk.model;

import com.simplify.android.sdk.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class representing a user-provided card
 */
public class Card
{
    private String mId;
    private String mNumber      = "";
    private String mLast4;
    private Type mType          = Type.UNKNOWN;
    private int mMonth          = 0;
    private int mYear           = 0;
    private String mCvc         = "";
    private long mDateCreated;


    /**
     * Returns the card id
     * @return  The card id
     */
    public String getId()
    {
        return mId;
    }

    /**
     * Sets the card id
     * @param id    The card id
     * @return      The current Card object
     */
    public Card setId(String id)
    {
        mId = id;
        return this;
    }

    /**
     * Returns the card number
     * @return  The card number
     */
    public String getNumber()
    {
        return mNumber;
    }

    /**
     * Sets the card number and associated card type
     * @param number    A card number
     * @return          The current Card object
     */
    public Card setNumber(String number)
    {
        mNumber = number.replaceAll("[^\\d]", "");
        mType = Type.detect(number);
        return this;
    }

    /**
     * Returns the last 4 digits of the card number
     * @return  The last 4 digits of the card number
     */
    public String getLast4()
    {
        return mLast4;
    }

    /**
     * Sets the last 4 digits of the card number
     * (Does not affect the actual card number)
     * @param last4     The last 4 digits of the card number
     * @return          The current Card object
     */
    public Card setLast4(String last4)
    {
        mLast4 = last4;
        return this;
    }

    /**
     * Returns the card type
     * @return      The matching card type
     */
    public Type getType()
    {
        return mType;
    }

    /**
     * Sets the card type
     * @param typeName  The typeName of the card type
     * @return          The current Card object
     */
    public Card setType(String typeName)
    {
        try {
            mType = Type.valueOf(typeName.trim().toUpperCase());
        }
        catch (IllegalArgumentException e) {
            mType = Type.UNKNOWN;
        }

        return this;
    }

    /**
     * Sets the card type
     * @param type  The card type
     * @return      The current Card object
     */
    public Card setType(Type type)
    {
        mType = type;
        return this;
    }

    /**
     * Returns the expiration date string in the format 'MM/YY'
     * @return  String representation of the card expiration date
     */
    public String getExpiration()
    {
        if (mMonth == 0 || mYear == 0) {
            return "";
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, mMonth - 1);
        c.set(Calendar.YEAR, mYear);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        return sdf.format(c.getTime());
    }

    /**
     * Returns the card expiration month
     * @return  The card expiration month
     */
    public int getMonth()
    {
        return mMonth;
    }

    /**
     * Sets the card expiration month
     * @param month     An expiration month (format: "MM")
     * @return          The current Card object
     */
    public Card setMonth(String month)
    {
        return setMonth(month.trim().length() == 0 ? 0 : Integer.parseInt(month));
    }

    /**
     * Sets the card expiration month
     * @param month     The card expiration month, 1 - 12
     * @return          The current Card object
     */
    public Card setMonth(int month)
    {
        mMonth = month;
        return this;
    }

    /**
     * Returns the card expiration year
     * @return  The card expiration year
     */
    public int getYear()
    {
        return mYear;
    }

    /**
     * Sets the card expiration year
     * @param year      The card expiration year (format: "YY" or "YYYY")
     * @return          The current Card object
     */
    public Card setYear(String year)
    {
        return setYear(year.trim().length() == 0 ? 0 : Integer.parseInt(year));
    }

    /**
     * Sets the card expiration year
     * @param year      The 2 or 4 digit card expiration year
     * @return          The current Card object
     */
    public Card setYear(int year)
    {
        mYear = 2000 + (year % 100);
        return this;
    }

    /**
     * Returns the card security code
     * @return      The card security code
     */
    public String getCvc()
    {
        return mCvc;
    }

    /**
     * Sets the card security code
     * @param cvc   A card security code (format: "123")
     * @return      The current Card object
     */
    public Card setCvc(String cvc)
    {
        mCvc = cvc;
        return this;
    }

    /**
     * Gets date created
     * @return  Date created
     */
    public long getDateCreated()
    {
        return mDateCreated;
    }

    /**
     * Sets date created
     * @param dateCreated   Date created
     * @return              The current Card object
     */
    public Card setDateCreated(long dateCreated)
    {
        mDateCreated = dateCreated;
        return this;
    }

    /**
     * Validates the current card details
     * @return  True or false
     */
    public boolean isValid()
    {
        return isValidNumber() && isValidExpiration() && isValidCvc();
    }

    /**
     * Validates the current card number
     * @return  True or False
     */
    public boolean isValidNumber()
    {
        return Utils.validateCardNumber(mNumber, mType);
    }

    /**
     * Validates the current card expiration date
     * @return  True or False
     */
    public boolean isValidExpiration()
    {
        return Utils.validateCardExpiration(mMonth, mYear);
    }

    /**
     * Validates the current card security code against the type of the card
     * @return  True or False
     */
    public boolean isValidCvc()
    {
        return Utils.validateCardCvc(mCvc, mType);
    }

    /**
     * Enumerated card types supported by the Simplify API
     */
    public enum Type
    {
        VISA(13, 19, 3, "^4\\d*"),
        MASTERCARD(16, 16, 3, "^(?:5[1-5]|67)\\d*"),
        AMERICAN_EXPRESS(15, 15, 4, "^3[47]\\d*"),
        DISCOVER(16, 16, 3, "^6(?:011|4[4-9]|5)\\d*"),
        DINERS(14, 16, 3, "^3(?:0(?:[0-5]|9)|[689])\\d*"),
        JCB(16, 16, 3, "^35(?:2[89]|[3-8])\\d*"),
        UNKNOWN(13, 19, 3);


        private int mMinLength;
        private int mMaxLength;
        private int mCvcLength;
        private String mPattern;

        Type(int minLength, int maxLength, int cvcLength)
        {
            mMinLength = minLength;
            mMaxLength = maxLength;
            mCvcLength = cvcLength;
        }

        Type(int minLength, int maxLength, int cvcLength, String prefixPattern)
        {
            this(minLength, maxLength, cvcLength);
            mPattern = prefixPattern;
        }

        public int getMinLength()
        {
            return mMinLength;
        }

        public int getMaxLength()
        {
            return mMaxLength;
        }

        public int getCvcLength()
        {
            return mCvcLength;
        }

        public boolean prefixMatches(String number)
        {
            return mPattern == null || number.matches(mPattern);
        }

        public String format(String number)
        {
            if (number == null) {
                return number;
            }

            String formatted = "";
            int length = number.length();

            switch (this) {
                case AMERICAN_EXPRESS:
                    for (int i = 0; i < length; i++) {
                        formatted += (i == 4 || i == 10 ? " " : "") + number.charAt(i);
                    }
                    break;
                default :
                    for (int i = 0; i < length; i++) {
                        formatted += (i > 0 && i % 4 == 0 ? " " : "") + number.charAt(i);
                    }
                    break;
            }

            return formatted;
        }

        public static Type detect(String cardNumber)
        {
            if (cardNumber != null) {
                cardNumber = cardNumber.replaceAll("[^\\d]+", "");
                for (Type type : values()) {
                    if (type.mPattern != null && type.prefixMatches(cardNumber)) {
                        return type;
                    }
                }
            }

            return UNKNOWN;
        }
    }
}
