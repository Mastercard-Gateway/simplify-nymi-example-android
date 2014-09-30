package com.simplify.android.sdk.model;

/**
 * Class representing a card token returned from the Simplify API
 */
public class Token
{
    private String mId;
    private boolean mUsed;
    private Card mCard;

    /**
     * Returns the id of the card token
     * @return  The card id
     */
    public String getId()
    {
        return mId;
    }

    public Token setId(String id)
    {
        mId = id;
        return this;
    }

    /**
     * Returns a flag indicating if the card token has been used
     * @return  True or False
     */
    public boolean isUsed()
    {
        return mUsed;
    }

    public Token setUsed(boolean used)
    {
        mUsed = used;
        return this;
    }

    /**
     * Returns the basic card details object
     * @return  The card details
     */
    public Card getCard()
    {
        return mCard;
    }

    public Token setCard(Card card)
    {
        mCard = card;
        return this;
    }
}
