package id.anison.amanogawa.core.ui.common

import androidx.compose.ui.unit.dp

object AmanogawaConst {

    val barHeight = 64.dp

    val bottomBarPaddings = 16.dp

    val bottomBarInsets = barHeight + bottomBarPaddings * 2

    val maxContentWidth = 1200.dp

    fun minCardWidth(isLandscape: Boolean): Int = 160//if (isLandscape) 200 else 180

}