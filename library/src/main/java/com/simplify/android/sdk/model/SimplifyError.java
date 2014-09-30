package com.simplify.android.sdk.model;

/**
 * Class representing a Simplify API error
 */
public class SimplifyError
{
    private int mStatusCode                 = 0;
    private String mCode;
    private String mMessage;
    private String mReference;
    private FieldError[] mFieldErrors;


    public int getStatusCode()
    {
        return mStatusCode;
    }

    public SimplifyError setStatusCode(int statusCode)
    {
        mStatusCode = statusCode;
        return this;
    }

    public String getCode()
    {
        return mCode;
    }

    public SimplifyError setCode(String code)
    {
        mCode = code;
        return this;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public SimplifyError setMessage(String message)
    {
        mMessage = message;
        return this;
    }

    public String getReference()
    {
        return mReference;
    }

    public SimplifyError setReference(String reference)
    {
        mReference = reference;
        return this;
    }

    public FieldError[] getFieldErrors()
    {
        return mFieldErrors;
    }

    public SimplifyError setFieldErrors(FieldError[] fieldErrors)
    {
        mFieldErrors = fieldErrors;
        return this;
    }
}
