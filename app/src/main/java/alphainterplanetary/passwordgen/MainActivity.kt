package alphainterplanetary.passwordgen

import alphainterplanetary.passwordgen.ui.theme.PasswordGenTheme
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow

private const val LENGTH_DEFAULT = 16
private const val LENGTH_MIN = 8
private const val LENGTH_MAX = 30

/**
 * The main activity for the Password Generator app.
 */
class MainActivity : ComponentActivity() {
  /**
   * A [MutableStateFlow] that holds the current [UiState] of the app.
   */
  private val current = MutableStateFlow(
    UiState(
      pwdState = PwdState(content = "", length = LENGTH_DEFAULT),
      recent = emptyList()
    )
  )
  /**
   * An instance of [PasswordStorage] for securely storing generated passwords.
   */
  private val passwordStorage = PasswordStorage()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Prevent screenshots
    window.addFlags(LayoutParams.FLAG_SECURE)
    val instructions = getString(R.string.default_instruction)
    current.value = UiState(
      pwdState = current.value.pwdState.copy(content = instructions),
      recent = passwordStorage.listPasswords()
    )

    setContent {
      PasswordGenTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          color = MaterialTheme.colorScheme.background
        ) {
          PwdColumn(this, passwordStorage, current)
        }
      }
    }
  }
}

/**
 * A composable function that displays the main content of the password generator.
 *
 * @param context The [Context] for accessing resources.
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
private fun PwdColumn(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>
) = Column(
  modifier = Modifier.fillMaxSize(),
  horizontalAlignment = Alignment.CenterHorizontally
) {
  Text(
    text = context.getString(R.string.header),
    modifier = Modifier.padding(LENGTH_DEFAULT.dp),
    style = MaterialTheme.typography.headlineLarge
  )
  CopyOnClickText(passwordStorage, stateFlow)
  LengthSlider(stateFlow)
  PasswordStatistics(context, stateFlow)
  ButtonRow(passwordStorage, stateFlow)
  HistoryList(context, passwordStorage, stateFlow)
  Links(context)
}

/**
 * A composable function that displays a slider for selecting the password length.
 *
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
private fun LengthSlider(stateFlow: MutableStateFlow<UiState>) {
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

/**
 * A composable function that displays password statistics.
 *
 * @param context The [Context] for accessing resources.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable fun PasswordStatistics(
  context: Context,
  stateFlow: MutableStateFlow<UiState>
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

/**
 * A composable function that displays the buttons for generating a new password, copying, and resetting.
 *
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@Composable
private fun ButtonRow(
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>
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

/**
 * A composable function that displays links.
 *
 * @param context The [Context] for accessing resources.
 */
@Composable
fun Links(context: Context) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 20.dp)
  ) {
    TextWithLink(
      context = context,
      prefix = "Source available on ",
      link = "GitHub",
      url = "https://github.com/pforhan/android-pwdgen"
    )
    TextWithLink(
      context = context,
      prefix = "Provided by ",
      link = "Alpha Interplanetary, LLC",
      url = "https://alphainterplanetary.com",
      style = MaterialTheme.typography.bodySmall,
    )
  }
}

/**
 * A composable function that displays text with a link.
 *
 * @param context The [Context] for accessing resources.
 * @param prefix The text to display before the link.
 * @param link The text to display as a link.
 * @param url The URL to launch when the link is clicked.
 * @param style The style of the text.
 */
@Composable
private fun ColumnScope.TextWithLink(
  context: Context,
  prefix: String,
  link: String,
  url: String,
  style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
  Text(
    style = style,
    text = buildAnnotatedString {
      append(prefix)
      withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
        append(link)
      }
      append(".")
    },
    modifier = Companion
      .align(Alignment.CenterHorizontally)
      .clickable(onClick = { context.launchUrl(url) })
  )
}

/**
 * Launches a URL in a browser.
 */
private fun Context.launchUrl(
  url: String
) {
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
  startActivity(intent)
}

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
  stateFlow: MutableStateFlow<UiState>
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

/**
 * Generates a new password if the instructions are still displaying and copies it to the clipboard.
 *
 * @param context The [Context] for accessing resources.
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
private fun maybeNewAndCopyText(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>
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

/**
 * Copies text to the clipboard.
 *
 * @param context The [Context] for accessing resources.
 * @param value The text to copy.
 * @param message The message to display after copying.
 */
private fun copyToClipboard(
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
