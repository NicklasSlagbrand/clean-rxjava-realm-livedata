package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nicklasslagbrand.placeholder.domain.model.Error
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base ViewModel class with default Error and [Disposable] handling.
 * @see ViewModel
 * @see Error
 * @see Disposable
 */
abstract class RxBaseViewModel : ViewModel() {
    var failure: MutableLiveData<ConsumableEvent<Error>> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(block: () -> Disposable) {
        compositeDisposable.add(block())
    }

    fun handleFailure(error: Error) {
        this.failure.value = ConsumableEvent(error)
    }

    fun doNothing(error: Error) {}

    public override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }
}
