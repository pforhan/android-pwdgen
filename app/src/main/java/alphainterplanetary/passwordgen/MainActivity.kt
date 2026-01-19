package alphainterplanetary.passwordgen

import alphainterplanetary.passwordgen.ui.ButtonColumn
import alphainterplanetary.passwordgen.ui.ButtonRow
import alphainterplanetary.passwordgen.ui.CopyOnClickText
import alphainterplanetary.passwordgen.ui.HistoryList
import alphainterplanetary.passwordgen.ui.LengthSlider
import alphainterplanetary.passwordgen.ui.Links
import alphainterplanetary.passwordgen.ui.PasswordStatistics
import alphainterplanetary.passwordgen.ui.theme.PasswordGenTheme
import android.content.Context
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

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
internal fun PwdColumn(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) {
  val configuration = LocalConfiguration.current
  if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
    PwdLandscape(context, passwordStorage, stateFlow)
  } else {
    PwdPortrait(context, passwordStorage, stateFlow)
  }
}

/**
 * A composable function that displays the portrait content of the password generator.
 */
@Composable
internal fun PwdPortrait(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
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
 * A composable function that displays the landscape content of the password generator.
 */
@Composable
internal fun PwdLandscape(
  context: Context,
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) = Row(
  modifier = Modifier
    .fillMaxSize()
    .padding(16.dp),
  verticalAlignment = Alignment.Top
) {
  // Column 1: Password, Slider, Statistics
  Column(
    modifier = Modifier.weight(1f),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    CopyOnClickText(passwordStorage, stateFlow)
    LengthSlider(stateFlow)
    PasswordStatistics(context, stateFlow)
  }

  // Column 2: Buttons
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    ButtonColumn(passwordStorage, stateFlow)
  }

  // Column 3: History & Links
  Column(
    modifier = Modifier.weight(1f),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    HistoryList(context, passwordStorage, stateFlow)
    Links(context)
  }
}
