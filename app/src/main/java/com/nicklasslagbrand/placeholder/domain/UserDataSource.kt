package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.data.repository.LocalUserRepository
import com.nicklasslagbrand.placeholder.data.repository.RemoteUserRepository
import com.nicklasslagbrand.placeholder.domain.model.CardsContainer
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Order
import com.nicklasslagbrand.placeholder.domain.model.OrderStatus
import com.nicklasslagbrand.placeholder.domain.model.PurchasedCard
import com.nicklasslagbrand.placeholder.domain.model.QrCodeStatus
import com.nicklasslagbrand.placeholder.domain.usecase.params.CreateOrderParam
import com.nicklasslagbrand.placeholder.extension.empty
import com.nicklasslagbrand.placeholder.extension.findDifference
import com.nicklasslagbrand.placeholder.extension.toResult
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable

interface UserDataSource {
    fun getAllUserCards(): Observable<Result<CardsContainer, Error>>
    fun activateCard(cardId: Int, activationDate: String): Observable<Result<Any, Error>>
    fun createOrder(createOrderParam: CreateOrderParam): Observable<Result<String, Error>>
    fun transferCard(cardId: Int, receiverDeviceId: String): Observable<Result<Any, Error>>

    class DefaultUserDataSource(
        private val localUserRepository: LocalUserRepository,
        private val remoteUserRepository: RemoteUserRepository
    ) : UserDataSource {
        override fun transferCard(cardId: Int, receiverDeviceId: String): Observable<Result<Any, Error>> {
            return remoteUserRepository.transferCard(cardId, receiverDeviceId)
                .flatMap {
                    localUserRepository.updateCardStatus(cardId, QrCodeStatus.Transferred)
                }
                .toResult()
        }

        override fun createOrder(createOrderParam: CreateOrderParam): Observable<Result<String, Error>> {
            return remoteUserRepository.createOrder(createOrderParam, Constants.CREATE_ORDER_DELIVERY_TYPE)
                .toResult()
        }

        override fun activateCard(cardId: Int, activationDate: String): Observable<Result<Any, Error>> {
            return remoteUserRepository.activateCard(cardId, activationDate)
                .toResult()
        }

        @Suppress("RedundantLambdaArrow")
        override fun getAllUserCards(): Observable<Result<CardsContainer, Error>> {
            // Initially fetch user cards from local storage
            return localUserRepository.getAllUserOrders().flatMap { fromStorage ->
                // Then fetch orders from the network
                remoteUserRepository.getAllUserOrders()
                    // Store orders from the network in the local storage
                    .flatMap { localUserRepository.storeOrders(it) }
                    .map {
                        Pair(it, fromStorage)
                    }
            }.map {
                // Find new cards in the response
                val result = it.first.findDifference(it.second)
                val cards = extractCardsFromOrders(it.first)
                CardsContainer(cards, result.isNotEmpty())
            }.onErrorResumeNext { _: Throwable ->
                // In case of any error during previous actions fallback to orders from local storage
                localUserRepository.getAllUserOrders()
                    .map { extractCardsFromOrders(it) }
                    .map { CardsContainer(it) }
            }.toResult()
        }

        private fun extractCardsFromOrders(orders: List<Order>): List<PurchasedCard> {
            return orders.map {
                return@map if (it.orderStatus == OrderStatus.Completed) {
                    it.cards
                } else {
                    listOf(
                        PurchasedCard(
                            it.orderReference.toInt(),
                            0,
                            String.empty(),
                            QrCodeStatus.Pending,
                            String.empty()
                        )
                    )
                }
            }.flatten()
        }
    }
}
