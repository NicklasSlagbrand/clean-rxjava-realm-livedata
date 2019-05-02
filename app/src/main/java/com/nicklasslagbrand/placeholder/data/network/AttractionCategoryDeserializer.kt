package com.nicklasslagbrand.placeholder.data.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import java.lang.reflect.Type

class AttractionCategoryDeserializer : JsonDeserializer<AttractionCategory> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): AttractionCategory {
        if (json == null) {
            throw IllegalArgumentException("Received null attraction category.")
        }

        val categoryId = json.asJsonObject.get("id").asInt

        return AttractionCategory.values().first { it.id == categoryId }
    }
}
