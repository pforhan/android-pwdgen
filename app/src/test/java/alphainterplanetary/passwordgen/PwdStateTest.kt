package alphainterplanetary.passwordgen

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PwdStateTest {
  @Test
  fun testAvoidAmbiguous() {
    val state = PwdState(length = 10000, content = "", avoidAmbiguous = true)
    val newState = state.withNewPassword()
    
    val ambiguousChars = "Il1O0|"
    for (c in newState.content) {
      assertFalse("Password contains ambiguous character: $c", c in ambiguousChars)
    }
  }

  @Test
  fun testAllowAmbiguous() {
    // Generate many passwords to increase chance of getting an ambiguous character
    var foundAmbiguous = false
    val ambiguousChars = "Il1O0"
    
    for (i in 1..100) {
        val state = PwdState(length = 1000, content = "")
        val newState = state.withNewPassword(avoid = false)
        if (newState.content.any { it in ambiguousChars }) {
            foundAmbiguous = true
            break
        }
    }
    
    assertTrue("Should have found at least one ambiguous character in 100 100-char passwords", foundAmbiguous)
  }
}
