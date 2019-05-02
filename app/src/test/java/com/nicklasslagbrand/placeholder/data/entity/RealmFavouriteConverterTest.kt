package com.nicklasslagbrand.placeholder.data.entity

import com.nicklasslagbrand.placeholder.data.entity.converter.RealmFavouriteConverter
import com.nicklasslagbrand.placeholder.domain.model.Favourite
import org.amshove.kluent.shouldEqual
import org.junit.Test

class RealmFavouriteConverterTest {
    @Test
    fun `verify the converter maps data correctly`() {
        val favourite = Favourite(1, 1)
        val realmFavourite = RealmFavouriteConverter.fromFavourite(favourite)

        RealmFavouriteConverter.toFavourite(realmFavourite).shouldEqual(favourite)
    }
}
