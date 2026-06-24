@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.profile.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar

class LicensesScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val libs = remember {
            val resId = context.resources.getIdentifier("aboutlibraries", "raw", context.packageName)
            val inputStream = context.resources.openRawResource(resId)
            val json = inputStream.bufferedReader().use { it.readText() }
            Libs.Builder().withJson(json).build()
        }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.open_source_licenses),
                    navigationIconContent = {
                        ArrowBackButton(onClick = { navigator.pop() })
                    },
                )
            },
        ) { paddingValues ->
            LibrariesContainer(
                libraries = libs,
                contentPadding = PaddingValues(bottom = AmanogawaConst.bottomBarInsets),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }
}
