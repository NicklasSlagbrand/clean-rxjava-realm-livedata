package com.nicklasslagbrand.placeholder.data.entity.converter

import com.nicklasslagbrand.placeholder.data.entity.RealmAttraction
import com.nicklasslagbrand.placeholder.data.entity.RealmContactInfo
import com.nicklasslagbrand.placeholder.data.entity.RealmDaysItem
import com.nicklasslagbrand.placeholder.data.entity.RealmLocation
import com.nicklasslagbrand.placeholder.data.entity.RealmOpeningHoursItem
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.domain.model.OpeningHoursItem
import io.realm.RealmList

object RealmAttractionConverter {
    fun toAttraction(realmAttraction: RealmAttraction): Attraction {
        with(realmAttraction) {
            val location = location!!.toLocation()

            val category = AttractionCategory.forId(category)
            val contactInfo = contactInfo!!.toContactInfo()

            val openingHoursItems = mutableListOf<OpeningHoursItem>()
            openingHours?.let {
                it.map { realmHourItem ->
                    openingHoursItems.add(realmHourItem.toOpeningHoursItem())
                }
            }

            val imageItems = mutableListOf<String>()
            images?.map {
                imageItems.add(it)
            }

            return Attraction(
                id,
                title,
                teaser,
                description,
                discount,
                imageItems,
                openingHoursItems,
                location,
                category,
                contactInfo
            )
        }
    }

    fun fromAttraction(attraction: Attraction): RealmAttraction {
        with(attraction) {
            val entity = RealmAttraction()
            entity.id = id
            entity.title = title
            entity.teaser = teaser
            entity.category = category.id
            entity.description = description
            entity.discount = discount

            entity.openingHours = RealmList()
            openingHours.forEach { openHours ->
                val openingHoursEntity = RealmOpeningHoursItem()
                openingHoursEntity.startDate = openHours.startDate
                openingHoursEntity.endDate = openHours.endDate
                entity.openingHours?.add(openingHoursEntity)

                openingHoursEntity.days = RealmList()
                openHours.days.forEach { day ->
                    val dayEntity = RealmDaysItem()
                    dayEntity.name = day.name
                    dayEntity.opening = day.opening
                    dayEntity.closing = day.closing
                    openingHoursEntity.days?.add(dayEntity)
                }
            }

            val locationEntity = RealmLocation()
            locationEntity.latitude = location.latitude
            locationEntity.longitude = location.longitude
            entity.location = locationEntity

            val contactInfoEntity = RealmContactInfo()
            contactInfoEntity.address = contactInfo.address
            contactInfoEntity.phone = contactInfo.phone
            contactInfoEntity.web = contactInfo.web
            entity.contactInfo = contactInfoEntity

            entity.images = RealmList()
            images.forEach { image ->
                entity.images?.add(image)
            }

            return entity
        }
    }
}
