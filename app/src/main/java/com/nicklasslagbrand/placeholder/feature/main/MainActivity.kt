package com.nicklasslagbrand.placeholder.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_CIRCLE_COLOR
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_DIM_COLOR
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_OUTERCIRCLE_ALPHA
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_RADIUS
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_TEXTSIZE
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_TEXT_COLOR
import com.nicklasslagbrand.placeholder.Constants.Companion.INSTRUCTION_VIEW_TITLE_TEXTSIZE
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowAttractionsView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowCardsView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowFavouriteView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowMapsView
import com.nicklasslagbrand.placeholder.data.viewmodel.NavigationMenuViewModel.Event.ShowMenuView
import com.nicklasslagbrand.placeholder.data.viewmodel.SideMenuViewModel
import com.nicklasslagbrand.placeholder.extension.consume
import com.nicklasslagbrand.placeholder.extension.deselect
import com.nicklasslagbrand.placeholder.extension.disable
import com.nicklasslagbrand.placeholder.extension.enable
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.openUrl
import com.nicklasslagbrand.placeholder.extension.select
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import com.nicklasslagbrand.placeholder.feature.main.attraction.AttractionsListFragment
import com.nicklasslagbrand.placeholder.feature.main.attraction.FavouritesFragment
import com.nicklasslagbrand.placeholder.feature.main.card.CardsFragment
import com.nicklasslagbrand.placeholder.feature.main.info.AppInfoActivity
import com.nicklasslagbrand.placeholder.feature.main.map.MapFragment
import com.nicklasslagbrand.placeholder.feature.main.message.MessageActivity
import com.nicklasslagbrand.placeholder.feature.main.receive.ReceiveCardActivity
import com.nicklasslagbrand.placeholder.feature.purchase.PurchaseActivity
import com.nicklasslagbrand.placeholder.view.PlaceholerDrawerLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_navigation_bottom.*
import org.koin.android.viewmodel.ext.android.viewModel

@SuppressWarnings("TooManyFunctions")
class MainActivity : BaseActivity() {
    private val navigationMenuViewModel: NavigationMenuViewModel by viewModel()
    private val sideMenuViewModel: SideMenuViewModel by viewModel()

    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        subscribeLiveData()

        intiViews()

        val shouldGoToCards = intent.getBooleanExtra(SHOULD_NAVIGATE_TO_CARDS_KEY, false)
        navigationMenuViewModel.initialize(shouldGoToCards)

