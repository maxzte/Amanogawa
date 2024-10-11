package id.anison.feature.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.feature.common.MainScreenModel
import id.anison.feature.tab.FavoritesTab
import id.anison.feature.tab.HomeTab
import id.anison.feature.tab.ProfileTab
import id.anison.feature.tab.SearchTab


@Composable
fun CustomNavigationRail(sm: MainScreenModel) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 64.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 64.dp),
            verticalArrangement = Arrangement.Center
        ) {
            NavRailItem(HomeTab, sm = sm)
            NavRailItem(SearchTab, sm = sm)
            NavRailItem(FavoritesTab, sm = sm)
            NavRailItem(ProfileTab, sm = sm)
        }
    }
}

@Composable
private fun NavRailItem(
    tab: Tab,
    tabNavigator: TabNavigator = LocalTabNavigator.current,
    sm: MainScreenModel,
) {
    val isSelected = tabNavigator.current == tab
    val isFavoritesTab = tab.options.index == 2u.toUShort()
    val currentIcon = getIconForCurrentTab(tab)

    val selectedColor = when {
        isSelected && isFavoritesTab -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> LocalContentColor.current
    }
    NavigationRailItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = {
            if (isSelected) sm.setToRoute(true)
            tabNavigator.current = tab
        },
        icon = {
            Icon(
                painter = currentIcon!!,
                contentDescription = tab.options.title,
                tint = selectedColor,
            )
        },
        label = {
            Text(
                text = tab.options.title,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp
            )
        },
    )
}