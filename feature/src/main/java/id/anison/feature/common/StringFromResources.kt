package id.anison.feature.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import id.anison.feature.R

fun getStringFromResources(context: Context, resId: Int): String =
    context.resources.getString(resId)


sealed class UiText {

    data class StringValue(val str: String) : UiText()

    class StringResource(
        @StringRes val resourceId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is StringValue -> {
                str
            }

            is StringResource -> {
                stringResource(id = resourceId, formatArgs = args)
            }
        }
    }
}