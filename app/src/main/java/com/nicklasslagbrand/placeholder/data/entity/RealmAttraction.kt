package com.nicklasslagbrand.placeholder.data.entity

import com.nicklasslagbrand.placeholder.domain.model.ContactInfo
import com.nicklasslagbrand.placeholder.domain.model.DaysItem
import com.nicklasslagbrand.placeholder.domain.model.Location
import com.nicklasslagbrand.placeholder.domain.model.OpeningHoursItem
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmAttraction(
    @PrimaryKey
    var id: Int = 0,
    var title: String = "",
    var category: Int = 0,
    var teaser: String = "",
    var description: String = "",
    var discount: String = "",
    var images: RealmList<String>? = null,
    var openingHours: RealmList<RealmOpeningHoursItem>? = null,
    var location: RealmLocation? = null,
    var contactInfo: RealmContactInfo? = null
) : RealmObject()

open class RealmLocation(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : RealmObject() {
    fun toLocation() = Location(latitude, longitude)
}

open class RealmContactInfo(
    var address: String = "",
    var phone: String = "",
    var web: String = ""
) : RealmObject() {
    fun toContactInfo() = ContactInfo(address, phone, web)
}

open class RealmOpeningHoursItem(
    var endDate: String = "",
    var startDate: String = "",
    var days: RealmList<RealmDaysItem>? = null
) : RealmObject() {
    fun toOpeningHoursItem(): OpeningHoursItem {
        val daysList = mutableListOf<DaysItem>()

        days?.let {
            it.map { realmDay ->
                daysList.add(realmDay.toDaysItem())
            }
        }

        return OpeningHoursItem(endDate, startDate, daysList)
    }
}

open class RealmDaysItem(
    var closing: String = "",
    var opening: String = "",
    var name: String = ""
) : RealmObject() {
    fun toDaysItem() = DaysItem(closing, opening, name)
}
