package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.PasswordStorage
import alphainterplanetary.passwordgen.UiState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays the generated password and allows the user to copy it
 * by tapping on the field.
 *
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
fun CopyOnClickText(
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) {
  Box(
    modifier = Modifier
      .padding(32.dp)
      .background(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
      ),
    contentAlignment = Alignment.Center
  ) {
    val context = LocalContext.current
    val textToUse = stateFlow.collectAsState().value

    Text(
      text = textToUse.pwdState.content,
      minLines = 3,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier
        .padding(LENGTH_DEFAULT.dp)
        .fillMaxWidth()
        .clickable {
          maybeNewAndCopyText(context, passwordStorage, stateFlow)
        },
      style = MaterialTheme.typography.headlineMedium.copy(lineBreak = LineBreak.Simple)
    )
  }
}
