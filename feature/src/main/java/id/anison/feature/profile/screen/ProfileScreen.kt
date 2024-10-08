package id.anison.feature.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import id.anison.feature.common.component.CustomTopAppBar
import id.anison.feature.ui.LocalGradientColors

class ProfileScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        ProfileScreenContent(navigator)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreenContent(
        navigator: Navigator,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomTopAppBar(
                title = stringResource(R.string.profile),
                actionBtnContent = {
                    IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(id = R.string.search_icon)
                        )
                    }
                }
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.ic_access_time_24),
                            contentDescription = stringResource(R.string.coming_soon)
                        )
                        Text(text = stringResource(id = R.string.coming_soon))
                    }
                }
            }
        }
    }
}

