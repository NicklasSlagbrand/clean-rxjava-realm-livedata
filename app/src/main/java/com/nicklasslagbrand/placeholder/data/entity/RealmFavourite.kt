package com.nicklasslagbrand.placeholder.data.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmFavourite(
    @PrimaryKey
    var attractionId: Int = 0,
    var timestamp: Long = 0
) : RealmObject()
