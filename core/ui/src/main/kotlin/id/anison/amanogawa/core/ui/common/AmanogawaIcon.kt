package id.anison.amanogawa.core.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import id.anison.amanogawa.core.ui.R


object AmanogawaIcon {


    val arrowBack = Icons.AutoMirrored.Default.ArrowBack

    val amanogawaLogo
        @Composable get() = painterResource(id = R.drawable.amanogawa_logo)

}