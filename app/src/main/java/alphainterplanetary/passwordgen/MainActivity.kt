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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

private const val LENGTH_DEFAULT = 16
private const val LENGTH_MIN = 8
private const val LENGTH_MAX = 30

class MainActivity : ComponentActivity() {
  private val current = MutableStateFlow(PwdState(content = "", length = LENGTH_DEFAULT))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.addFlags(LayoutParams.FLAG_SECURE)
    val instructions = getString(R.string.default_instruction)
    current.value = current.value.copy(content = instructions)

    setContent {
      PasswordGenTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          PwdColumn(this, current)
        }
      }
    }
  }
}

data class PwdState(
  val length: Int,
  val content: String,
) {
  fun withNewPassword() = copy(content = generateCharacters(length))
  fun withNewPassword(newLength: Int) =
    PwdState(length = newLength, content = generateCharacters(newLength))

  private fun generateCharacters(count: Int): String {
    val sb = StringBuilder()
    // Make a new random for every invocation... that way we're seeded by real-world random input.
    val random = Random(System.currentTimeMillis())
    for (i in 0..<count) {
      sb.append('!' + random.nextInt(94))
    }
    return sb.toString()
  }
}

class CharacterBreakdown(
  val total: Int
) {
  var lower: Short = 0
  var upper: Short = 0
  var number: Short = 0
  var symbol: Short = 0
}

@Composable
private fun PwdColumn(context: Context, stateFlow: MutableStateFlow<PwdState>) =
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = context.getString(R.string.header),
      modifier = Modifier.padding(LENGTH_DEFAULT.dp),
      style = MaterialTheme.typography.headlineLarge
    )
    CopyOnClickText(stateFlow)
    LengthSlider(stateFlow)
    PasswordStatistics(context, stateFlow)
    ButtonRow(stateFlow)
    Links(context)
  }

@Composable
private fun LengthSlider(stateFlow: MutableStateFlow<PwdState>) {
  val contentState = stateFlow.collectAsState()
  Slider(
    value = contentState.value.length.toFloat(),
    onValueChange = {
      val length = it.toInt()
      val current = stateFlow.value
      if (length != current.length) stateFlow.value = current.withNewPassword(length)
    },
    steps = LENGTH_MAX - LENGTH_MIN + 1,
    valueRange = LENGTH_MIN.toFloat()..LENGTH_MAX.toFloat()
  )
}

@Composable fun PasswordStatistics(context: Context, stateFlow: MutableStateFlow<PwdState>) {
  Row(horizontalArrangement = Arrangement.SpaceBetween) {
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.padding(end = 4.dp)
    ) {
      Text(
        text = context.getString(R.string.stats_total),
        style = MaterialTheme.typography.bodyMedium
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
      val stats = stateFlow.map {
        if (it.content == context.getString(R.string.default_instruction)) {
          CharacterBreakdown(it.length)
        } else {
          stats(it.content)
        }
      }.collectAsState(initial = CharacterBreakdown(0)).value
      Text(
        text = stats.total.toString(),
        style = MaterialTheme.typography.bodyMedium
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

@Composable
private fun ButtonRow(stateFlow: MutableStateFlow<PwdState>) {
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
        stateFlow.value = stateFlow.value.withNewPassword()
      }) {
      Text(text = stringResource(R.string.new_text))
    }
    val context = LocalContext.current

    Button(
      modifier = padding,
      onClick = { maybeNewAndCopyText(context, stateFlow) }) {
      Text(text = stringResource(R.string.copy))
    }

    Button(
      modifier = padding,
      onClick = {
        stateFlow.value =
          stateFlow.value.copy(content = context.getString(R.string.default_instruction))
      }) {
      Text(text = stringResource(R.string.clear))
    }
  }
}

@Composable
fun Links(context: Context) {
  Column(modifier = Modifier.fillMaxWidth()) {
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
      style = MaterialTheme.typography.bodySmall
    )
  }
}

@Composable
private fun ColumnScope.TextWithLink(
  context: Context,
  prefix: String,
  link: String,
  suffix: String = ".",
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
      append(suffix)
    },
    modifier = Companion
      .align(Alignment.CenterHorizontally)
      .padding(LENGTH_DEFAULT.dp)
      .clickable(onClick = { launchUrl(context, url) })
  )
}

private fun launchUrl(context: Context, url: String) {
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
  context.startActivity(intent)
}

fun stats(pwd: String): CharacterBreakdown {
  val statistics = CharacterBreakdown(pwd.length)

  for (c in pwd) {
    when {
      c.isDigit() -> statistics.number++
      c.isLetter() -> {
        if (c.isUpperCase()) {
          statistics.upper++
        } else {
          statistics.lower++
        }
      }

      else -> statistics.symbol++
    }
  }
  return statistics
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CopyOnClickText(stateFlow: MutableStateFlow<PwdState>) {
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
      text = textToUse.content,
      minLines = 3,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier
        .padding(LENGTH_DEFAULT.dp)
        .fillMaxWidth()
        .clickable {
          maybeNewAndCopyText(context, stateFlow)
        },
      style = MaterialTheme.typography.headlineMedium.copy(lineBreak = LineBreak.Simple)
    )
  }
}

private fun maybeNewAndCopyText(context: Context, stateFlow: MutableStateFlow<PwdState>) {
  var current = stateFlow.value
  if (current.content == context.getString(R.string.default_instruction)) {
    // Go ahead and generate first so the copy can proceed.
    stateFlow.value = current.withNewPassword()
    current = stateFlow.value
  }
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  clipboard.setPrimaryClip(
    ClipData.newPlainText(
      context.getString(R.string.copied_password),
      current.content
    ))
  val toastMessage = context.getString(R.string.copied_current)
  Toast
    .makeText(context, toastMessage, Toast.LENGTH_SHORT)
    .show()
}

@Preview(showBackground = true)
@Composable
fun PwdPreview() {
  PwdColumn(LocalContext.current, MutableStateFlow(PwdState(8, "F@k3Pa5%")))
}