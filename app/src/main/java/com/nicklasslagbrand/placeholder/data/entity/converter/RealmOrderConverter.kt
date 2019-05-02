package com.nicklasslagbrand.placeholder.data.entity.converter

import com.nicklasslagbrand.placeholder.data.entity.RealmOrder
import com.nicklasslagbrand.placeholder.data.entity.RealmPurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.OrderStatus
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import io.realm.RealmList

object RealmOrderConverter {
    fun toOrder(realmOrder: RealmOrder): Order {
        with(realmOrder) {
            val orderReference = orderReference
            val orderStatus = OrderStatus.valueOf(orderStatus)
            val cardsQty = cardsQty

            val cards = cards?.map {
                with(it) {
                    PurchasedCard(
                        id,
                        validationTime,
                        qrCodeUrl,
                        QrCodeStatus.valueOf(status),
                        expirationDate
                    )
                }
            } ?: emptyList()

            return Order(orderReference, orderStatus, cardsQty, cards)
        }
    }

    fun fromOrder(order: Order): RealmOrder {
        with(order) {
            val realmCards = RealmList<RealmPurchasedCard>()
            cards.forEach {
                realmCards.add(
                    with(it) {
                        RealmPurchasedCard(
                            id,
                            validationTime,
                            qrCodeUrl,
                            status.name,
                            expirationDate
                        )
                    }
                )
            }

            return RealmOrder(orderReference, orderStatus.name, order.cardsQty, realmCards)
        }
    }
}
