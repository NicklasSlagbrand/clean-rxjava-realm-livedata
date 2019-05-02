package com.nicklasslagbrand.placeholder.extension

import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.functional.Result
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

fun <T : Any> Observable<T>.toResult(): Observable<Result<T, Error>> {
    return compose(ObservableToResultTransformer())
}

class ObservableToResultTransformer<T : Any> : ObservableTransformer<T, Result<T, Error>> {
    override fun apply(upstream: Observable<T>): ObservableSource<Result<T, Error>> {
        return upstream.map { Result.success(it) }
            .onErrorReturn { Result.Failure(Error.GeneralError(it)) }
    }
}
