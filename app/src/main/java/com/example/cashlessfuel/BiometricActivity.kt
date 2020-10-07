package com.example.cashlessfuel

import android.Manifest
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey


class BiometricActivity : AppCompatActivity() {

    private val KEY_NAME = "yourKey"
    private var cipher: Cipher? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private var textView: TextView? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null
    private var fingerprintManager: FingerprintManager? = null
    private var keyguardManager: KeyguardManager? = null
    private var btn_next : Button?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            fingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
            textView = findViewById<View>(R.id.textview) as TextView
            btn_next = findViewById<View>(R.id.btn_next) as Button
            if (!fingerprintManager!!.isHardwareDetected()) {
                textView?.setText("Your device doesn't support fingerprint authentication")
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.USE_FINGERPRINT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                textView?.setText("Please enable the fingerprint permission")
            }
            if (!fingerprintManager!!.hasEnrolledFingerprints()) {
                textView?.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings")
            }
            if (!keyguardManager!!.isKeyguardSecure()) {
                textView?.setText("Please enable lockscreen security in your device's Settings")
            } else {
                try {
                    generateKey()
                } catch (e: FingerprintException) {
                    e.printStackTrace()
                }
                if (initCipher()) {
                    cryptoObject = FingerprintManager.CryptoObject(cipher!!)
                    val helper = FingerPrintHandler(this)
                    helper.startAuth(fingerprintManager, cryptoObject)
                }
            }
        }
        btn_next?.setOnClickListener {
            val intent = Intent(this,PinActivity::class.java)
            startActivity(intent)
        }

    }
    @Throws(FingerprintException::class)
    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore?.load(null)
            keyGenerator?.init(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or
                            KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
                    )
                    .build()
            )
            keyGenerator?.generateKey()
        } catch (exc: KeyStoreException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: NoSuchAlgorithmException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: NoSuchProviderException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: InvalidAlgorithmParameterException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: CertificateException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: IOException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun initCipher(): Boolean {
        cipher = try {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        return try {
            keyStore!!.load(null)
            val key: SecretKey = keyStore!!.getKey(
                KEY_NAME,
                null
            ) as SecretKey
            cipher?.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
    private class FingerprintException(e: Exception?) : Exception(e)
}