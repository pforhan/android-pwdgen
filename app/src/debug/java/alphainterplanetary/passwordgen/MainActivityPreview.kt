package alphainterplanetary.passwordgen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A preview function for the [PwdColumn] composable.
 */
@Preview(showBackground = true)
@Composable
fun PwdPreview() {
  PwdColumn(
    LocalContext.current,
    PasswordStorage(),
    MutableStateFlow(
      UiState(
        PwdState(8, "F@k3Pa5%"),
        listOf("Password1", "password2", "aPassword3")
      )
    )
  )
}
