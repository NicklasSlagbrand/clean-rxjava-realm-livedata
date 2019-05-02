package com.nicklasslagbrand.placeholder.domain.model

data class CardsContainer(val currentCards: List<PurchasedCard>, val isNewCardAvailable: Boolean = false)
