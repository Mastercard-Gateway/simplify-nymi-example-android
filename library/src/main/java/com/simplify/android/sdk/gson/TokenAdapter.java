package com.simplify.android.sdk.gson;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.simplify.android.sdk.model.Card;
import com.simplify.android.sdk.model.Token;

import java.lang.reflect.Type;

public class TokenAdapter implements JsonDeserializer<Token>
{
    @Override
    public Token deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject json = jsonElement.getAsJsonObject();

        Card card = context.deserialize(json.get("card"), Card.class);

        return new Token()
                .setId(json.get("id").getAsString())
                .setUsed(json.get("used").getAsBoolean())
                .setCard(card);
    }
}
