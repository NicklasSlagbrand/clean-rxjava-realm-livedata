package com.nicklasslagbrand.placeholder.data.entity

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmOrder(
    @PrimaryKey
    var orderReference: String = "",
    var orderStatus: String = "",
    var cardsQty: Int = 0,
    var cards: RealmList<RealmPurchasedCard>? = null
) : RealmObject()

open class RealmPurchasedCard(
    @PrimaryKey
    var id: Int = 0,
    var validationTime: Int = 0,
    var qrCodeUrl: String = "",
    var status: String = "",
    var expirationDate: String = ""
) : RealmObject()
