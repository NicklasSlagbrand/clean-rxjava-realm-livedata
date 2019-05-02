package com.nicklasslagbrand.placeholder.feature.base

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.extension.consume

@SuppressLint("Registered")
open class BaseFragment : Fragment() {

    fun handleFailure(errorEvent: ConsumableEvent<Error>) {
        errorEvent.handleIfNotConsumed {
            consume {
                showToast("Faced an error: $it.")
            }
        }
    }

    fun showToast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }
}