        navigationMenuViewModel.showInstructionsView()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val shouldGoToCards = intent.getBooleanExtra(SHOULD_NAVIGATE_TO_CARDS_KEY, false)
        navigationMenuViewModel.onBackToScreen(shouldGoToCards)
    }

    private fun intiViews() {
        btnNavigationMap.setOnClickListener {
            navigationMenuViewModel.showMapsView()
        }

        btnNavigationAttractions.setOnClickListener {
            navigationMenuViewModel.showAttractionsView()
        }

        btnNavigationFavourites.setOnClickListener {
            navigationMenuViewModel.showFavouriteView()
        }

        btnNavigationMenu.setOnClickListener {
            navigationMenuViewModel.showMenuView()
        }

        btnNavigationCards.setOnClickListener {
            navigationMenuViewModel.showCardsView()
        }

        setupDrawer()
    }

    private fun showInstructions() {
        val sequence = TapTargetSequence(this).targets(
            getInstructionsView(btnNavigationCards,
                R.drawable.ic_navigation_cards,
                getString(R.string.label_intro_screen_use),
                getString(R.string.label_intro_screen_use_description)),
            getInstructionsView(btnNavigationFavourites,
                R.drawable.ic_navigation_favourites,
                getString(R.string.label_intro_screen_favoutires),
                getString(R.string.label_intro_screen_favoutires_descr)))
            getInstructionsView(btnNavigationFavourites,
                R.drawable.ic_navigation_attractions,
                getString(R.string.label_intro_screen_explore),
                getString(R.string.label_intro_screen_explore_descr))

        sequence.start()
    }

    private fun getInstructionsView(view: View, icon: Int, title: String, description: String): TapTarget {
        return TapTarget.forView(view, title, description)
            .outerCircleColor(com.nicklasslagbrand.placeholder.R.color.primary)
            .outerCircleAlpha(INSTRUCTION_VIEW_OUTERCIRCLE_ALPHA)
            .targetCircleColor(INSTRUCTION_VIEW_CIRCLE_COLOR)
            .titleTextSize(INSTRUCTION_VIEW_TITLE_TEXTSIZE)
            .descriptionTextSize(INSTRUCTION_VIEW_TEXTSIZE)
            .textColor(INSTRUCTION_VIEW_TEXT_COLOR)
            .dimColor(INSTRUCTION_VIEW_DIM_COLOR)
            .drawShadow(true)
            .cancelable(false)
            .tintTarget(true)
            .icon(getDrawable(icon))
            .transparentTarget(false)
            .targetRadius(INSTRUCTION_VIEW_RADIUS)
    }

    private fun setupDrawer() {
        val drawerContent = PlaceholerDrawerLayout(this)

        drawer = DrawerBuilder()
            .withDrawerGravity(Gravity.END)
            .withActivity(this)
            .withCustomView(drawerContent)
            .build()

        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawer.drawerLayout.isFocusableInTouchMode = false // https://stackoverflow.com/a/18997280/968702

        drawerContent.onMenuItemsClickListener = ActivityMenuItemListener()
    }

    private fun subscribeLiveData() {
        observe(navigationMenuViewModel.eventsLiveData) {
            handleNavigationEvents(it)
        }

        observe(sideMenuViewModel.eventsLiveData) {
            handleDrawerEvents(it)
        }
    }

    private fun handleDrawerEvents(event: ConsumableEvent<SideMenuViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is SideMenuViewModel.Event.OpenLinkInExternalBrowser -> consume {
                    openUrl(it.url)
                }
                SideMenuViewModel.Event.OpenAppInfoScreen -> consume {
                    AppInfoActivity.startActivity(this)
                }
                SideMenuViewModel.Event.OpenReceiveScreen -> consume {
                    ReceiveCardActivity.startActivity(this)
                }
                SideMenuViewModel.Event.OpenPurchaseScreen -> consume {
                    PurchaseActivity.startActivity(this)
                }
                SideMenuViewModel.Event.OpenMessageScreen -> consume {
                    MessageActivity.startActivity(this)
                }
            }
        }
    }

    private fun handleNavigationEvents(event: ConsumableEvent<Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is ShowMapsView -> {
                    switchFragment(VIEW_PAGER_MAPS_POSITION)
                    selectButtonAndUnSelectOthers(btnNavigationMap)
                    true
                }
                is ShowAttractionsView -> {
                    switchFragment(VIEW_PAGER_ATTRACTIONS_POSITION)
                    selectButtonAndUnSelectOthers(btnNavigationAttractions)
                    true
                }
                is ShowCardsView -> {
                    switchFragment(VIEW_PAGER_CARDS_POSITION)
                    selectButtonAndUnSelectOthers(btnNavigationCards)
                    btnNavigationCards.disable()

                    true
                }
                is ShowFavouriteView -> {
                    switchFragment(VIEW_PAGER_FAVOURITES_POSITION)
                    selectButtonAndUnSelectOthers(btnNavigationFavourites)
                    true
                }
                is ShowMenuView -> {
                    drawer.openDrawer()
                    true
                }
                is Event.ShowInstructionsView -> {
                    showInstructions()
                    true
                }
            }
        }
    }

    private fun switchFragment(pageId: Int) {
        val f = getItem(pageId)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFragment, f)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()

            return
        }

        super.onBackPressed()
    }

    private fun selectButtonAndUnSelectOthers(button: View) {
        btnNavigationMap.deselect()
        btnNavigationAttractions.deselect()
        btnNavigationFavourites.deselect()
        btnNavigationCards.enable()

        button.select()
    }

    inner class ActivityMenuItemListener : PlaceholerDrawerLayout.OnSideMenuItemClickListener {
        override fun onBuyCardClicked() {
            drawer.closeDrawer()
            sideMenuViewModel.showPurchaseScreen()
        }
        override fun onMessageClicked() {
            drawer.closeDrawer()
            sideMenuViewModel.showMessageScreen()
        }

        override fun onReceiveCardClicked() {
            drawer.closeDrawer()
            sideMenuViewModel.showReceiveScreen()
        }

        override fun onAppInfoClicked() {
            drawer.closeDrawer()
            sideMenuViewModel.showAppInfo()
        }

        override fun onWhatsIncludedClicked() {
            sideMenuViewModel.showWhatsIncluded()
            drawer.closeDrawer()
        }

        override fun onHowItWorksClicked() {
            sideMenuViewModel.showHowItWorks()
            drawer.closeDrawer()
        }

        override fun onConditionsClicked() {
            sideMenuViewModel.showConditions()
            drawer.closeDrawer()
        }

        override fun onDiscountsClicked() {
            sideMenuViewModel.showDiscounts()
            drawer.closeDrawer()
        }

        override fun onTransportClicked() {
            sideMenuViewModel.showTransport()
            drawer.closeDrawer()
        }

        override fun onFaqAndHelpClicked() {
            sideMenuViewModel.showFaqAndHelp()
            drawer.closeDrawer()
        }
    }

    private fun getItem(position: Int) = when (position) {
        VIEW_PAGER_MAPS_POSITION -> MapFragment()
        VIEW_PAGER_ATTRACTIONS_POSITION -> AttractionsListFragment()
        VIEW_PAGER_CARDS_POSITION -> CardsFragment()
        VIEW_PAGER_FAVOURITES_POSITION -> FavouritesFragment()
        else -> throw IllegalArgumentException("Index $position is not supported.")
    }

    companion object {
        const val VIEW_PAGER_MAPS_POSITION = 0
        const val VIEW_PAGER_ATTRACTIONS_POSITION = 1
        const val VIEW_PAGER_CARDS_POSITION = 2
        const val VIEW_PAGER_FAVOURITES_POSITION = 3

        const val SHOULD_NAVIGATE_TO_CARDS_KEY = "navigate_to_cards"

        fun startActivity(context: Context, shouldGoToCards: Boolean = false) {
            context.startActivity(
                Intent(context, MainActivity::class.java)
                    .putExtra(
                        SHOULD_NAVIGATE_TO_CARDS_KEY, shouldGoToCards
                    )
            )
        }

        fun returnToActivity(context: Context, shouldGoToCards: Boolean = false) {
            context.startActivity(returnToActivityIntent(context, shouldGoToCards))
        }

        fun returnToActivityIntent(context: Context, shouldGoToCards: Boolean = false): Intent {
            return Intent(context, MainActivity::class.java)
                .putExtra(
                    SHOULD_NAVIGATE_TO_CARDS_KEY, shouldGoToCards
                )
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}
