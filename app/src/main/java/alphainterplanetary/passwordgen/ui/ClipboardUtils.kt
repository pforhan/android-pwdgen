package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.PasswordStorage
import alphainterplanetary.passwordgen.R
import alphainterplanetary.passwordgen.UiState
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Copies text to the clipboard.
 *
 * @param context The [Context] for accessing resources.
 * @param value The text to copy.
 * @param message The message to display after copying.
 */
fun copyToClipboard(
  context: Context,
  value: String,
  message: String = context.getString(R.string.copied_current),
) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  clipboard.setPrimaryClip(
    ClipData.newPlainText(context.getString(R.string.copied_password), value)
  )
  Toast
    .makeText(context, message, Toast.LENGTH_SHORT)
    .show()
}

/**
 * Generates a new password if the instructions are still displaying and copies it to the clipboard.
 *
 * @param context The [Context] for accessing resources.
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
fun maybeNewAndCopyText(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) {
  var current = stateFlow.value
  if (current.pwdState.content == context.getString(R.string.default_instruction)) {
    // Go ahead and generate first so the copy can proceed.
    val newPwdState = current.pwdState.withNewPassword()
    current = current.copy(
      pwdState = newPwdState
    )
  }
  copyToClipboard(context, current.pwdState.content)
  passwordStorage.addPassword(current.pwdState.content)
  stateFlow.value = current.copy(recent = passwordStorage.listPasswords())
}
