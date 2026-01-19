package alphainterplanetary.passwordgen.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

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
    modifier = Modifier
      .align(Alignment.CenterHorizontally)
      .clickable(onClick = { context.launchUrl(url) })
  )
}

/**
 * Launches a URL in a browser.
 */
fun Context.launchUrl(
  url: String,
) {
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
  startActivity(intent)
}
