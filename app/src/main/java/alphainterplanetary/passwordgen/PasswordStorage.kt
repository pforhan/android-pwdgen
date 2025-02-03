package alphainterplanetary.passwordgen

import android.security.keystore.KeyGenParameterSpec.Builder
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import javax.crypto.KeyGenerator

class PasswordStorage {

  private val passwordQueue = PasswordQueue()
  var lastError: Exception? = null

  private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
    try {
      load(null)
      aliases().toList()
        .filter { it.startsWith(PASSWORD_ENTRY_PREFIX) }
        // Order by index -- index comes after the prefix so just string ordering is fine.
        .sortedBy { it }
        // Strip out the prefix and the index.
        .map { it.substring(PASSWORD_ENTRY_PREFIX.length + 1) }
        // Save to memory:
        .forEach {
          passwordQueue.enqueue(it)
        }
    } catch (e: Exception) {
      lastError = e
      e.printStackTrace()
    }
  }

  private fun saveAll() {
    removePasswordsFromKeystore()
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
    passwordQueue.list().forEachIndexed { index, password ->
      keyGenerator.save(password.toAlias(index))
    }
  }

  private fun KeyGenerator.save(alias: String) {
    init(
      Builder(
        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
      )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build()
    )
    generateKey()
  }

  /**
   * Add a password to the store, ejecting the eldest if there are more than the maximum.
   * @return true if the save was successful, false if this password is the same as the last
   * or if there was an error saving.
   */
  fun addPassword(password: String): Boolean {
    try {
      if (passwordQueue.peekLast() == password) {
        return false
      }
      passwordQueue.enqueue(password)
      saveAll()
      return true
    } catch (e: Exception) {
      lastError = e
      e.printStackTrace()
      return false
    }
  }

  /** List all passwords. These are ordered from most recent to least recent. */
  fun listPasswords(): List<String> {
    Log.d("aps", " Passwds: ${passwordQueue.list().reversed()}")

    return passwordQueue.list().reversed()
  }

  /**
   * Remove a password from the store.
   * @return true if the save was successful, false otherwise.
   */
  fun remove(password: String): Boolean = try {
    passwordQueue.remove(password)
    saveAll()
    true
  } catch (e: Exception) {
    lastError = e
    e.printStackTrace()
    false
  }

  // Method to remove all keys from the Keystore
  fun removeAllEntries() = try {
    passwordQueue.clear()
    removePasswordsFromKeystore()
  } catch (e: Exception) {
    lastError = e
    e.printStackTrace()
  }

  private fun removePasswordsFromKeystore() {
    keyStore.aliases().toList()
      .filter { it.startsWith(PASSWORD_ENTRY_PREFIX) }
      .forEach { keyStore.deleteEntry(it) }
  }
}

const val ANDROID_KEY_STORE = "AndroidKeyStore"
const val PASSWORD_ENTRY_PREFIX = "pwd"
private fun String.toAlias(index: Int) = "$PASSWORD_ENTRY_PREFIX$index$this"

private class PasswordQueue(private val maxSize: Int = 3) {
  private val queue = mutableListOf<String>()

  fun enqueue(newEntry: String): String? {
    queue.add(newEntry)
    return if (queue.size > maxSize) {
      queue.removeAt(0) // Remove the eldest entry
    } else null
  }

  fun peekLast(): String? = queue.lastOrNull()

  fun clear() = queue.clear()

  fun list(): List<String> = queue.toList()

  fun remove(password: String) {
    queue.remove(password)
  }
}
