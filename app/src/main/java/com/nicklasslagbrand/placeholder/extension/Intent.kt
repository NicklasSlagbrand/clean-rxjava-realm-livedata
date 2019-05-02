package com.nicklasslagbrand.placeholder.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.domain.model.Location

fun Activity.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "No app found to open $url", Toast.LENGTH_LONG).show()
    }
}

fun Activity.openDirections(location: Location) {
    val finalUrl = "${Constants.GOOGLE_MAPS_URL}&destination=${location.latitude},${location.longitude}"

    openUrl(finalUrl)
}
