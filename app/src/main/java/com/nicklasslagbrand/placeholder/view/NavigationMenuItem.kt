package com.nicklasslagbrand.placeholder.view

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.nicklasslagbrand.placeholder.R

class NavigationMenuItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {
    init {
        isSelected = false
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)

        if (isSelected) {
            changeButtonColor(R.color.colorNavigationItemNotSelected)
        } else {
            changeButtonColor(R.color.colorPrimary)
        }
    }

    private fun changeButtonColor(@ColorRes color: Int) {
        setTextColor(AppCompatResources.getColorStateList(context, color))

        // We assume that drawable top is specified in the xml resource
        // So on the second position in array we should have drawableTop resource drawable
        val drawableTop = compoundDrawables[1].mutate()
        drawableTop.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP)

        setCompoundDrawables(null, drawableTop, null, null)
    }
}
