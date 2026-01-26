package alphainterplanetary.passwordgen.ui

import alphainterplanetary.passwordgen.PasswordStorage
import alphainterplanetary.passwordgen.PwdState
import alphainterplanetary.passwordgen.R
import alphainterplanetary.passwordgen.UiState
import alphainterplanetary.passwordgen.maybeNewAndCopyText
import alphainterplanetary.passwordgen.ui.theme.LocalMintHighlight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A composable function that displays the generated password in a card and provides
 * actions for copying, refreshing, and resetting.
 *
 * @param passwordStorage The [PasswordStorage] for managing stored passwords.
 * @param stateFlow The [MutableStateFlow] for managing the UI state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopyOnClickText(
  passwordStorage: PasswordStorage,
  stateFlow: MutableStateFlow<UiState>,
) {
  val context = LocalContext.current
  val uiState = stateFlow.collectAsState().value

  ElevatedCard(
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = uiState.pwdState.content,
        minLines = 2,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(8.dp)
          .fillMaxWidth()
          .clickable {
            maybeNewAndCopyText(context, passwordStorage, stateFlow)
          },
        style = MaterialTheme.typography.headlineMedium.copy(lineBreak = LineBreak.Simple)
      )

      val isInstruction = uiState.pwdState.content == stringResource(R.string.default_instruction)

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IndicatorPill(
            label = "a",
            isActive = !isInstruction && uiState.pwdState.lower > 0,
            tooltipText = stringResource(R.string.count_lower, uiState.pwdState.lower.toInt())
          )
          IndicatorPill(
            label = "A",
            isActive = !isInstruction && uiState.pwdState.upper > 0,
            tooltipText = stringResource(R.string.count_upper, uiState.pwdState.upper.toInt())
          )
          IndicatorPill(
            label = "2",
            isActive = !isInstruction && uiState.pwdState.number > 0,
            tooltipText = stringResource(R.string.count_number, uiState.pwdState.number.toInt())
          )
          IndicatorPill(
            label = "@",
            isActive = !isInstruction && uiState.pwdState.symbol > 0,
            tooltipText = stringResource(R.string.count_symbol, uiState.pwdState.symbol.toInt())
          )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        TooltipBox(
          positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
          tooltip = { PlainTooltip { Text(stringResource(R.string.generate_new)) } },
          state = rememberTooltipState()
        ) {
          IconButton(onClick = {
            stateFlow.value = stateFlow.value.copy(
              pwdState = stateFlow.value.pwdState.withNewPassword()
            )
          }) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = stringResource(R.string.generate_new)
            )
          }
        }

        TooltipBox(
          positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
          tooltip = { PlainTooltip { Text(stringResource(R.string.copy)) } },
          state = rememberTooltipState()
        ) {
          IconButton(onClick = {
            maybeNewAndCopyText(context, passwordStorage, stateFlow)
          }) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = stringResource(R.string.copy)
            )
          }
        }

        if (!isInstruction) {
          TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text(stringResource(R.string.clear)) } },
            state = rememberTooltipState()
          ) {
            IconButton(onClick = {
              val len = uiState.pwdState.length
              stateFlow.value = uiState.copy(
                pwdState = PwdState(
                  length = len,
                  content = context.getString(R.string.default_instruction)
                )
              )
            }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.clear)
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IndicatorPill(
  label: String,
  isActive: Boolean,
  tooltipText: String,
  modifier: Modifier = Modifier
) {
  TooltipBox(
    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
    tooltip = { PlainTooltip { Text(tooltipText) } },
    state = rememberTooltipState()
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(
          if (isActive) LocalMintHighlight.current else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.1f
          )
        )
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (isActive) Color.Black else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
      )
    }
  }
}
