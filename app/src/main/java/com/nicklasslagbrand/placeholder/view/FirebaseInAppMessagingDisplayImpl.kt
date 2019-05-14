package com.nicklasslagbrand.placeholder.view

import android.R
import android.app.Activity
import android.app.Dialog
import android.view.Window
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplay
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks
import com.google.firebase.inappmessaging.model.InAppMessage
import com.google.gson.Gson
import com.nicklasslagbrand.placeholder.domain.model.InAppMessageModel
import com.nicklasslagbrand.placeholder.feature.splash.OnDialogListener
import kotlinx.android.synthetic.main.dialog_message.*

class FirebaseInAppMessagingDisplayImpl(val activity: Activity) : FirebaseInAppMessagingDisplay {

    private var messageDialog: Dialog? = null

    private var dialogListener: OnDialogListener? = null

    private val gson: Gson = Gson()

    fun setOnDismissDialog(onDialogListener: OnDialogListener) {
        dialogListener = onDialogListener
    }

    override fun displayMessage(p0: InAppMessage?, p1: FirebaseInAppMessagingDisplayCallbacks?) {
        val inAppMessageModel = gson.fromJson(gson.toJson(p0), InAppMessageModel::class.java)
        showMessageDialog(inAppMessageModel, this.dialogListener!!)
    }

    private fun showMessageDialog(message: InAppMessageModel, listener: OnDialogListener) {

        messageDialog = Dialog(activity, R.style.Theme_Black_NoTitleBar_Fullscreen)
        messageDialog?.let { dialog ->
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(com.nicklasslagbrand.placeholder.R.layout.dialog_message)

            dialog.tvMessageTitle.text = message.title
            dialog.tvMessageDescrLong.text = message.body

            dialog.btnMessage.setOnClickListener {
                listener.onDismissDialog()
            }

            dialog.show()
        }
    }
}
