package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.MutableLiveData
import com.nicklasslagbrand.placeholder.Constants
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashViewModel : RxBaseViewModel() {
    val completableLiveData = MutableLiveData<ConsumableEvent<Any>>()

    fun start(isMessageSupressed: Boolean) {
        addDisposable {
            Completable.complete()
                .delay(Constants.SPLASH_SCREEN_DURATION_SECONDS, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    if (isMessageSupressed)
                        completableLiveData.value = ConsumableEvent(Any())
                }
        }
    }
}
