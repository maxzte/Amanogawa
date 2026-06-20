package id.anison.amanogawa.core.ui.common

import androidx.compose.runtime.Composable
import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.ErrorMessage


@Composable
fun <T> DataStateContent(
    state: DataState<T>,
    content: @Composable (T) -> Unit,
    loadingContent: @Composable (() -> Unit)? = null,
    errorContent: @Composable (() -> Unit)? = null,
) {
    when(state) {
        is DataState.Error -> {
            errorContent?.invoke() ?: ErrorMessage(onUpdate = {})
        }

        is DataState.Loading -> {
                loadingContent?.invoke() ?: AmanogawaSpinner()
        }

        is DataState.Success -> {
            content(state.data)
        }
    }
}