package alphainterplanetary.passwordgen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * A [ViewModel] that holds the current [UiState] of the app.
 * This persists across configuration changes like screen rotation.
 */
class MainViewModel : ViewModel() {
  /**
   * A [MutableStateFlow] that holds the current [UiState] of the app.
   */
  val current = MutableStateFlow(
    UiState(
      pwdState = PwdState(content = "", length = LENGTH_DEFAULT),
      recent = emptyList()
    )
  )

  /**
   * Sets the initial state if it hasn't been set yet.
   */
  fun setInitialState(instructions: String, recentPasswords: List<String>) {
    if (current.value.pwdState.content.isEmpty() || current.value.pwdState.content == instructions) {
      current.update {
        it.copy(
          pwdState = it.pwdState.copy(content = instructions),
          recent = recentPasswords
        )
      }
    }
  }
}
