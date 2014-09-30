package com.simplify.android.sdk;


import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.simplify.android.sdk.gson.CardAdapter;
import com.simplify.android.sdk.gson.FieldErrorAdapter;
import com.simplify.android.sdk.gson.SimplifyErrorAdapter;
import com.simplify.android.sdk.gson.TokenAdapter;
import com.simplify.android.sdk.model.Card;
import com.simplify.android.sdk.model.FieldError;
import com.simplify.android.sdk.model.SimplifyError;
import com.simplify.android.sdk.model.Token;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Class representing the main communication entry point for the Simplify API
 */
public class Simplify
{
    private String mKey;
    private String mUrl;
    private String mUserAgent;

    /**
     * Constructs a new Simplify object
     * @param key Your public API key
     */
    public Simplify(String key) throws InvalidParameterException
    {
        setKey(key);
    }

    /**
     * Returns the API key
     * @return The API key
     */
    public String getKey()
    {
        return mKey;
    }

    /**
     * Sets the API key
     * @param key   Your public API key
     * @return      The current Simplify object
     */
    public Simplify setKey(String key) throws InvalidParameterException
    {
        if (!key.matches("(?:lv|sb)pb_.+")) {
            throw new InvalidParameterException("Invalid api key");
        }

        mKey = key;
        mUrl = key.startsWith("lv") ? Constants.API_BASE_LIVE_URL : Constants.API_BASE_SANDBOX_URL;

        return this;
    }

    /**
     * Returns the current API url
     * @return  The current API url
     */
    public String getUrl()
    {
        return mUrl;
    }

    /**
     * Returns the current user agent
     * @return  The current user agent
     */
    public String getUserAgent()
    {
        return mUserAgent;
    }

    /**
     * Sets the current user agent
     * @param userAgent The user agent
     * @return          The current Simplify Object
     */
    public Simplify setUserAgent(String userAgent)
    {
        mUserAgent = userAgent;
        return this;
    }

    /**
     * Creates a Card object populated with the provided details
     * @param number    A card number
     * @param expMonth  An expiration month (format: MM)
     * @param expYear   An expiration year (format: YY or YYYY)
     * @param cvc       A card security code
     * @return          A populated Card object
     */
    public Card createCard(String number, String expMonth, String expYear, String cvc)
    {
        Card card = new Card()
                .setNumber(number)
                .setMonth(expMonth)
                .setYear(expYear)
                .setCvc(cvc);

        return card;
    }

    /**
     * Creates a Card object populated with the provided details
     * @param number    A card number
     * @param expMonth  An expiration month, 1 - 12
     * @param expYear   A 2 or 4 digit expiration year
     * @param cvc       A card security code
     * @return          A populated Card object
     */
    public Card createCard(String number, int expMonth, int expYear, String cvc)
    {
        Card card = new Card()
                .setNumber(number)
                .setMonth(expMonth)
                .setYear(expYear)
                .setCvc(cvc);

        return card;
    }

    /**
     * Performs an asynchronous request to the Simplify server to retrieve a card token
     * that you can then use to process a payment
     * @param number    A card number
     * @param expMonth  An expiration month (format: MM)
     * @param expYear   An expiration year (format: YY or YYYY)
     * @param cvc       A card security code
     * @param listener  A callback for the request
     * @return          A running AsyncTask
     */
    public AsyncTask<?, ?, ?> createCardToken(String number, String expMonth, String expYear, String cvc, CreateTokenListener listener)
    {
        return createCardToken(createCard(number, expMonth, expYear, cvc), listener);
    }

    /**
     * Performs an asynchronous request to the Simplify server to retrieve a card token
     * that you can then use to process a payment
     * @param number    A card number
     * @param expMonth  An expiration month, 1 - 12
     * @param expYear   A 2 or 4 digit expiration year
     * @param cvc       A card security code
     * @param listener  A callback for the request
     * @return          A running AsyncTask
     */
    public AsyncTask<?, ?, ?> createCardToken(String number, int expMonth, int expYear, String cvc, CreateTokenListener listener)
    {
        return createCardToken(createCard(number, expMonth, expYear, cvc), listener);
    }

    /**
     * Performs an asynchronous request to the Simplify server to retrieve a card token
     * that you can then use to process a payment
     * @param card     A valid card object
     * @param listener A callback for the request
     * @return A running AsyncTask
     */
    public AsyncTask<?, ?, ?> createCardToken(Card card, CreateTokenListener listener)
    {
        return new CreateTokenTask(card, listener).execute();
    }

    /**
     * Listener callback for creating a token
     */
    public interface CreateTokenListener extends SimplifyListener
    {
        public void onSuccess(Token token);
    }

    /**
     * Listener callback for Simplify API
     */
    interface SimplifyListener
    {
        public void onError(SimplifyError error);
    }


    private class CreateTokenTask extends AsyncTask<Void, Void, Token>
    {
        private Card mCard;
        private CreateTokenListener mListener;
        private SimplifyError mSimplifyError;

        public CreateTokenTask(Card card, CreateTokenListener listener)
        {
            mCard = card;
            mListener = listener;
        }

        @Override
        protected Token doInBackground(Void... voids)
        {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Token.class, new TokenAdapter())
                    .registerTypeAdapter(Card.class, new CardAdapter())
                    .registerTypeAdapter(SimplifyError.class, new SimplifyErrorAdapter())
                    .registerTypeAdapter(FieldError.class, new FieldErrorAdapter())
                    .create();

            HttpClient client = new DefaultHttpClient();

            try {
                JsonObject json = new JsonObject();
                json.addProperty("key", getKey());
                json.add("card", gson.toJsonTree(mCard));

                // append user-defined user agent if present
                String userAgent = Constants.USER_AGENT;
                if (getUserAgent() != null) {
                    userAgent = getUserAgent() + " " + Constants.USER_AGENT;
                }

                // build post
                HttpPost post = new HttpPost(getUrl() + Constants.API_PATH_CARDTOKEN);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                post.setHeader("User-Agent", userAgent);
                post.setEntity(new StringEntity(json.toString()));

                Log.i("CreateTokenTask", "REQUEST: " + post.getMethod() + " " + post.getURI().toString());

                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                String content = Utils.inputStreamToString(response.getEntity().getContent());

                Log.i("CreateTokenTask", "RESPONSE: " + statusCode + " " + content);

                if (statusCode < 200 || statusCode >= 300) {
                    mSimplifyError = gson.fromJson(content, SimplifyError.class)
                            .setStatusCode(statusCode);

                    return null;
                }

                return gson.fromJson(content, Token.class);
            }
            catch (IOException e) {
                e.printStackTrace();

                mSimplifyError = new SimplifyError()
                        .setCode("io")
                        .setMessage(e.getMessage());

                return null;
            }
            finally {
                client.getConnectionManager().shutdown();
            }
        }

        @Override
        protected void onPostExecute(Token token)
        {
            if (token != null) {
                Log.i("CreateTokenTask", "Card Token created successfully. Token: " + token.getId());

                mListener.onSuccess(token);
            }
            else {
                Log.e("CreateTokenTask", "Request to create a Card Token failed. " + mSimplifyError.getMessage());

                mListener.onError(mSimplifyError);
            }
        }
    }
}
