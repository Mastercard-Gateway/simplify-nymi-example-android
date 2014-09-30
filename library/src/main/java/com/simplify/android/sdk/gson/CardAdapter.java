package com.simplify.android.sdk.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simplify.android.sdk.model.Card;

import java.lang.reflect.Type;


public class CardAdapter implements JsonSerializer<Card>, JsonDeserializer<Card>
{
    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject json = new JsonObject();
        json.addProperty("number", card.getNumber());
        json.addProperty("expMonth", card.getMonth() + "");
        json.addProperty("expYear", (card.getYear() % 100) + "");
        json.addProperty("cvc", card.getCvc());

        return json;
    }

    @Override
    public Card deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject json = jsonElement.getAsJsonObject();

        return new Card()
                .setId(json.get("id").getAsString())
                .setType(json.get("type").getAsString())
                .setLast4(json.get("last4").getAsString())
                .setMonth(json.get("expMonth").getAsInt())
                .setYear(json.get("expYear").getAsInt())
                .setDateCreated(json.get("dateCreated").getAsLong());
    }
}
