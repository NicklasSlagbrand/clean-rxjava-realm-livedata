package com.nicklasslagbrand.placeholder.feature.purchase.card

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.nicklasslagbrand.placeholder.R
import kotlinx.android.synthetic.main.view_people_selection.view.*

class PeopleSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.view_people_selection, this)
    }

    fun setSectionTitle(title: String) {
        tvSectionTitle.text = title
    }

    fun setPeopleCount(count: Int) {
        tvPersonCount.text = count.toString()
    }

    fun setTotalSum(sum: String) {
        tvTotalSum.text = sum
    }

    fun onAddClicked(listener: PeopleSelectionView.() -> Unit) {
        fbAddPerson.setOnClickListener {
            listener()
        }
    }

    fun onRemoveClicked(listener: PeopleSelectionView.() -> Unit) {
        fbRemovePerson.setOnClickListener {
            listener()
        }
    }

    fun enableRemoveButton(isEnabled: Boolean) {
        fbRemovePerson.isEnabled = isEnabled
    }

    fun enableAddButton(isEnabled: Boolean) {
        fbAddPerson.isEnabled = isEnabled
    }
}
