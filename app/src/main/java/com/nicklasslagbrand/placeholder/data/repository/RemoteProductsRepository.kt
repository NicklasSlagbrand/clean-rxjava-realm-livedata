package com.nicklasslagbrand.placeholder.data.repository

import com.nicklasslagbrand.placeholder.data.network.PlaceholderApiWrapper
import com.nicklasslagbrand.placeholder.domain.model.Product
import io.reactivex.Observable

interface RemoteProductsRepository {
    fun getAllProducts(): Observable<List<Product>>

    class NetworkProductsRepository(private val placeholderApiWrapper: PlaceholderApiWrapper)
        : RemoteProductsRepository {
        override fun getAllProducts(): Observable<List<Product>> = placeholderApiWrapper.fetchAllProducts()
    }
}
