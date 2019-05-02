package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.entity.RealmOrder
import com.nicklasslagbrand.placeholder.data.entity.RealmPurchasedCard
import com.nicklasslagbrand.placeholder.data.entity.converter.RealmOrderConverter
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where

interface LocalUserRepository {
    fun getAllUserOrders(): Observable<List<Order>>
    fun storeOrders(orders: List<Order>): Observable<List<Order>>
    fun storeOrder(order: Order): Observable<Order>
    fun updateCardStatus(cardId: Int, newStatus: QrCodeStatus): Observable<Any>

    class RealmLocalUserRepository : LocalUserRepository {
        override fun updateCardStatus(cardId: Int, newStatus: QrCodeStatus): Observable<Any> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use {
                    it.executeTransaction { realm ->
                        val card = realm.where<RealmPurchasedCard>().equalTo("id", cardId)
                            .findFirst()

                        if (card != null) {
                            card.status = newStatus.name
                            realm.insertOrUpdate(card)
                        }
                    }
                }

                return@fromCallable Any()
            }
        }

        override fun storeOrders(orders: List<Order>): Observable<List<Order>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use {
                    it.executeTransaction { realm ->
                        orders.forEach { order ->
                            realm.insertOrUpdate(RealmOrderConverter.fromOrder(order))
                        }
                    }
                }

                return@fromCallable orders
            }
        }

        override fun storeOrder(order: Order): Observable<Order> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use {
                    it.executeTransaction { realm ->
                        realm.insertOrUpdate(RealmOrderConverter.fromOrder(order))
                    }
                }

                return@fromCallable order
            }
        }

        override fun getAllUserOrders(): Observable<List<Order>> {
            return Observable.fromCallable {
                Realm.getDefaultInstance().use { realm ->
                    val realmResults = realm.where<RealmOrder>().findAll()
                    return@fromCallable handleQueryResults(realmResults, realm)
                }
            }
        }

        private fun handleQueryResults(realmResults: RealmResults<RealmOrder>, realm: Realm): List<Order> {
            val realmOrders = realm.copyFromRealm(realmResults)
            return realmOrders.map {
                RealmOrderConverter.toOrder(it)
            }
        }
    }
}
