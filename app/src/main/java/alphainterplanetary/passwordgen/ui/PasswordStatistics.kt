package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.R
import alphainterplanetary.passwordgen.UiState
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays password statistics.
 *
 * @param context The [Context] for accessing resources.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
fun PasswordStatistics(
  context: Context,
  stateFlow: MutableStateFlow<UiState>,
) {
  Row(horizontalArrangement = Arrangement.SpaceBetween) {
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.padding(end = 4.dp)
    ) {
      Text(
        text = context.getString(R.string.stats_total),
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = context.getString(R.string.stats_lower),
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = context.getString(R.string.stats_upper),
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = context.getString(R.string.stats_number),
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = context.getString(R.string.stats_symbol),
        style = MaterialTheme.typography.bodyMedium
      )
    }
    Column {
      val stats = stateFlow.collectAsState().value.pwdState
      Text(
        text = stats.length.toString(),
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = stats.lower.toString(),
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = stats.upper.toString(),
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = stats.number.toString(),
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        text = stats.symbol.toString(),
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}
