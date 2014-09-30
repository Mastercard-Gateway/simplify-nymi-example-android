package com.simplify.android.sdk.gson;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.simplify.android.sdk.model.SimplifyError;
import com.simplify.android.sdk.model.FieldError;

import java.lang.reflect.Type;

public class SimplifyErrorAdapter implements JsonDeserializer<SimplifyError>
{
    @Override
    public SimplifyError deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject json = jsonElement.getAsJsonObject();
        JsonObject error = json.get("error").getAsJsonObject();

        FieldError[] fieldErrors = context.deserialize(error.get("fieldErrors"), FieldError[].class);

        return new SimplifyError()
                .setReference(json.get("reference").getAsString())
                .setCode(error.get("code").getAsString())
                .setMessage(error.get("message").getAsString())
                .setFieldErrors(fieldErrors);
    }
}
