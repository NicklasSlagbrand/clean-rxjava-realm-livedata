package com.nicklasslagbrand.placeholder.feature.main.attraction

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.OpeningHoursItem
import com.nicklasslagbrand.placeholder.extension.gone
import com.nicklasslagbrand.placeholder.extension.inflate
import com.nicklasslagbrand.placeholder.extension.isVisible
import com.nicklasslagbrand.placeholder.extension.visible
import kotlinx.android.synthetic.main.view_opening_hours.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class OpeningHoursView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val dateInputFormat: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("yyyy-MM-dd")
    }
    private val dateOutputFormat: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("dd.MM.yyyy")
    }

    init {
        inflate(context, R.layout.view_opening_hours, this)

        layoutOpeningHoursHeader.setOnClickListener {
            if (rvOpeningHoursList.isVisible()) {
                rvOpeningHoursList.gone()
                ivOpeningHoursArrow.animate()
                    .rotation(DOWN_ARROW_ANGLE)
                    .start()
            } else {
                rvOpeningHoursList.visible()
                ivOpeningHoursArrow.animate()
                    .rotation(UP_ARROW_ANGLE)
                    .start()
            }
        }
    }

    fun setOpeningHourItems(items: List<OpeningHoursItem>) {
        val listItems = mapModelToListItems(items)

        rvOpeningHoursList.adapter = OpeningHoursAdapter(listItems)
    }

    private fun mapModelToListItems(items: List<OpeningHoursItem>): List<OpeningHoursListItem> {
        return items.map { hourItem ->
            val startDate = dateInputFormat.parseDateTime(hourItem.startDate)
            val endDate = dateInputFormat.parseDateTime(hourItem.endDate)

            val startString = startDate.toString(dateOutputFormat)
            val endString = endDate.toString(dateOutputFormat)

            val headerItem = listOf(OpeningHoursListItem(true, "$startString - $endString"))
            val childItems = hourItem.days.map { day ->
                OpeningHoursListItem(text = "${day.name.capitalize()}: ${day.opening} - ${day.closing}")
            }

            return@map headerItem + childItems
        }.flatten()
    }

    companion object {
        const val DOWN_ARROW_ANGLE = 180f
        const val UP_ARROW_ANGLE = 0f
    }

    data class OpeningHoursListItem(val isHeader: Boolean = false, val text: String)

    class OpeningHoursAdapter(private val items: List<OpeningHoursListItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return OpeningHourViewHolder(parent.inflate(R.layout.item_opening_hour) as TextView)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = items[position]

            (holder as OpeningHourViewHolder).bind(item)
        }

        inner class OpeningHourViewHolder(itemView: TextView) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: OpeningHoursListItem) {
                val texColor = if (item.isHeader) {
                    ContextCompat.getColor(itemView.context, android.R.color.black)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.searchGray)
                }
                with(itemView as TextView) {
                    text = item.text
                    setTextColor(texColor)
                }
            }
        }
    }
}
