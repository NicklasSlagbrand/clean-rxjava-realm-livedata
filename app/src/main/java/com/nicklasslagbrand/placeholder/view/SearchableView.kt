package com.nicklasslagbrand.placeholder.view

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Attraction
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import com.nicklasslagbrand.placeholder.extension.gone
import com.nicklasslagbrand.placeholder.extension.visible
import kotlinx.android.synthetic.main.view_search.view.backView
import kotlinx.android.synthetic.main.view_search.view.etSearch
import kotlinx.android.synthetic.main.view_search.view.fabSearch
import kotlinx.android.synthetic.main.view_search.view.layoutResults
import kotlinx.android.synthetic.main.view_search.view.rvAttractionCategories
import kotlinx.android.synthetic.main.view_search.view.rvSearchResults
import kotlinx.android.synthetic.main.view_search.view.tilSearch
import kotlinx.android.synthetic.main.view_search.view.tvNoResults
import kotlinx.android.synthetic.main.view_search.view.vDecorLine
import kotlinx.android.synthetic.main.view_search.view.vFocusHandler

class SearchableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var searchState: SearchState = SearchState.CLOSE
    private lateinit var adapter: SearchAdapter

    var onSearchResultSelected: OnResultSelectedListener? = null
    var onCategoriesSelected: OnCategoriesSelectedListener? = null

    init {
        inflate(context, R.layout.view_search, this)

        fabSearch.setOnClickListener {
            handleSearchClick()
        }

        backView.setOnClickListener { hideSearch() }

        setupResultRecyclerView()
    }

    fun setOnSearchTextChangeListener(textListener: (String) -> Unit) {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length >= DEFAULT_STRING_SEARCH_LENGTH) {
                    textListener(s.toString())
                } else if (s.trim().isEmpty()) {
                    rvSearchResults.gone()
                    tvNoResults.gone()
                    layoutResults.gone()
                    vDecorLine.gone()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun renderCategories(categories: List<AttractionCategory>) {
        setupCategoriesRecyclerView(categories)
    }

    private fun setupCategoriesRecyclerView(categories: List<AttractionCategory>) {
        val adapter = CategoriesAdapter(context) {
            onCategoriesSelected?.onCategoriesSelected(it)
        }

        adapter.categories = categories

        rvAttractionCategories.adapter = adapter
        rvAttractionCategories.setHasFixedSize(true)
    }

    private fun setupResultRecyclerView() {
        rvSearchResults.layoutManager = LinearLayoutManager(context)
        adapter = SearchAdapter(context) {
            hideSearch()
            onSearchResultSelected?.onSearchResultSelected(it)
        }

        rvSearchResults.adapter = adapter
    }

    private fun handleSearchClick() {
        when (searchState) {
            SearchState.CLOSE -> showSearch()
            SearchState.OPEN -> hideSearch()
        }
    }

    private fun showSearch() {
        tilSearch.visible()
        rvAttractionCategories.visible()

        fabSearch.setImageResource(R.drawable.ic_search_close)
        searchState = SearchState.OPEN

        vFocusHandler.requestFocus()
    }

    fun hideSearch() {
        layoutResults.gone()
        vDecorLine.gone()
        tilSearch.gone()
        tvNoResults.gone()
        rvAttractionCategories.gone()

        fabSearch.setImageResource(R.drawable.ic_search)
        searchState = SearchState.CLOSE

        clearSearch()
        hideResults()
        hideKeyboardFrom()
    }

    private fun hideResults() {
        backView.gone()
        rvSearchResults.gone()
        vDecorLine.gone()
        adapter.results = emptyList()
    }

    private fun clearSearch() {
        etSearch.text = null
    }

    fun showSearchResults(searchResults: List<Attraction>) {
        if (searchResults.isNotEmpty()) {
            backView.visible()
            rvSearchResults.visible()
            tvNoResults.gone()
            layoutResults.visible()
            vDecorLine.visible()
            adapter.results = searchResults
        } else {
            rvSearchResults.gone()
            tvNoResults.visible()
            layoutResults.visible()
            backView.gone()
            vDecorLine.visible()
            adapter.results = emptyList()
        }
    }

    private fun hideKeyboardFrom() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    interface OnResultSelectedListener {
        fun onSearchResultSelected(attraction: Attraction)
    }

    interface OnCategoriesSelectedListener {
        fun onCategoriesSelected(categories: List<AttractionCategory>)
    }

    enum class SearchState {
        CLOSE,
        OPEN
    }

    companion object {
        const val DEFAULT_STRING_SEARCH_LENGTH = 1
    }
}
