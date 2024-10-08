package id.anison.feature.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import id.anison.feature.common.component.CustomTopAppBar
import id.anison.feature.common.component.SocialRow
import id.anison.feature.ui.LocalGradientColors

class SupportUsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        SupportUsScreenContent(navigator)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SupportUsScreenContent(
        navigator: Navigator,
    ) {
        val uriHandler = LocalUriHandler.current
        Column(modifier = Modifier.fillMaxSize()) {
            CustomTopAppBar(
                title = stringResource(id = R.string.support_us),
                popNavigate = { navigator.pop() },
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Button(
                        onClick = { uriHandler.openUri("https://www.buymeacoffee.com/AmanogawaUA") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SocialRow(
                            icon = R.drawable.ic_buymeacoffee,
                            desc = stringResource(R.string.bymeacoffee_icon),
                            label = stringResource(R.string.buy_me_a_coffee)
                        )
                    }
                }
                item {
                    Button(
                        onClick = { uriHandler.openUri("https://www.patreon.com/amanogawa") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SocialRow(
                            icon = R.drawable.ic_patreon,
                            desc = stringResource(R.string.patreon_icon),
                            label = stringResource(
                                R.string.patreon
                            ),
                        )
                    }
                }
                item {
                    Button(
                        onClick = { uriHandler.openUri("https://send.monobank.ua/jar/87knhUx5i7") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SocialRow(
                            icon = R.drawable.ic_monobank,
                            desc = stringResource(R.string.mono_icon),
                            label = stringResource(
                                R.string.monobank
                            ),
                            modifier = Modifier.clip(CircleShape)
                        )
                    }
                }
            }
        }
    }
}