package alphainterplanetary.passwordgen

import kotlin.random.Random

data class PwdState(
  val length: Int,
  val content: String,
  val lower: Short = 0,
  val upper: Short = 0,
  val number: Short = 0,
  val symbol: Short = 0,
) {
  fun withNewPassword(): PwdState = withNewPassword(length)

  fun withNewPassword(newLength: Int): PwdState {
    val stats = Stats()
    val pwd = randomCharacters(newLength, stats)
    return PwdState(
      length = newLength,
      content = pwd,
      lower = stats.lower,
      upper = stats.upper,
      number = stats.number,
      symbol = stats.symbol
    )
  }

  private fun randomCharacters(count: Int, stats: Stats): String {
    val sb = StringBuilder()
    // Make a new random for every invocation... that way we're seeded by real-world randomness.
    val random = Random(System.currentTimeMillis())
    for (i in 0..<count) {
      val c = '!' + random.nextInt(94)
      sb.append(c)
      stats.tally(c)
    }
    return sb.toString()
  }

  private class Stats {
    var lower: Short = 0
    var upper: Short = 0
    var number: Short = 0
    var symbol: Short = 0

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
