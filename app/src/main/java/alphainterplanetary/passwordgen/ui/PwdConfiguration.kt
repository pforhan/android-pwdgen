package alphainterplanetary.passwordgen.ui
 
import alphainterplanetary.passwordgen.LENGTH_MAX
import alphainterplanetary.passwordgen.LENGTH_MIN
import alphainterplanetary.passwordgen.UiState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays a slider for selecting the password length and a checkbox
 * for avoiding ambiguous characters.
 *
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
fun PwdConfiguration(stateFlow: MutableStateFlow<UiState>) {
  val contentState = stateFlow.collectAsState()
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Slider(
      modifier = Modifier.weight(1f),
      value = contentState.value.pwdState.length.toFloat(),
      onValueChange = {
        val length = it.toInt()
        val current = stateFlow.value
        if (length != current.pwdState.length) {
          stateFlow.value = current.copy(pwdState = current.pwdState.withNewPassword(length))
        }
      },
      steps = LENGTH_MAX - LENGTH_MIN + 1,
      valueRange = LENGTH_MIN.toFloat()..LENGTH_MAX.toFloat()
    )
    AvoidAmbiguousCheckbox(stateFlow, contentState.value.pwdState.avoidAmbiguous)
  }
}

@Composable
private fun AvoidAmbiguousCheckbox(stateFlow: MutableStateFlow<UiState>, checked: Boolean) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Checkbox(
      checked = checked,
      onCheckedChange = { avoid ->
        val current = stateFlow.value
        stateFlow.value =
          current.copy(pwdState = current.pwdState.withNewPassword(current.pwdState.length, avoid))
      }
    )
    Text(
      text = stringResource(id = alphainterplanetary.passwordgen.R.string.avoid_ambiguous),
      style = MaterialTheme.typography.bodyMedium
    )
  }
}
