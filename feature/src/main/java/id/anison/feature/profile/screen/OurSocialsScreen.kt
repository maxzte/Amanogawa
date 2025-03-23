package id.anison.feature.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.common.component.SocialRow

class OurSocialsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        SocialLinks(navigator)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SocialLinks(
        navigator: Navigator,
    ) {
        val localUrlHandler = LocalUriHandler.current
        Column(modifier = Modifier.fillMaxSize()) {
            AmanogawaTopBar(
                title = stringResource(id = R.string.our_socials),
                popNavigate = { navigator.pop() }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Button(onClick = { localUrlHandler.openUri("https://t.me/amanogawa_ua") }) {
                        SocialRow(
                            icon = R.drawable.ic_telegram,
                            desc = stringResource(R.string.telegram_icon),
                            label = stringResource(R.string.telegram)
                        )
                    }
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://www.instagram.com/amanogawa_ua") }) {
                        SocialRow(
                            icon = R.drawable.ic_instagram,
                            desc = stringResource(R.string.instagram_icon),
                            label = stringResource(R.string.instagram)
                        )
                    }
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://tiktok.com/@amanogawa_off") }) {
                        SocialRow(
                            icon = R.drawable.ic_tiktok,
                            desc = stringResource(R.string.tiktok_icon),
                            label = stringResource(R.string.tiktok)
                        )
                    }
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://youtube.com/@Amanogawa_UA") }) {
                        SocialRow(
                            icon = R.drawable.ic_youtube,
                            desc = stringResource(R.string.youtube_icon),
                            label = stringResource(R.string.youtube)
                        )
                    }
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://discord.gg/QPdbnzwjtM") }) {
                        SocialRow(
                            icon = R.drawable.ic_discord,
                            desc = stringResource(R.string.discord_icon),
                            label = stringResource(R.string.discord)
                        )
                    }
                }
                item {
                    Text(text = stringResource(R.string.podcast_amanokawa))
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://podcasts.apple.com/ua/podcast/amanokawa/id1706368748") }) {
                        SocialRow(
                            icon = R.drawable.ic_apple_podcasts,
                            desc = stringResource(R.string.applepodcasts_icon),
                            label = stringResource(R.string.applepodcasts)
                        )
                    }
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://open.spotify.com/show/4glnP22wmfTafqLBunifc7") }) {
                        SocialRow(
                            icon = R.drawable.ic_spotify,
                            desc = stringResource(R.string.spotify_icon),
                            label = stringResource(R.string.spotify)
                        )
                    }
                }
                item {
                    Button(onClick = { localUrlHandler.openUri("https://soundcloud.com/amanokawa-anime") }) {
                        SocialRow(
                            icon = R.drawable.ic_soundcloud,
                            desc = stringResource(R.string.soundcloud_icon),
                            label = stringResource(R.string.soundcloud)
                        )
                    }
                }
            }
        }
    }
}