package muddyhorse.passwordgen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import muddyhorse.passwordgen.ui.theme.PasswordGenTheme
import kotlin.random.Random

private val current = MutableStateFlow("")

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val instructions = getString(R.string.default_instruction)
    current.value = instructions
    setContent {
      PasswordGenTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = getString(R.string.header),
              modifier = Modifier.padding(16.dp),
              style = MaterialTheme.typography.headlineLarge)
            CopyOnClickText(current)
            ButtonRow(current)
          }
        }
      }
    }
  }
}

@Composable
private fun ButtonRow(content: MutableStateFlow<String>) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
  ) {
    val padding = Modifier
      .padding(
        PaddingValues(
          horizontal = 16.dp,
        )
      )
    Button(
      modifier = padding,
      onClick = {
        content.value = generateCharacters(16)
      }) {
      Text(text = stringResource(R.string.new_text))
    }
    val context = LocalContext.current
    val textToUse = content.collectAsState().value

    Button(
      modifier = padding,
      onClick = { copyText(context, textToUse) }) {
      Text(text = stringResource(R.string.copy))
    }

    Button(
      modifier = padding,
      onClick = {
        current.value = context.getString(R.string.default_instruction)
      }) {
      Text(text = stringResource(R.string.clear))
    }
  }
}

private fun generateCharacters(count: Int): String {
  val sb = StringBuilder()
  for (i in 0..count) {
    sb.append('*' + Random.nextInt(80))
  }
  return sb.toString()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CopyOnClickText(content: MutableStateFlow<String>) {
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
    val textToUse = content.collectAsState().value

    Text(
      text = textToUse,
      modifier = Modifier
        .padding(16.dp)
        .clickable {
          copyText(context, textToUse)
        },
      style = MaterialTheme.typography.headlineMedium
    )
  }
}

private fun copyText(context: Context, textToUse: String) {
  val toastMessage: String
  if (textToUse != context.getString(R.string.default_instruction)) {
    toastMessage = context.getString(R.string.copied_currrent)
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Copied Password", textToUse))
  } else {
    toastMessage = context.getString(R.string.no_password_created)
  }
  Toast
    .makeText(context, toastMessage, Toast.LENGTH_SHORT)
    .show()
}

@Preview(showBackground = true)
@Composable
fun TextAppPreview() {
  CopyOnClickText(current)
}