package com.example.cashlessfuel

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashlessfuel.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class ProfileActivity : AppCompatActivity() {

    var ed_first_name : TextInputEditText? = null
    var ed_last_name : TextInputEditText? = null
    var ed_mobile_number : EditText? = null
    var ed_email : TextInputEditText? = null
    var ed_address : TextInputEditText? = null
    var btn_submit : Button? = null

    val db = Firebase.firestore
    private val TAG = "ProfileActivity"
    lateinit var auth: FirebaseAuth
    var frompage : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()

        try {
            frompage = intent?.extras?.getString("Profile")!!
        }catch (e : Exception){
            e.printStackTrace()
        }

        bindView()

        if (frompage?.equals("getdata")!!){
            getData()
        }


    }

    private fun getData(){

        //check
        db.collection("users")
            .whereEqualTo("phone", auth.currentUser?.phoneNumber.toString())
            .get()
            .addOnSuccessListener {
                var user : User? = it.documents[0].toObject(User::class.java)
                Log.d(
                    TAG,
                    "${user?.firstName} ${user?.lastName} ${user?.address} ${user?.phone} ${user?.email}"
                )
                ed_first_name?.setText(user?.firstName)
                ed_last_name?.setText(user?.lastName)
                ed_mobile_number?.setText(user?.phone)
                ed_email?.setText(user?.email)
                ed_address?.setText(user?.address)
            }
    }

    private fun bindView() {
        ed_first_name = findViewById(R.id.ed_first_name)
        ed_last_name = findViewById(R.id.ed_last_name)
        ed_mobile_number = findViewById(R.id.ed_mobile_number)
        ed_email = findViewById(R.id.ed_email)
        ed_address = findViewById(R.id.ed_address)
        btn_submit = findViewById(R.id.btn_submit)

        ed_mobile_number?.setText(auth.currentUser?.phoneNumber.toString())

        btn_submit?.setOnClickListener {

            var firstName = ed_first_name?.text.toString()
            if(firstName?.equals("")) {
                ed_first_name?.error = "First Name can not be null"
                return@setOnClickListener
            }
            var lastName = ed_last_name?.text.toString()
            if(lastName?.equals("")) {
                ed_last_name?.error = "Last Name can not be null"
                return@setOnClickListener
            }
            var email = ed_email?.text.toString()
            if(email?.equals("")) {
                ed_email?.error = "Email can not be null"
                return@setOnClickListener
            }
            if(!isValidEmail(email)) {
                ed_email?.error = "Email is invalid !"
                return@setOnClickListener
            }

            var address = ed_address?.text.toString()
            if(address?.equals("")) {
                ed_address?.error = "Address can not be null"
                return@setOnClickListener
            }

            val user = User(
                firstName = firstName,
                lastName = lastName,
                phone = auth.currentUser?.phoneNumber.toString(),
                email = email,
                address = address
            )
            val updates = hashMapOf<String, Any>(
                "firstName" to firstName,
                "firstName" to firstName,
                "lastName" to lastName,
                "phone" to auth.currentUser?.phoneNumber.toString(),
                "email" to email,
                "address" to address
            )

            var q = db.collection("users")

            q.whereEqualTo("phone",auth.currentUser?.phoneNumber.toString())
                .get()
                .addOnCompleteListener {
                    if (it.isComplete) {
                        for (document in it.getResult()) {
                            q.document(document.id).update(updates)
                            Toast.makeText(
                                applicationContext,
                                "Profile Updated Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            if (frompage == "o") {
                                val intent = Intent(this, PinActivity::class.java)
                                intent.putExtra("Profile", "p")
                                startActivity(intent)
                            }

                        }
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext,"Profile Not Updated. Try Again...",Toast.LENGTH_SHORT).show()
                    it.printStackTrace()

                }
                db.collection("users")
                    .whereEqualTo("phone",auth.currentUser?.phoneNumber.toString())




        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


}