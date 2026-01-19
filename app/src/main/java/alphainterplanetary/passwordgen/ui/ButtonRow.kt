package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.PasswordStorage
import alphainterplanetary.passwordgen.PwdState
import alphainterplanetary.passwordgen.R
import alphainterplanetary.passwordgen.UiState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays the buttons for generating a new password, copying, and resetting.
 *
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
fun ButtonRow(
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(LENGTH_DEFAULT.dp, top = 32.dp)
  ) {
    val padding = Modifier
      .padding(
        PaddingValues(
          horizontal = LENGTH_DEFAULT.dp,
        )
      )
    Button(
      modifier = padding,
      onClick = {
        stateFlow.value = stateFlow.value.copy(
          pwdState = stateFlow.value.pwdState.withNewPassword()
        )
      }) {
      Text(text = stringResource(R.string.new_text))
    }
    val context = LocalContext.current

    Button(
      modifier = padding,
      onClick = { maybeNewAndCopyText(context, passwordStorage, stateFlow) }) {
      Text(text = stringResource(R.string.copy))
    }

    Button(
      modifier = padding,
      onClick = {
        val len = stateFlow.value.pwdState.length
        stateFlow.value = stateFlow.value.copy(
          pwdState = PwdState(
            length = len,
            content = context.getString(R.string.default_instruction)
          )
        )
      }) {
      Text(text = stringResource(R.string.reset))
    }
  }
}
