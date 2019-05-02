package com.nicklasslagbrand.placeholder

import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.domain.model.ContactInfo
import com.nicklasslagbrand.placeholder.domain.model.DaysItem
import com.nicklasslagbrand.placeholder.domain.model.Location
import com.nicklasslagbrand.placeholder.domain.model.OpeningHoursItem
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.OrderStatus
import com.nicklasslagbrand.placeholder.domain.model.Price
import com.nicklasslagbrand.placeholder.domain.model.Product
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import com.nicklasslagbrand.placeholder.extension.empty

const val TEST_DEVICE_ID = "test_device_id"

@Suppress("MagicNumber")
val testDkkPrice = Price("DKK", 1)
@Suppress("MagicNumber")
val testUsdPrice = Price("USD", 2)
@Suppress("MagicNumber")
val testEurPrice = Price("EUR", 3)

@Suppress("MagicNumber")
val testChild24Product =
    Product(
        2,
        "Children 24 hours",
        24,
        listOf(testDkkPrice, testUsdPrice, testEurPrice)
    )
@Suppress("MagicNumber")
val testAdult24Product =
    Product(
        1,
        "Adult 24 hours",
        24,
        listOf(testDkkPrice, testUsdPrice, testEurPrice)
    )

@Suppress("MagicNumber")
val testPurchasedAdult24ExpiredCard =
    PurchasedCard(
        1234,
        24,
        "https://en.wikipedia.org/wiki/QR_code#/media/File:QR_code_for_mobile_English_Wikipedia.svg",
        QrCodeStatus.Expired,
        "2018-12-19T14:10:37+0000"
    )
@Suppress("MagicNumber")
val testPurchasedChild72ActiveCard =
    PurchasedCard(
        1567,
        72,
        "https://en.wikipedia.org/wiki/QR_code#/media/File:QR_code_for_mobile_English_Wikipedia.svg",
        QrCodeStatus.Active,
        "2018-12-19T14:10:37+0000"
    )
@Suppress("MagicNumber")
val testPurchasedChild48NotActivatedCard =
    PurchasedCard(
        1568,
        48,
        "https://en.wikipedia.org/wiki/QR_code#/media/File:QR_code_for_mobile_English_Wikipedia.svg",
        QrCodeStatus.NotActivated,
        "2018-12-19T14:10:37+0000"
    )

val openingHour =
    OpeningHoursItem(
        "2019-01-02", "2019-12-22", listOf(
            DaysItem("23:00", "11:00", "monday"),
            DaysItem("22:00", "10:00", "tuesday"),
            DaysItem("20:00", "08:00", "wednesday")
        )
    )

val testMuseumAttraction = Attraction(
    1,
    "title",
    "teaser",
    "description",
    "discount",
    listOf("image_url"),
    listOf(openingHour),
    Location(1.0, 1.0),
    AttractionCategory.History,
    ContactInfo("address", "phone", "web")
)

val testPendingCard = PurchasedCard(
    2,
    0,
    String.empty(),
    QrCodeStatus.Pending,
    String.empty()
)

@Suppress("MagicNumber")
val testNotActivatedCard = PurchasedCard(
    3,
    72,
    String.empty(),
    QrCodeStatus.NotActivated,
    String.empty()
)

val testProcessedOrder = Order(
    "1",
    OrderStatus.Completed,
    1,
    listOf(testPurchasedChild72ActiveCard)
)

val testPendingOrder = Order("2", OrderStatus.Pending, 0, emptyList())
