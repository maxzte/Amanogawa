package id.anison.amanogawa.feature.search.navigation

import cafe.adriel.voyager.core.registry.screenModule
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.feature.search.screen.FilterResultScreen

val searchScreenModule = screenModule {
    register<SharedScreen.SearchResult> { provider ->
        FilterResultScreen(
            filterId = provider.filterId,
            filterName = provider.filterName,
            filterType = provider.filterType
        )
    }
}