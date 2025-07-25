package id.anison.amanogawa.core.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import id.anison.amanogawa.core.ui.R


object AmanogawaIcon {


    val arrowBack = Icons.AutoMirrored.Default.ArrowBack
    val arrowForward = Icons.AutoMirrored.Default.ArrowForward

    val amanogawaLogo
        @Composable get() = painterResource(id = R.drawable.amanogawa_logo)

}