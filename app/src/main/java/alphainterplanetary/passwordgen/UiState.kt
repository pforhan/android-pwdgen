package alphainterplanetary.passwordgen

import kotlin.random.Random

/**
 * Represents the UI state of the password generator.
 *
 * @property pwdState The state of the generated password.
 * @property recent A list of recently generated passwords.
 */
data class UiState(
  val pwdState: PwdState,
  val recent: List<String>,
)

/**
 * Represents the state of a generated password.
 *
 * @property length The length of the password.
 * @property content The password string.
 * @property lower The number of lowercase letters in the password.
 * @property upper The number of uppercase letters in the password.
 * @property number The number of digits in the password.
 * @property symbol The number of symbols in the password.
 */
data class PwdState(
  val length: Int,
  val content: String,
  val lower: Short = 0,
  val upper: Short = 0,
  val number: Short = 0,
  val symbol: Short = 0,
  val avoidAmbiguous: Boolean = true,
) {

  /**
   * Generates a new password with the specified length and ambiguity setting.
   *
   * @param newLength The length of the new password.
   * @param avoid The ambiguity setting.
   * @return A new [PwdState] with a new password.
   */
  fun withNewPassword(
    newLength: Int = length,
    avoid: Boolean = avoidAmbiguous
  ): PwdState {
    val stats = Stats()
    val pwd = randomCharacters(newLength, avoid, stats)
    return PwdState(
      length = newLength,
      content = pwd,
      lower = stats.lower,
      upper = stats.upper,
      number = stats.number,
      symbol = stats.symbol,
      avoidAmbiguous = avoid,
    )
  }

  /**
   * Generates a string of random characters.
   *
   * @param count The number of characters to generate.
   * @param avoid Whether to avoid ambiguous characters.
   * @param stats The [Stats] object to track character counts.
   * @return A string of random characters.
   */
  private fun randomCharacters(count: Int, avoid: Boolean, stats: Stats): String {
    val sb = StringBuilder()
    // Make a new random for every invocation... that way we're seeded by real-world randomness.
    val random = Random(System.currentTimeMillis())
    while (sb.length < count) {
      val c = '!' + random.nextInt(94)
      if (avoid && c.isAmbiguous()) continue
      sb.append(c)
      stats.tally(c)
    }
    return sb.toString()
  }

  /**
   * A private class to track password statistics.
   */
  private class Stats {
    var lower: Short = 0
    var upper: Short = 0
    var number: Short = 0
    var symbol: Short = 0

    /**
     * Tallies the character.
     *
     * @param c The character to tally.
     */
    fun tally(c: Char) {
      when {
        c.isDigit() -> number++
        c.isLetter() -> {
          if (c.isUpperCase()) {
            upper++
          } else {
            lower++
          }
        }

        else -> symbol++
      }
    }
  }
}

/**
 * Checks if a character is ambiguous.
 *
 * @param c The character to check.
 * @return True if the character is ambiguous, false otherwise.
 */
private fun Char.isAmbiguous(): Boolean = this in "Il1|O0"
