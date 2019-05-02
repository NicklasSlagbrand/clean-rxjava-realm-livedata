package com.nicklasslagbrand.placeholder.data.entity.converter

import com.nicklasslagbrand.placeholder.data.entity.RealmFavourite
import com.nicklasslagbrand.placeholder.domain.model.Favourite

object RealmFavouriteConverter {
    fun toFavourite(realmFavourite: RealmFavourite): Favourite {
        return with(realmFavourite) {
            Favourite(attractionId, timestamp)
        }
    }

    fun toFavouriteId(realmFavourite: RealmFavourite): Int {
        return with(realmFavourite) {
            attractionId
        }
    }

    fun fromFavourite(favourite: Favourite): RealmFavourite {
        return with(favourite) {
            RealmFavourite(attractionId, timestamp)
        }
    }
}
