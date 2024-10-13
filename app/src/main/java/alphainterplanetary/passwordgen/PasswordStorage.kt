package alphainterplanetary.passwordgen

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Date
import java.util.PriorityQueue
import javax.crypto.KeyGenerator

class PasswordStorage {

  private val passwordQueue = PasswordQueue()
  var lastError: Exception? = null

  private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
    try {
      load(null)
      aliases().toList()
        .filter { it.isPasswordAlias() }
        .map { alias ->
          loadPasswordEntry(alias)
        }
        .forEach {
          passwordQueue.enqueue(it)
        }
    } catch (e: Exception) {
      lastError = e
      e.printStackTrace()
    }
  }

  // Method to create a key with the given alias
  fun savePassword(password: String): Boolean = try {
    val alias = password.toAlias()
    val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
    keyPairGenerator.initialize(
      KeyGenParameterSpec.Builder(alias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
        .build()
    )
    keyPairGenerator.generateKeyPair()
    val passwordEntry = keyStore.loadPasswordEntry(alias)
    val old = passwordQueue.enqueue(passwordEntry)
    if (old != null) {
      remove(old)
    }
    true
  } catch (e: Exception) {
    lastError = e
    e.printStackTrace()
    false
  }

  // Method to list all current aliases and their creation dates
  fun listPasswords(): List<PasswordEntry> = passwordQueue.toList()

  private fun KeyStore.loadPasswordEntry(alias: String): PasswordEntry {
    val creationDate = keyStore.dateOf(alias)
    Log.d("pwdgen-date", "date is $creationDate")
    return PasswordEntry(alias.toPassword(), creationDate)
  }

  private fun KeyStore.dateOf(alias: String): Date {
    val cert: Certificate = keyStore.getCertificate(alias)
    Log.d("pwdgen-date", "cert is $cert and is x509? ${cert is X509Certificate}")
    return if (cert is X509Certificate) cert.notBefore else Date()
  }

  // Method to delete a key with the given alias
  fun remove(passwordEntry: PasswordEntry): Boolean = try {
    passwordQueue.remove(passwordEntry)
    keyStore.deleteEntry(passwordEntry.password.toAlias())
    true
  } catch (e: Exception) {
    lastError = e
    e.printStackTrace()
    false
  }

  // Method to remove all keys from the Keystore
  fun removeAllEntries() = try {
    listPasswords().forEach(::remove)
  } catch (e: Exception) {
    lastError = e
    e.printStackTrace()
  }
}

data class PasswordEntry(
  val password: String,
  val creationDate: Date
) : Comparable<PasswordEntry> {
  override fun compareTo(other: PasswordEntry): Int = creationDate.compareTo(other.creationDate)
}

const val PASSWORD_KEYSTORE_PREFIX = " "
private fun String.isPasswordAlias(): Boolean = startsWith(PASSWORD_KEYSTORE_PREFIX)
private fun String.toAlias(): String = "$PASSWORD_KEYSTORE_PREFIX$this"
private fun String.toPassword(): String = substring(PASSWORD_KEYSTORE_PREFIX.length)

private class PasswordQueue(private val maxSize: Int = 3) : Iterable<PasswordEntry> {
  private val queue = PriorityQueue<PasswordEntry>()

  fun enqueue(passwordEntry: PasswordEntry): PasswordEntry? {
    // todo debug this because I think it's always deleting the newest entry instead of the oldest
    queue.add(passwordEntry)
    return if (queue.size > maxSize) {
      queue.poll() // Remove the eldest entry
    } else null
  }

  fun peek(): PasswordEntry? = queue.peek()

  fun clear() = queue.clear()

  override fun iterator(): Iterator<PasswordEntry> = queue.sortedBy { it.creationDate }.iterator()

  fun remove(passwordEntry: PasswordEntry) {
    queue.remove(passwordEntry)
  }
}
