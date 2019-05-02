package com.nicklasslagbrand.placeholder.data.entity

import com.nicklasslagbrand.placeholder.data.entity.converter.RealmAttractionConverter
import com.nicklasslagbrand.placeholder.testMuseumAttraction
import org.amshove.kluent.shouldEqual
import org.junit.Test

class RealmAttractionConverterTest {
    @Test
    fun `test attraction correctly mapped to realm attraction to and from`() {
        val realmAttraction = RealmAttractionConverter.fromAttraction(testMuseumAttraction)

        RealmAttractionConverter.toAttraction(realmAttraction)
            .shouldEqual(testMuseumAttraction)
    }
}
