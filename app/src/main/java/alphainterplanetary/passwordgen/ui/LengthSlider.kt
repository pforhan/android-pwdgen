package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.UiState
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays a slider for selecting the password length.
 *
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
fun LengthSlider(stateFlow: MutableStateFlow<UiState>) {
  val contentState = stateFlow.collectAsState()
  Slider(
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
}
