package org.feup.cmov.acmeclient.utils

import android.security.KeyPairGeneratorSpec
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Buffer
import org.feup.cmov.acmeclient.MainApplication
import org.mindrot.jbcrypt.BCrypt
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.util.*
import javax.security.auth.x500.X500Principal

class Crypto {
    companion object {
        private const val KEY_STORE = "AndroidKeyStore"

        @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
        suspend fun generateRSAKeyPair(alias: String): String {
            return withContext(Dispatchers.IO) {
                val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", KEY_STORE)

                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 1)

                val spec = KeyPairGeneratorSpec
                    .Builder(MainApplication.context)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=ACME, O=ACME Inc., C=PT"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.time).setEndDate(end.time)
                    .build()

                kpg.initialize(spec)
                val keyPair = kpg.generateKeyPair()

                encode(keyPair.public.encoded)
            }
        }

        fun sign(alias: String, data: Buffer): String {
            val keyStore = KeyStore.getInstance(KEY_STORE).apply {
                load(null)
            }

            val entry = keyStore.getEntry(alias, null)
            val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey

            val signature = Signature.getInstance("SHA256withRSA").run {
                initSign(privateKey)
                update(data.readByteArray())
                sign()
            }

            return encode(signature)
        }

        fun hashPassword(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }

        fun checkPassword(password1: String, password2: String): Boolean {
            return BCrypt.checkpw(password1, password2)
        }

        private fun encode(data: ByteArray): String {
            return Base64.encodeToString(data, Base64.NO_WRAP)
        }
    }
}