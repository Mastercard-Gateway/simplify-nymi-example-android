package com.simplify.android.sdk.model;


public class FieldError
{
    private String mField;
    private String mCode;
    private String mMessage;


    public String getField()
    {
        return mField;
    }

    public FieldError setField(String field)
    {
        mField = field;
        return this;
    }

    public String getCode()
    {
        return mCode;
    }

    public FieldError setCode(String code)
    {
        mCode = code;
        return this;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public FieldError setMessage(String message)
    {
        mMessage = message;
        return this;
    }
}
