package id.anison.feature.tab

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import id.anison.feature.R

enum class AppDestinations(
    val index: UShort,
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    HOME(0u, R.string.home_tab, Icons.Default.Home, R.string.home_tab),
    SEARCH(1u, R.string.search_tab, Icons.Default.Search, R.string.search_tab),
    FAVORITES(2u, R.string.favorites_tab, Icons.Default.Favorite, R.string.favorites_tab),
    PROFILE(3u, R.string.profile_tab, Icons.Default.Person, R.string.profile_tab),
}
