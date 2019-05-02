package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.network.PlaceholderApiWrapper
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.usecase.params.CreateOrderParam
import io.reactivex.Observable
import retrofit2.HttpException

interface RemoteUserRepository {
    fun getAllUserOrders(): Observable<List<Order>>
    fun activateCard(cardId: Int, activationDate: String): Observable<Any>
    fun createOrder(createOrderParam: CreateOrderParam, deliveryType: String): Observable<String>
    fun transferCard(cardId: Int, receiverDeviceId: String): Observable<Int>

    class NetworkUserRepository(private val placeholderApiWrapper: PlaceholderApiWrapper) : RemoteUserRepository {
        override fun transferCard(cardId: Int, receiverDeviceId: String): Observable<Int> {
            return placeholderApiWrapper.transferCard(cardId, receiverDeviceId)
        }

        override fun createOrder(createOrderParam: CreateOrderParam, deliveryType: String): Observable<String> {
            return placeholderApiWrapper.createOrder(createOrderParam, deliveryType)
        }

        override fun activateCard(cardId: Int, activationDate: String): Observable<Any> {
            return placeholderApiWrapper.activateCard(cardId, activationDate).flatMap {
                if (it.isSuccessful) Observable.just(Any()) else Observable.error(HttpException(it))
            }
        }

        override fun getAllUserOrders(): Observable<List<Order>> = placeholderApiWrapper.fetchAllUserOrders()
        }
}
