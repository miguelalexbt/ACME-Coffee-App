package org.feup.cmov.acmeclient

import android.security.KeyPairGeneratorSpec
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.util.*
import javax.security.auth.x500.X500Principal

class Utils {
    companion object {
        private const val KEY_STORE = "AndroidKeyStore"

        @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
        suspend fun generateCertificate(
            alias: String
        ): String {
            return withContext(Dispatchers.IO) {
                val ks: KeyStore = KeyStore.getInstance(KEY_STORE).apply {
                    load(null)
                }

                val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", KEY_STORE)

                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 1)

                val spec = KeyPairGeneratorSpec
                    .Builder( MainApplication.instance)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=ACME, O=ACME Inc., C=PT"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.time).setEndDate(end.time)
                    .build()

                kpg.initialize(spec)
                kpg.generateKeyPair()

                val cert: Certificate = ks.getCertificate(alias)

                encode(cert.encoded)
            }
        }

        fun sign(username: String, data: ByteArray): String? {
            val ks: KeyStore = KeyStore.getInstance(KEY_STORE).apply {
                load(null)
            }

            val pk: PrivateKey = ks.getKey(username, null) as PrivateKey

            val signature = Signature.getInstance("SHA256withRSA").run {
                initSign(pk)
                update(data)
                sign()
            }

            return encode(signature)
        }

        fun verify(username: String, data: ByteArray, signature: String): Boolean {
            val ks: KeyStore = KeyStore.getInstance(KEY_STORE).apply {
                load(null)
            }

            val cert: Certificate = ks.getCertificate(username)

            return Signature.getInstance("SHA256withRSA").run {
                initVerify(cert)
                update(data)
                verify(decode(signature))
            }
        }

        private fun encode(data: ByteArray): String {
            return Base64.encodeToString(data, Base64.DEFAULT)
        }

        private fun decode(data: String): ByteArray {
            return Base64.decode(data, Base64.DEFAULT)
        }
    }
}