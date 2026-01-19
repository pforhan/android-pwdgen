package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.PasswordStorage
import alphainterplanetary.passwordgen.R
import alphainterplanetary.passwordgen.UiState
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays the password history list.
 *
 * @param context The [Context] for accessing resources.
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
fun HistoryList(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) {
  val items = stateFlow.collectAsState().value.recent

  Column(
    modifier = Modifier.wrapContentSize()
  ) {
    DividerWithText(
      modifier = Modifier.padding(8.dp),
      text = stringResource(R.string.recent)
    )
    items.forEach { pwd ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
          .clickable {
            copyToClipboard(
              context, pwd
            )
          },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = pwd,
          style = TextStyle(
            fontSize = 20.sp,
          ),
          modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        )
        IconButton(onClick = {
          passwordStorage.remove(pwd)
          stateFlow.value = stateFlow.value.copy(recent = passwordStorage.listPasswords())
          Toast
            .makeText(context, "Deleted from history.", Toast.LENGTH_SHORT)
            .show()
        }) {
          Icon(
            Icons.Default.Delete, contentDescription = stringResource(R.string.delete)
          )
        }
      }
      Divider()
    }
  }
}

/**
 * A composable function that displays a divider with text.
 *
 * @param text The text to display.
 * @param modifier The modifier for the row.
 */
@Composable
fun DividerWithText(
  text: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = text,
      modifier = Modifier.padding(end = 8.dp)
    )
    Divider(
      modifier = Modifier.weight(1f),
    )
  }
}
