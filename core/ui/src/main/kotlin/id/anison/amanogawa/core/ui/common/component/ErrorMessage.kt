package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.R


@Composable
fun ErrorMessage(
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_rounded_language_24),
                contentDescription = "No internet connection icon",
                modifier = modifier.size(50.dp)
            )
            Text(text = stringResource(R.string.something_wrong))
            Text(
                text = stringResource(R.string.check_your_internet_connection_and_try_again),
                textAlign = TextAlign.Center,
            )
            OutlinedButton(onClick = { onUpdate() }) {
                Text(text = "Refresh")
            }
        }
    }
}