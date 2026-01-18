package alphainterplanetary.passwordgen

import android.security.keystore.KeyGenParameterSpec.Builder
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator

/** The Android KeyStore type. */
const val ANDROID_KEY_STORE = "AndroidKeyStore"
/** The prefix for password entries in the KeyStore. */
const val PASSWORD_ENTRY_PREFIX = "pwd"

/**
 * A class for securely storing and retrieving passwords.
 */
class PasswordStorage {

  private val passwordQueue = PasswordQueue()
  /** The last error encountered during a storage operation, if any. */
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
      keyGenerator.save("$PASSWORD_ENTRY_PREFIX$index$password")
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
   * @param password The password to add.
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
  fun listPasswords(): List<String> = passwordQueue.list().reversed()

  /**
   * Remove a password from the store.
   * @param password The password to remove.
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

  private fun removePasswordsFromKeystore() {
    keyStore.aliases().toList()
      .filter { it.startsWith(PASSWORD_ENTRY_PREFIX) }
      .forEach { keyStore.deleteEntry(it) }
  }
}

/**
 * A queue for storing passwords.
 *
 * @property maxSize The maximum size of the queue.
 */
private class PasswordQueue(private val maxSize: Int = 3) {
  private val queue = mutableListOf<String>()

  /**
   * Adds a new entry to the queue.
   *
   * @param newEntry The new entry to add.
   * @return The eldest entry if the queue is full, null otherwise.
   */
  fun enqueue(newEntry: String): String? {
    queue.add(newEntry)
    return if (queue.size > maxSize) {
      queue.removeAt(0) // Remove the eldest entry
    } else null
  }

  /**
   * Returns the last entry in the queue without removing it.
   *
   * @return The last entry in the queue, or null if the queue is empty.
   */
  fun peekLast(): String? = queue.lastOrNull()

  /**
   * Returns a list of all entries in the queue.
   *
   * @return A list of all entries in the queue.
   */
  fun list(): List<String> = queue.toList()

  /**
   * Removes a password from the queue.
   *
   * @param password The password to remove.
   */
  fun remove(password: String) {
    queue.remove(password)
  }
}
