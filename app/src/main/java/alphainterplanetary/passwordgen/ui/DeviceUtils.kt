package alphainterplanetary.passwordgen.ui

import android.os.Build

/**
 * Utility class to detect specific hardware characteristics.
 */
object DeviceUtils {
  private val EPAPER_MODELS = listOf("MP01")

  /**
   * Checks if the device is likely an e-paper/e-ink device.
   * Currently focuses on detecting the Minimal Phone.
   */
  fun isEPaperDevice(): Boolean {
    val model = Build.MODEL ?: ""
    return EPAPER_MODELS.any { model.contains(it, ignoreCase = true) }
  }
}
