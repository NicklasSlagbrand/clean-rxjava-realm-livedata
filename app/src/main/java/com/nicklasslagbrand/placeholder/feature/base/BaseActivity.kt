package com.nicklasslagbrand.placeholder.feature.base

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.extension.consume

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    fun handleFailure(errorEvent: ConsumableEvent<Error>) {
        errorEvent.handleIfNotConsumed {
            consume {
                showToast("Faced an error: $it.")
            }
        }
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
