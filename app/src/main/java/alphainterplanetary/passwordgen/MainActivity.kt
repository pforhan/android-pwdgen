package alphainterplanetary.passwordgen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import kotlinx.coroutines.flow.map
import alphainterplanetary.passwordgen.ui.theme.PasswordGenTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
  private val current = MutableStateFlow("")

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    window.addFlags(LayoutParams.FLAG_SECURE)
    val instructions = getString(R.string.default_instruction)
    current.value = instructions

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

class PwdStats {
  var lower: Short = 0
  var upper: Short = 0
  var number: Short = 0
  var symbol: Short = 0
}

@Composable
private fun PwdColumn(context: Context, content: MutableStateFlow<String>) =
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = context.getString(R.string.header),
      modifier = Modifier.padding(16.dp),
      style = MaterialTheme.typography.headlineLarge
    )
    CopyOnClickText(content)
    PasswordStatistics(context, content)
    ButtonRow(content)

  }

@Composable fun PasswordStatistics(context: Context, content: MutableStateFlow<String>) {
  Row(horizontalArrangement = Arrangement.SpaceBetween) {
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.padding(end = 4.dp)
    ) {
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
      val stats = content.map {
        if (it == context.getString(R.string.default_instruction)) {
          PwdStats()
        } else {
          stats(it)
        }
      }.collectAsState(initial = PwdStats()).value
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
private fun ButtonRow(content: MutableStateFlow<String>) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp, top = 32.dp)
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
        val pwd = generateCharacters(16)
        content.value = pwd
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
        content.value = context.getString(R.string.default_instruction)
      }) {
      Text(text = stringResource(R.string.clear))
    }
  }
}

fun generateCharacters(count: Int): String {
  val sb = StringBuilder()
  for (i in 0..count) {
    sb.append('!' + Random.nextInt(94))
  }
  return sb.toString()
}

fun stats(pwd: String) : PwdStats {
  val pwdStats = PwdStats()

  for (c in pwd) {
    when {
      c.isDigit() -> pwdStats.number++
      c.isLetter() -> {
        if (c.isUpperCase()) {
          pwdStats.upper++
        } else {
          pwdStats.lower++
        }
      }
      else -> pwdStats.symbol++
    }
  }
  return pwdStats
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
fun PwdPreview() {
  PwdColumn(LocalContext.current, MutableStateFlow("F@k3Pa5%"))
}