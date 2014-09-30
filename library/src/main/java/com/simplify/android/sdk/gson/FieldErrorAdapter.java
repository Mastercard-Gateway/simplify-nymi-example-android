package com.simplify.android.sdk.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.simplify.android.sdk.model.FieldError;

import java.lang.reflect.Type;


public class FieldErrorAdapter implements JsonDeserializer<FieldError>
{
    @Override
    public FieldError deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject json = jsonElement.getAsJsonObject();

        return new FieldError()
                .setField(json.get("field").getAsString())
                .setCode(json.get("code").getAsString())
                .setMessage(json.get("message").getAsString());
    }
}
