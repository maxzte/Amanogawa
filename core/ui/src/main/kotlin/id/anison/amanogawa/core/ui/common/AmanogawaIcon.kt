package id.anison.amanogawa.core.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import id.anison.amanogawa.core.ui.R


object AmanogawaIcon {


    val arrowBack = Icons.AutoMirrored.Default.ArrowBack
    val arrowForward = Icons.AutoMirrored.Default.ArrowForward
    val refresh = Icons.Filled.Refresh
    val close = Icons.Filled.Close
    val check = Icons.Default.Check
    val settings = Icons.Rounded.Settings
    val keyboardArrowRight = Icons.AutoMirrored.Outlined.KeyboardArrowRight
    val favorite = Icons.Filled.Favorite
    val favoriteBorder = Icons.Filled.FavoriteBorder
    val playArrow = Icons.Rounded.PlayArrow
    val done = Icons.Filled.Done
    val search = Icons.Filled.Search
    val searchOutlined = Icons.Outlined.Search
    val home = Icons.Rounded.Home
    val homeOutlined = Icons.Outlined.Home
    val person = Icons.Filled.Person
    val personOutlined = Icons.Outlined.Person
    val clear = Icons.Default.Clear

    val amanogawaLogo
        @Composable get() = painterResource(id = R.drawable.amanogawa_logo)

}