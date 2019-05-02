package com.nicklasslagbrand.placeholder.domain

import com.nicklasslagbrand.placeholder.data.repository.RemoteProductsRepository
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Product
import com.nicklasslagbrand.placeholder.extension.toResult
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable

interface ProductsDataSource {
    fun getAllProducts(): Observable<Result<List<Product>, Error>>

    class DefaultProductsDataSource(
        private val remoteProductsRepository: RemoteProductsRepository
    ) : ProductsDataSource {
        override fun getAllProducts(): Observable<Result<List<Product>, Error>> {
            return remoteProductsRepository.getAllProducts()
                .toResult()
        }
    }
}
