package id.anison.feature.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.common.DataState
import id.anison.feature.common.component.CustomTopAppBar
import id.anison.feature.common.component.DetailText
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.model.TeamItemUI
import id.anison.feature.profile.AboutUsScreenModel
import id.anison.feature.profile.event.AboutUsScreenEvents
import id.anison.feature.search.screen.ExtendedSearchTitlesScreen
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel


class AboutUsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AboutUsScreenModel>()
        val extendedSearchScreenModel = koinScreenModel<ExtendedSearchScreenModel>()
        val state by screenModel.teamState.collectAsState()
        when (val team = state.team) {
            is DataState.Error ->
                AboutUsContent(
                    onEvents = { event ->
                        when (event) {
                            is AboutUsScreenEvents.PopBack -> navigator.pop()
                            is AboutUsScreenEvents.NavigateToTitlesByTeammateName -> {
                                extendedSearchScreenModel.searchByTeammateName(event.name)
                                navigator.push(ExtendedSearchTitlesScreen(event.name))
                            }
                        }
                    }
                )

            is DataState.Loading -> ProgressIndicator()
            is DataState.Success -> {
                AboutUsContent(
                    team = team.data,
                    onEvents = { event ->
                        when (event) {
                            is AboutUsScreenEvents.PopBack -> navigator.pop()
                            is AboutUsScreenEvents.NavigateToTitlesByTeammateName -> {
                                extendedSearchScreenModel.searchByTeammateName(event.name)
                                navigator.push(ExtendedSearchTitlesScreen(event.name))
                            }
                        }
                    }
                )
            }

            null -> Unit
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AboutUsContent(
        team: List<TeamItemUI>? = null,
        onEvents: (AboutUsScreenEvents) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            CustomTopAppBar(
                title = stringResource(R.string.about_us),
                popNavigate = { onEvents(AboutUsScreenEvents.PopBack) },
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    DetailText(
                        label = stringResource(R.string.amanogawa),
                        value = stringResource(R.string.amanogawa_about)
                    )
                }
                AboutUs(
                    team = team,
                    onEvents = onEvents,
                )
            }
        }
    }

    @Composable
    fun AboutUs(
        team: List<TeamItemUI>? = null,
        onEvents: (AboutUsScreenEvents) -> Unit,
    ) {
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
            if (team == null) TeamOffline(onEvents)
            else Team(team, onEvents)
        }
    }

    @Composable
    fun AboutSection(titleResId: Int, missionResId: Int, doingResId: Int) {
        Column {
            Text(text = stringResource(titleResId), fontWeight = FontWeight.Bold)
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
    private fun TeamOffline(
        onEvents: (AboutUsScreenEvents) -> Unit,
    ) {
        val teammateInfo = mapOf(
            R.string.rainy984 to R.string.rainy_position,
            R.string.bodya500icq to R.string.bodya500icq_position,
            R.string.yunko to R.string.yunko_position,
            R.string.spalah to R.string.spalah_position,
            R.string.suni to R.string.suni_position,
            R.string.maira to R.string.maira_position,
            R.string.son to R.string.son_position,
            R.string.boni to R.string.boni_position,
            R.string.yudgi to R.string.yudgi_position,
            R.string.dedrds to R.string.dedrds_position,
            R.string.alisand to R.string.alisand_position,
            R.string.nutix to R.string.nutix_position,
            R.string.dennyprout to R.string.dennyprout_position,
            R.string.furunze to R.string.furunze_position,
            R.string.seraficus to R.string.seraficus_position,
            R.string.andr to R.string.andr_position,
            R.string.angelbagel to R.string.angelbagel_position,
            R.string.hazuki to R.string.hazuki_position,
            R.string.nekonosan to R.string.nekonosan_position,
        )
        Column {
            Text(
                text = stringResource(R.string.team),
                fontWeight = FontWeight.Bold,
            )
            teammateInfo.forEach { (nameId, positionId) ->
                val name = stringResource(id = nameId)
                val position = stringResource(id = positionId)
                DetailText(
                    label = name,
                    value = position,
                    modifier = Modifier.clickable {
                        onEvents(AboutUsScreenEvents.NavigateToTitlesByTeammateName(name.trim()))
                    }
                )
            }
        }
    }

    @Composable
    fun Team(
        team: List<TeamItemUI>,
        onEvents: (AboutUsScreenEvents) -> Unit,
    ) {
        Column {
            Text(
                text = stringResource(R.string.team),
                fontWeight = FontWeight.Bold,
            )
            team.forEach { teammate ->
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
                    value = " — " + teammate.role_text,
                    modifier = Modifier.clickable {
                        onEvents(AboutUsScreenEvents.NavigateToTitlesByTeammateName(name = teammate.name.trim()))
                    }
                )
            }
        }
    }
}