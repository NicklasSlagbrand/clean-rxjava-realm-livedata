package com.nicklasslagbrand.placeholder.feature.main.map

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.domain.model.Error
import com.nicklasslagbrand.placeholder.domain.model.Location
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.fromHtml
import com.nicklasslagbrand.placeholder.extension.loadImageWithCenterCrop
import com.nicklasslagbrand.placeholder.extension.openDirections
import com.nicklasslagbrand.placeholder.extension.setCategoryColorAndTitle
import com.nicklasslagbrand.placeholder.feature.main.attraction.AttractionDetailsActivity
import kotlinx.android.synthetic.main.dialog_attraction_tile.view.*

class AttractionInfoDialogFragment : DialogFragment() {
    private var attractionId: Int = 0
    private lateinit var attractionTitle: String
    private lateinit var attractionTeaser: String
    private lateinit var attractionImage: String
    private var attractionCategoryId: Int = AttractionCategory.Fun.id
    private var attractionLocationLatitude: Double = 0.0
    private var attractionLocationLongitude: Double = 0.0

    var dismissListener: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.dialog_attraction_tile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readValuesFromBundle(arguments)

        with(view) {
            ivAttractionImage.loadImageWithCenterCrop(attractionImage)

            setupEllipsizeAndMaxLinesForDescriptionText(tvAttractionDescription)
            AttractionCategory.forId(attractionCategoryId).setCategoryColorAndTitle(tvCategoryTitle)

            tvAttractionTitle.text = attractionTitle
            tvAttractionDescription.text = attractionTeaser.fromHtml()
            btnGetDirections.setOnClickListener {
                activity?.openDirections(Location(attractionLocationLatitude, attractionLocationLongitude))
            }
            vClickCatcher.setOnClickListener {
                activity?.let {
                    AttractionDetailsActivity.startActivity(it, attractionId)
                    dismiss()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        customizeDialog()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        dismissListener()
    }

    private fun readValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            attractionId = it.getInt(ATTRACTION_ID_KEY, 0)
            attractionTitle = it.getString(ATTRACTION_TITLE_KEY, "")
            attractionTeaser = it.getString(ATTRACTION_TEASER_KEY, "")
            attractionImage = it.getString(ATTRACTION_IMAGE_KEY, "")
            attractionCategoryId = it.getInt(ATTRACTION_CATEGORY_ID_KEY, AttractionCategory.Fun.id)
            attractionLocationLatitude = it.getDouble(ATTRACTION_LOCATION_LATITUDE_KEY)
            attractionLocationLongitude = it.getDouble(ATTRACTION_LOCATION_LONGITUDE_KEY)
        }
    }

    private fun customizeDialog() {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val yPosition = context?.resources?.getDimensionPixelSize(
            R.dimen.attraction_info_dialog_bottom_margin
        ) ?: 0

        val attr = dialog.window?.attributes?.apply {
            gravity = Gravity.BOTTOM
            width = ViewGroup.LayoutParams.MATCH_PARENT
            y = yPosition
        }

        dialog.window?.attributes = attr
    }

    fun handleFailure(errorEvent: ConsumableEvent<Error>) {
        errorEvent.handleIfNotConsumed {
            consume {
                Toast.makeText(activity, "Faced an error: $it", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setupEllipsizeAndMaxLinesForDescriptionText(textView: TextView) {
        textView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val numberOfLines = textView.height / textView.lineHeight

                    textView.maxLines = numberOfLines
                    textView.ellipsize = TextUtils.TruncateAt.END
                }
            })
    }

    companion object {
        private const val ATTRACTION_ID_KEY = "attraction_id"
        private const val ATTRACTION_TITLE_KEY = "attraction_title"
        private const val ATTRACTION_TEASER_KEY = "attraction_teaser"
        private const val ATTRACTION_IMAGE_KEY = "attraction_image"
        private const val ATTRACTION_CATEGORY_ID_KEY = "attraction_category_id"
        private const val ATTRACTION_LOCATION_LATITUDE_KEY = "attraction_location_latitude"
        private const val ATTRACTION_LOCATION_LONGITUDE_KEY = "attraction_location_longitude"

        @SuppressWarnings("LongParameterList")
        fun newInstance(
            attractionId: Int,
            attractionTitle: String,
            attractionTeaser: String,
            attractionImage: String,
            attractionCategoryId: Int,
            attractionLocationLatitude: Double,
            attractionLocationLongitude: Double
        ): AttractionInfoDialogFragment {
            val fragment = AttractionInfoDialogFragment()

            fragment.arguments = Bundle().apply {
                putInt(ATTRACTION_ID_KEY, attractionId)
                putString(ATTRACTION_TITLE_KEY, attractionTitle)
                putString(ATTRACTION_TEASER_KEY, attractionTeaser)
                putString(ATTRACTION_IMAGE_KEY, attractionImage)
                putInt(ATTRACTION_CATEGORY_ID_KEY, attractionCategoryId)
                putDouble(ATTRACTION_LOCATION_LATITUDE_KEY, attractionLocationLatitude)
                putDouble(ATTRACTION_LOCATION_LONGITUDE_KEY, attractionLocationLongitude)
            }

            return fragment
        }
    }
}
