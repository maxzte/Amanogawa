package id.anison.feature.profile.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.common.component.CustomListItem
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.profile.event.SettingsScreenEvents

class SettingsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow

        SettingsScreenContent(
            onEvents = { event ->
                when (event) {
                    is SettingsScreenEvents.PopBack -> {
                        navigator.pop()
                    }
                    is SettingsScreenEvents.PushAboutUs -> {
                        navigator.push(AboutUsScreen())
                    }
                    is SettingsScreenEvents.PushOurSocials -> {
                        navigator.push(OurSocialsScreen())
                    }
                    is SettingsScreenEvents.PushSupportUs -> {
                        navigator.push(SupportUsScreen())
                    }
                    is SettingsScreenEvents.PushAppearance -> {
                        navigator.push(AppearanceScreen())
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SettingsScreenContent(
        onEvents: (SettingsScreenEvents) -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AmanogawaTopBar(
                title = stringResource(R.string.settings),
                popNavigate = { onEvents(SettingsScreenEvents.PopBack) },
            )
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    CustomListItem(
                        text = stringResource(R.string.appearance),
                        leadingIcon = painterResource(id = R.drawable.ic_rounded_palette_24),
                        leadingIconDesc = "Appearance icon",
                        trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        trailingIconDesc = stringResource(id = R.string.keyboardarrowright),
                        onClick = { onEvents(SettingsScreenEvents.PushAppearance) },
                    )
                }
                item {
                    CustomListItem(
                        text = stringResource(R.string.about_us),
                        leadingIcon = painterResource(id = R.drawable.ic_rounded_info_24),
                        leadingIconDesc = "Info icon",
                        trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        trailingIconDesc = stringResource(id = R.string.keyboardarrowright),
                        onClick = { onEvents(SettingsScreenEvents.PushAboutUs) },
                    )
                }
                item {
                    CustomListItem(
                        text = stringResource(R.string.our_socials),
                        leadingIcon = painterResource(id = R.drawable.ic_rounded_link_24),
                        leadingIconDesc = "Link icon",
                        trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        trailingIconDesc = stringResource(id = R.string.keyboardarrowright),
                        onClick = { onEvents(SettingsScreenEvents.PushOurSocials) },
                    )
                }
                item {
                    CustomListItem(
                        text = stringResource(R.string.support_us),
                        leadingIcon = painterResource(R.drawable.ic_gifts_24),
                        leadingIconDesc = stringResource(R.string.gift_icon),
                        trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        trailingIconDesc = stringResource(id = R.string.keyboardarrowright),
                        onClick = { onEvents(SettingsScreenEvents.PushSupportUs) },
                    )
                }
            }
        }
    }
}