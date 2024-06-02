package alphainterplanetary.passwordgen

import kotlin.random.Random

data class PwdState(
  val length: Int,
  val content: String,
) {
  var lower: Short = 0
    private set
  var upper: Short = 0
    private set
  var number: Short = 0
    private set
  var symbol: Short = 0
    private set

  fun withNewPassword() = copy(content = randomCharacters(length))
  fun withNewPassword(newLength: Int) =
    PwdState(length = newLength, content = randomCharacters(newLength))

  private fun randomCharacters(count: Int): String {
    clearStats();
    val sb = StringBuilder()
    // Make a new random for every invocation... that way we're seeded by real-world randomness.
    val random = Random(System.currentTimeMillis())
    for (i in 0..<count) {
      val c = '!' + random.nextInt(94)
      sb.append(c)
      tally(c)
    }
    return sb.toString()
  }

  private fun tally(c: Char) {
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

  private fun clearStats() {
    lower = 0
    upper = 0
    number = 0
    symbol = 0
  }
}