package com.nicklasslagbrand.placeholder.domain.usecase

import com.nicklasslagbrand.placeholder.domain.ProductsDataSource
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Product
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.Scheduler

class GetAllProductsUseCase(
    private val productsDataSource: ProductsDataSource,
    workScheduler: Scheduler,
    callbackScheduler: Scheduler
) : RxUseCase<List<Product>, RxUseCase.None>(workScheduler, callbackScheduler) {
    override fun raw(params: None): Observable<Result<List<Product>, Error>> {
        return productsDataSource.getAllProducts()
    }
}
