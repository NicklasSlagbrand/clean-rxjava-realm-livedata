package com.nicklasslagbrand.placeholder.feature.transfer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import kotlinx.android.synthetic.main.activity_start_transfer_card.btnScan
import kotlinx.android.synthetic.main.activity_start_transfer_card.toolbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class StartTransferCardActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var cardId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start_transfer_card)

        if (intent == null || !intent.hasExtra(CARD_ID_KEY)) {
            showToast("Missing card id")
            finish()
            return
        }

        cardId = intent.getStringExtra(CARD_ID_KEY)

        initializeToolbar()

        btnScan.setOnClickListener {
            checkPermissions()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar)

        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .build()
                .show()
        }
    }

    @AfterPermissionGranted(ZXING_CAMERA_PERMISSION)
    private fun checkPermissions() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Already have permission, do the thing
            QrCodeScannerActivity.startActivity(this, cardId)
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.scan_camera_permission_rationale),
                ZXING_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        }
    }

    companion object {
        private const val ZXING_CAMERA_PERMISSION = 1
        private const val CARD_ID_KEY = "card_id"

        fun startActivity(context: Context, cardId: Int) {
            context.startActivity(
                Intent(context, StartTransferCardActivity::class.java)
                    .putExtra(CARD_ID_KEY, cardId)
            )
        }
    }
}
