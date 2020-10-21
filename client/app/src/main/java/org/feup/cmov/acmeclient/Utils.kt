package org.feup.cmov.acmeclient

import android.security.KeyPairGeneratorSpec
import android.util.Base64
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.util.*
import javax.security.auth.x500.X500Principal

class Utils {

    companion object {
        private const val KEY_STORE = "AndroidKeyStore"
        private const val ALIAS = "acme"

        @Suppress("DEPRECATION")
        fun generateCertificate(): String {
            val ks: KeyStore = KeyStore.getInstance(KEY_STORE).apply {
                load(null)
            }

            val kpg = KeyPairGenerator.getInstance("RSA", KEY_STORE)

            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 1)

            val spec = KeyPairGeneratorSpec
                .Builder(App.instance)
                .setAlias(ALIAS)
                .setSubject(X500Principal("CN=ACME, O=ACME Inc., C=PT"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.time).setEndDate(end.time)
                .build()

            kpg.initialize(spec)
            kpg.generateKeyPair()

            val cert = ks.getCertificate(ALIAS)

            return encode(cert.encoded)
        }

        fun sign(data: ByteArray): String? {
            val ks: KeyStore = KeyStore.getInstance(KEY_STORE).apply {
                load(null)
            }

            val pk: PrivateKey = ks.getKey(ALIAS, null) as PrivateKey

            val signature: ByteArray? = Signature.getInstance("SHA256withRSA").run {
                initSign(pk)
                update(data)
                sign()
            }

            return encode(signature!!)
        }

        fun verify(data: ByteArray, signature: String): Boolean {
            val ks: KeyStore = KeyStore.getInstance(KEY_STORE).apply {
                load(null)
            }

            val cert: Certificate? = ks.getCertificate(ALIAS)

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