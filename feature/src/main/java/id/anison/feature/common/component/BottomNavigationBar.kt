package id.anison.feature.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.feature.R
import id.anison.feature.common.MainScreenModel
import id.anison.feature.tab.FavoritesTab
import id.anison.feature.tab.HomeTab
import id.anison.feature.tab.ProfileTab
import id.anison.feature.tab.SearchTab


@Composable
fun CustomNavigationBar(
    sm: MainScreenModel,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .background(containerColor)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxWidth()
            .heightIn(max = 50.dp)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
    ) {
        TabNavigationItem(HomeTab, sm = sm)
        TabNavigationItem(SearchTab, sm = sm)
        TabNavigationItem(FavoritesTab, sm = sm)
        TabNavigationItem(ProfileTab, sm = sm)
    }
}

@Composable
private fun TabNavigationItem(
    tab: Tab,
    tabNav: TabNavigator = LocalTabNavigator.current,
    sm: MainScreenModel,
) {
    val isSelected = tabNav.current == tab
    val isFavoritesTab = tab.options.index == 2u.toUShort()
    val currentIcon = getIconForCurrentTab(tab)
    val selectedColor = when {
        isSelected && isFavoritesTab -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> LocalContentColor.current
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 4.dp)
            .heightIn(max = 50.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                if (isSelected) sm.setToRoute(true)
                tabNav.current = tab
            },
    ) {
        Icon(
            painter = currentIcon!!,
            contentDescription = tab.options.title,
            tint = selectedColor,
            modifier = Modifier
        )
        Text(
            text = tab.options.title,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            modifier = Modifier
        )
    }
}

@Composable
fun getIconForCurrentTab(tab: Tab): Painter? {
    val tabNavigator = LocalTabNavigator.current
    val isSelected = tabNavigator.current == tab
    val isHomeTab = tab.options.index == 0u.toUShort()
    val isFavoritesTab = tab.options.index == 2u.toUShort()
    val isProfileTab = tab.options.index == 3u.toUShort()

    return when {
        isSelected && isHomeTab -> painterResource(id = R.drawable.ic_home_filled_24px)
        isSelected && isFavoritesTab -> painterResource(id = R.drawable.ic_favorite_filled_24px)
        isSelected && isProfileTab -> painterResource(id = R.drawable.ic_person_filled_24px)
        else -> tab.options.icon
    }
}