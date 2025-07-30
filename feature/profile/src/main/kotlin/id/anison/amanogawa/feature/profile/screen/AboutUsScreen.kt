package id.anison.amanogawa.feature.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.model.TeamUI
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.DetailText
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.profile.AboutUsScreenEvents
import id.anison.amanogawa.feature.profile.AboutUsScreenModel
import id.anison.amanogawa.feature.profile.toScreen


class AboutUsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<AboutUsScreenModel>()
        val state by screenModel.teamState
        val lazyListState = screenModel.lazyListState

        NavigationHandler(
            navigationState = screenModel.navigationEventState,
            toScreen = { toScreen() }
        )

        DataStateContent(
            state = state,
            content = { data ->
                AboutUsContent(
                    team = data,
                    lazyListState = lazyListState,
                    onEvents = { screenModel.onEvents(it) }
                )
            },
            errorContent = {
                AboutUsContent(
                    team = emptyList(),
                    lazyListState = lazyListState,
                    onEvents = { screenModel.onEvents(it) }
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AboutUsContent(
        team: List<TeamUI>,
        lazyListState: LazyListState,
        onEvents: (AboutUsScreenEvents) -> Unit,
    ) {
        val localUrlHandler = LocalUriHandler.current

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.about_us),
                    navigationIconContent = {
                        ArrowBackButton(onClick = { onEvents(AboutUsScreenEvents.PopBack) })
                    },
                )
            },
        ) { paddingValues ->
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    end = 8.dp,
                    bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                    else paddingValues.calculateBottomPadding()
                ),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    DetailText(
                        label = stringResource(R.string.amanogawa),
                        value = stringResource(R.string.amanogawa_about)
                    )

                    AboutUs()
                }

                if (team.isNotEmpty()) {
                    item {
                        Team(
                            teamMembers = team,
                            onTeamMemberClick = {
                                onEvents(
                                    AboutUsScreenEvents.NavigateToTitlesByTeamMemberName(
                                        teamMemberName = it.trim()
                                    )
                                )
                            }
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.our_socials),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://t.me/amanogawa_ua") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_telegram,
                                desc = stringResource(R.string.telegram_icon),
                                label = stringResource(R.string.telegram)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://www.instagram.com/amanogawa_ua") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_instagram,
                                desc = stringResource(R.string.instagram_icon),
                                label = stringResource(R.string.instagram)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://tiktok.com/@amanogawa_off") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_tiktok,
                                desc = stringResource(R.string.tiktok_icon),
                                label = stringResource(R.string.tiktok)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://youtube.com/@Amanogawa_UA") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_youtube,
                                desc = stringResource(R.string.youtube_icon),
                                label = stringResource(R.string.youtube)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://discord.gg/QPdbnzwjtM") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_discord,
                                desc = stringResource(R.string.discord_icon),
                                label = stringResource(R.string.discord)
                            )
                        }
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.podcast_amanokawa),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://podcasts.apple.com/ua/podcast/amanokawa/id1706368748") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_apple_podcasts,
                                desc = stringResource(R.string.applepodcasts_icon),
                                label = stringResource(R.string.applepodcasts)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://open.spotify.com/show/4glnP22wmfTafqLBunifc7") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_spotify,
                                desc = stringResource(R.string.spotify_icon),
                                label = stringResource(R.string.spotify)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://soundcloud.com/amanokawa-anime") },
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_soundcloud,
                                desc = stringResource(id = R.string.soundcloud_icon),
                                label = stringResource(id = R.string.soundcloud)
                            )
                        }
                    )
                }

                item {
                    Text(
                        text = stringResource(id = R.string.support_us),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://www.buymeacoffee.com/AmanogawaUA") },
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_buymeacoffee,
                                desc = stringResource(id = R.string.bymeacoffee_icon),
                                label = stringResource(id = R.string.buy_me_a_coffee)
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://www.patreon.com/amanogawa") },
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_patreon,
                                desc = stringResource(id = R.string.patreon_icon),
                                label = stringResource(id = R.string.patreon),
                            )
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { localUrlHandler.openUri("https://send.monobank.ua/jar/87knhUx5i7") },
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            SocialRow(
                                icon = R.drawable.ic_monobank,
                                desc = stringResource(id = R.string.mono_icon),
                                label = stringResource(id = R.string.monobank),
                                modifier = Modifier.clip(CircleShape)
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun AboutUs() {
        val linkStyle = SpanStyle(
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.secondary
        )
        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                ),
            ) {
                append(stringResource(R.string.our_plans))
            }
            withLink(
                link = LinkAnnotation.Url(
                    url = "https://t.me/amanogawa_ua",
                    styles = TextLinkStyles(style = linkStyle)
                )
            ) {
                append(stringResource(id = R.string.telegram_link))
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                ),
            ) {
                append(stringResource(R.string.after_telegramLinkText))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AboutSection(
                titleResId = R.string.translators,
                missionResId = R.string.translators_mission,
                doingResId = R.string.translators_do,
            )
            AboutSection(
                titleResId = R.string.voice_actors,
                missionResId = R.string.dabers_mission,
                doingResId = R.string.dabers_do,
            )
            AboutSection(
                titleResId = R.string.sound,
                missionResId = R.string.sound_mission,
                doingResId = R.string.sound_do,
            )
            DetailText(
                label = stringResource(R.string.main_target),
                value = stringResource(R.string.main_target_val)
            )
            Text(text = annotatedText)
        }
    }

    @Composable
    fun AboutSection(titleResId: Int, missionResId: Int, doingResId: Int) {
        Column {
            Text(
                text = stringResource(titleResId),
                fontWeight = FontWeight.Bold
            )
            DetailText(
                label = stringResource(R.string.mission),
                value = stringResource(missionResId)
            )
            DetailText(
                label = stringResource(R.string.what_are_they_doing),
                value = stringResource(doingResId)
            )
        }
    }

    @Composable
    private fun Team(
        teamMembers: List<TeamUI>,
        onTeamMemberClick: (String) -> Unit,
    ) {
        Column {
            Text(
                text = stringResource(R.string.team),
                fontWeight = FontWeight.Bold,
            )
            teamMembers.forEach { teammate ->
                val annotatedString = AnnotatedString(
                    text = teammate.name,
                    spanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline,
                    )
                )
                DetailText(
                    label = teammate.name,
                    teammateName = annotatedString,
                    value = " — " + teammate.roleText,
                    modifier = Modifier.clickable { onTeamMemberClick(teammate.name) }
                )
            }
        }
    }

    @Composable
    private fun SocialRow(
        icon: Int,
        desc: String,
        label: String,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = desc,
                modifier = modifier.size(24.dp),
            )
            Text(text = label)
        }
    }
}