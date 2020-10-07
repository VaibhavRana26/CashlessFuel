package com.example.cashlessfuel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.cashlessfuel.model.User
import com.example.cashlessfuel.util.SharedPrefs
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OTPActivity : AppCompatActivity() {

    var ed_otp : TextInputEditText? = null
    var btn_submit : Button? = null
    var receivedCode : String = ""
    lateinit var auth: FirebaseAuth
    var progressBar : ProgressBar? = null
    var mobile : String? = null
    var tv_mobile : TextView? = null
    val TAG = "OTPActivity"

    private fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: android.view.View(this))
    private fun Context.hideKeyboard(view: View) =
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                ).hideSoftInputFromWindow(view.windowToken, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)


        ed_otp = findViewById(R.id.ed_OTP)
        btn_submit = findViewById(R.id.btn_submit)
        progressBar = findViewById(R.id.progressBar)
        tv_mobile = findViewById(R.id.tv_mobile)

        receivedCode = intent.extras?.getString("otp").toString()
        mobile = intent.extras?.getString("mobile").toString()
        tv_mobile?.text = mobile
        auth = FirebaseAuth.getInstance()

        btn_submit?.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            hideKeyboard()
            if (ed_otp?.text!!.isNotEmpty()) {
                val credential =
                    PhoneAuthProvider.getCredential(receivedCode, ed_otp?.text.toString())
                SigninWithPhone(credential)
            } else {
                Toast
                    .makeText(
                        this@OTPActivity,
                        "Please type OTP number",
                        Toast.LENGTH_SHORT
                    )
                    .show()
                progressBar?.visibility = View.GONE

            }
        }



    }

    private fun SigninWithPhone(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@OTPActivity, "Correct OTP", Toast.LENGTH_LONG)
                        .show()

                    //setPhoneNumber()
                    addNewUser()


                } else {
                    Toast.makeText(this@OTPActivity, "Incorrect OTP", Toast.LENGTH_SHORT)
                        .show()
                }
                progressBar?.visibility = View.GONE

            }
    }

    private fun addNewUser(){
        val user = User(phone = auth?.currentUser?.phoneNumber.toString(),wallet = 0.0)
        Firebase.firestore.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(
                    applicationContext,
                    "You are Successfully Registered!",
                    Toast.LENGTH_SHORT
                ).show()
                val prefs = SharedPrefs()
                prefs.setLogin(applicationContext,true)
                prefs.setPhone(applicationContext,mobile)
                val intent = Intent(this,ProfileActivity::class.java)
                intent.putExtra("Profile","o")
                finish()
                finish()
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(
                    applicationContext,
                    "Error updating information.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}