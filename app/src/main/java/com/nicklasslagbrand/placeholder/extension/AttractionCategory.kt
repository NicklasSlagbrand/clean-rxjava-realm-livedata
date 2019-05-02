package com.nicklasslagbrand.placeholder.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory

fun AttractionCategory.getLogo(context: Context): Drawable {
    return when (this) {
        AttractionCategory.Art -> context.getDrawable(R.drawable.logo_art_design)!!
        AttractionCategory.Fun -> context.getDrawable(R.drawable.logo_family_fun)!!
        AttractionCategory.History -> context.getDrawable(R.drawable.logo_history)!!
        AttractionCategory.Royal -> context.getDrawable(R.drawable.logo_royal)!!
        AttractionCategory.Sight -> context.getDrawable(R.drawable.logo_sight)!!
        AttractionCategory.Favourite -> context.getDrawable(R.drawable.logo_favorite)!!
    }
}

fun AttractionCategory.getTitle(context: Context): String {
    return when (this) {
        AttractionCategory.Art -> context.getString(R.string.attraction_type_art)
        AttractionCategory.Fun -> context.getString(R.string.attraction_type_family)
        AttractionCategory.History -> context.getString(R.string.attraction_type_history)
        AttractionCategory.Royal -> context.getString(R.string.attraction_type_royal)
        AttractionCategory.Sight -> context.getString(R.string.attraction_type_sight)
        AttractionCategory.Favourite -> context.getString(R.string.attraction_type_favorite)
    }
}

fun AttractionCategory.setCategoryColorAndTitle(categoryTextView: TextView) {
    val context = categoryTextView.context

    when (id) {
        Attraction.ART_CATEGORY_ID -> categoryTextView.background.overrideColor(
            context,
            R.color.colorCategoryArt
        )
        Attraction.FUN_CATEGORY_ID -> categoryTextView.background.overrideColor(
            context,
            R.color.colorCategoryFun
        )
        Attraction.HISTORY_CATEGORY_ID -> categoryTextView.background.overrideColor(
            context,
            R.color.colorCategoryHistory
        )
        Attraction.ROYAL_CATEGORY_ID -> categoryTextView.background.overrideColor(
            context,
            R.color.colorCategoryRoyal
        )
        Attraction.SIGHT_CATEGORY_ID -> categoryTextView.background.overrideColor(
            context,
            R.color.colorCategorySight
        )
    }

    categoryTextView.text = getTitle(context)
}
