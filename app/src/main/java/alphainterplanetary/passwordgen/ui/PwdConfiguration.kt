package alphainterplanetary.passwordgen.ui
 
import alphainterplanetary.passwordgen.LENGTH_MAX
import alphainterplanetary.passwordgen.LENGTH_MIN
import alphainterplanetary.passwordgen.UiState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import alphainterplanetary.passwordgen.ui.theme.MintHighlight
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
  val pwdState = contentState.value.pwdState
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "Length: ${pwdState.length}",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Slider(
        value = pwdState.length.toFloat(),
        onValueChange = {
          val length = it.toInt()
          val current = stateFlow.value
          if (length != current.pwdState.length) {
            stateFlow.value = current.copy(pwdState = current.pwdState.withNewPassword(length))
          }
        },
        steps = LENGTH_MAX - LENGTH_MIN - 1,
        valueRange = LENGTH_MIN.toFloat()..LENGTH_MAX.toFloat(),
        colors = SliderDefaults.colors(
          thumbColor = MintHighlight,
          activeTrackColor = MintHighlight,
          inactiveTrackColor = MintHighlight.copy(alpha = 0.24f)
        )
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    AvoidAmbiguousToggle(stateFlow, pwdState.avoidAmbiguous)
  }
}

@Composable
private fun AvoidAmbiguousToggle(stateFlow: MutableStateFlow<UiState>, checked: Boolean) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(start = 16.dp)
      .clip(MaterialTheme.shapes.small)
      .toggleable(
        value = checked,
        onValueChange = { avoid ->
          val current = stateFlow.value
          stateFlow.value =
            current.copy(pwdState = current.pwdState.withNewPassword(current.pwdState.length, avoid))
        },
        role = Role.Switch
      )
      .padding(vertical = 4.dp, horizontal = 8.dp)
  ) {
    Text(
      text = stringResource(id = alphainterplanetary.passwordgen.R.string.avoid_ambiguous),
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.padding(end = 8.dp)
    )
    Switch(
      checked = checked,
      onCheckedChange = null, // Handled by toggleable row
      colors = SwitchDefaults.colors(
        checkedThumbColor = MintHighlight,
        checkedTrackColor = MintHighlight.copy(alpha = 0.5f),
        checkedBorderColor = MintHighlight,
        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
      )
    )
  }
}
