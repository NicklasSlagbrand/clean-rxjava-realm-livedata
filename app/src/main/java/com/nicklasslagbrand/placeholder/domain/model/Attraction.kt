package com.nicklasslagbrand.placeholder.domain.model

import com.google.gson.annotations.SerializedName
import com.nicklasslagbrand.placeholder.domain.model.Attraction.Companion.ART_CATEGORY_ID
import com.nicklasslagbrand.placeholder.domain.model.Attraction.Companion.FAVORITE_CATEGORY_ID
import com.nicklasslagbrand.placeholder.domain.model.Attraction.Companion.FUN_CATEGORY_ID
import com.nicklasslagbrand.placeholder.domain.model.Attraction.Companion.HISTORY_CATEGORY_ID
import com.nicklasslagbrand.placeholder.domain.model.Attraction.Companion.ROYAL_CATEGORY_ID
import com.nicklasslagbrand.placeholder.domain.model.Attraction.Companion.SIGHT_CATEGORY_ID

data class Attraction(
    var id: Int,
    val title: String,
    val teaser: String,
    val description: String,
    val discount: String,
    @SerializedName("imageUrl")
    val images: List<String>,
    @SerializedName("openinghours")
    val openingHours: List<OpeningHoursItem>,
    val location: Location,
    val category: AttractionCategory,
    val contactInfo: ContactInfo
) {
    companion object {
        const val ART_CATEGORY_ID = 256
        const val FUN_CATEGORY_ID = 96
        const val HISTORY_CATEGORY_ID = 191
        const val ROYAL_CATEGORY_ID = 91
        const val SIGHT_CATEGORY_ID = 276
        const val FAVORITE_CATEGORY_ID = 7
    }
}

enum class AttractionCategory(val id: Int) {
    Art(ART_CATEGORY_ID),
    Fun(FUN_CATEGORY_ID),
    History(HISTORY_CATEGORY_ID),
    Royal(ROYAL_CATEGORY_ID),
    Sight(SIGHT_CATEGORY_ID),
    Favourite(FAVORITE_CATEGORY_ID);

    companion object {
        fun forId(id: Int) = AttractionCategory.values().first { it.id == id }
    }
}

data class Location(val latitude: Double, val longitude: Double)
data class ContactInfo(val address: String, val phone: String, val web: String)
data class OpeningHoursItem(val endDate: String, val startDate: String, val days: List<DaysItem>)
data class DaysItem(val closing: String, val opening: String, val name: String)
