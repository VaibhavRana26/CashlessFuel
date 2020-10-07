package com.example.cashlessfuel

import android.R.attr.phoneNumber
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cashlessfuel.util.SharedPrefs
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {


    private var ed_username: TextInputEditText? = null

    val TIME_OUT = 60

    lateinit var currentUserPhone: TextView
    lateinit var otpTV: TextView
    lateinit var sendOTPbtn: Button
    lateinit var cb_privacy : CheckBox
    lateinit var tv_privacy : TextView

    var progressBar : ProgressBar? = null

    lateinit var auth: FirebaseAuth
    var job: Deferred<Unit>? = null

    var mCallback: OnVerificationStateChangedCallbacks? = null
    var verificationCode: String = ""
    var prefs : SharedPrefs? = null

    private fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))

    private fun Context.hideKeyboard(view: View) =
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                ).hideSoftInputFromWindow(view.windowToken, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        prefs = SharedPrefs()

        checkLogin()
        defineUI()

        startFirebaseLogin()

        //setPhoneNumber()

        sendOTPbtn.setOnClickListener {
            progressBar?.visibility = View.VISIBLE

            //TODO send OTP to the selected phone number
            if (ed_username?.text != null && ed_username?.text!!.isNotEmpty())
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    ed_username?.text.toString(),                     // Phone number to verify
                    TIME_OUT.toLong(),                           // Timeout duration
                    TimeUnit.SECONDS,                // Unit of timeout
                    this@LoginActivity,        // Activity (for callback binding)
                    mCallback!!
            )
            else {
                hideKeyboard()
                Toast
                    .makeText(
                        this@LoginActivity,
                        "Please type phone number",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }

        }



//        logout.setOnClickListener {
//            if (job != null && job!!.isActive)
//                job!!.cancel()
//            FirebaseAuth.getInstance().signOut()
//            //setPhoneNumber()
//        }
    }

    private fun checkLogin(){
        if(prefs?.getLogin(applicationContext)!!){
            val intent = Intent(this,BiometricActivity::class.java)
            startActivity(intent)
        }else{

        }
//        var user = auth.currentUser;
//
//        if (user?.phoneNumber!="") {
//            // User is signed in.
//            val intent = Intent(this,BiometricActivity::class.java)
//            startActivity(intent)
//        } else {
//            // No user is signed in.
//        }


    }

    private fun defineUI() {
        sendOTPbtn = findViewById(R.id.btn_request)
        otpTV = findViewById(R.id.textView)
        ed_username = findViewById(R.id.ed_username)
        progressBar = findViewById(R.id.progressBar)
        sendOTPbtn.setBackgroundColor(Color.GRAY)
        cb_privacy = findViewById(R.id.cb_privacy)
        tv_privacy = findViewById(R.id.tv_privacy_policy)
        //currentUserPhone = findViewById(R.id.textView4)
        //logout = findViewById(R.id.logout)

        cb_privacy?.setOnCheckedChangeListener{ compoundButton, b ->
            if(b) {
                sendOTPbtn.setBackgroundColor(Color.BLACK)
                sendOTPbtn.isEnabled = true

            }
            else {
                sendOTPbtn.setBackgroundColor(Color.GRAY)
                sendOTPbtn.isEnabled = false
            }
        }

        tv_privacy.setOnClickListener {
            val intent = Intent(this,TermsActivity::class.java)
            startActivity(intent)
        }


    }


    private fun countDown() = GlobalScope.async(Dispatchers.IO) {
        hideKeyboard()

        repeat(TIME_OUT+1) {
            val res = DecimalFormat("00").format(TIME_OUT - it)
            println("Kotlin Coroutines World! $res")
            withContext(Dispatchers.Main) {
                otpTV.text = "00:$res"
            }
            delay(1000)
        }
        println("finished")

    }

    private fun startFirebaseLogin() {
        mCallback = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Toast.makeText(this@LoginActivity, "verification completed", Toast.LENGTH_SHORT)
                    .show()
                progressBar?.visibility = View.GONE

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@LoginActivity, "verification failed", Toast.LENGTH_SHORT).show()
                Log.d("FirebaseException", e.toString())
                progressBar?.visibility = View.GONE

            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationCode = s
                Log.d("verificationCode", verificationCode)
                Toast.makeText(this@LoginActivity, "Code Sent", Toast.LENGTH_SHORT).show()
                job = if (job == null || job!!.isCancelled)
                    countDown()
                else {
                    job!!.cancel()
                    countDown()
                }

                val intent = Intent(this@LoginActivity,OTPActivity::class.java)
                intent.putExtra("otp",verificationCode)
                intent.putExtra("mobile",ed_username?.text.toString())
                startActivity(intent)
                progressBar?.visibility = View.GONE

            }
        }
    }
}