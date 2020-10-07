package com.example.cashlessfuel

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cashlessfuel.model.StationModel
import com.example.cashlessfuel.model.TransactionModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class PayActivity : AppCompatActivity() {

    var tv_pump : TextView? = null
    var tv_gas_station : TextView? = null

    var tv_amount : TextView? = null
    var ed_lt_amount : EditText? = null
    var radiogroup : RadioGroup? = null

    var cb_e10 : RadioButton? = null
    var cb_91 : RadioButton? = null
    var cb_diesel : RadioButton? = null
    var cb_gas : RadioButton? = null


    var btn_pay : Button? = null

    var final_amount : Double?=null
    var amount : Double?=67.88
    val firestore = Firebase.firestore
    lateinit var auth: FirebaseAuth
    var myAmount = 0.0

    val precision = DecimalFormat("0.00")
    var gasltValue = 67.88

    var q : Query? = null
    var qroot  = firestore.collection("users")
    var mywallet = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        FirebaseApp.initializeApp(applicationContext)
        auth = FirebaseAuth.getInstance()

        var gas_station = intent?.extras?.getString("gas_station")
       // var gas_station = "shell"
       // var pump = "Pump 1"
        var pump = intent?.extras?.getString("pump")

        Log.d("PAYACTIVITY", "$gas_station $pump")

        q = firestore.collection("users").whereEqualTo("phone",auth?.currentUser?.phoneNumber)

        tv_pump = findViewById(R.id.tv_pump)
        tv_gas_station = findViewById(R.id.tv_gas_station)
        tv_amount = findViewById(R.id.tv_amount)
        ed_lt_amount = findViewById(R.id.ed_lt_amount)
        radiogroup = findViewById(R.id.radiogroup)

        cb_e10 = findViewById(R.id.cb_e10)
        cb_91 = findViewById(R.id.cb_91)
        cb_diesel = findViewById(R.id.cb_diesel)
        cb_gas = findViewById(R.id.cb_gas)

        btn_pay = findViewById(R.id.btn_pay)
        //setGasValue()

        setGasPrice(station = gas_station!!)

        tv_gas_station?.text = gas_station
        tv_pump?.text = "Pump # : $pump"

        radiogroup?.setOnCheckedChangeListener { p0, p1 ->

            when(p1){
                R.id.cb_e10 -> {
                    amount = 108.99
                    setGasValue()

                }
                R.id.cb_91 -> {
                    amount = 112.99
                    setGasValue()

                }
                R.id.cb_diesel -> {
                    amount = 105.99
                    setGasValue()

                }
                R.id.cb_gas -> {
                    amount = gasltValue
                    setGasValue()
                }
            }

        }



        ed_lt_amount?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    setGasValue()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btn_pay?.setOnClickListener {



            myAmount = ed_lt_amount?.text.toString().toDouble()
            if(myAmount == 0.0){
                Toast.makeText(applicationContext, "Please enter value", Toast.LENGTH_SHORT).show()
            }else{
                val c = Calendar.getInstance().time
                println("Current time => $c")

                val df = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
                val formattedDate: String = df.format(c)


                if(!checkBalance()){
                    Toast.makeText(applicationContext,"You don't have sufficient money!\nAdd money to your wallet",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                var transaction = TransactionModel(
                    String.format("%.2f", final_amount).toDouble(),
                    pump,
                    precision.format(myAmount).toDouble(),
                    amount,
                    gas_station,
                    FieldValue.serverTimestamp(),
                    auth?.currentUser?.phoneNumber
                )
                firestore.collection("transaction")
                    .add(transaction)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Successfully Paid !", Toast.LENGTH_LONG).show()
                        deductAmount()
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        finish()
                        finish()
                        startActivity(intent)
                    }
                    .addOnFailureListener{

                        it.printStackTrace()
                        Toast.makeText(
                            applicationContext,
                            "Error paying try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }



    }

    private fun deductAmount() {
        q?.get()?.addOnCompleteListener {
            if(it.isComplete){
                for (document in it.getResult()) {
                    val map: MutableMap<String, Double?> = HashMap()
                    mywallet = mywallet?.minus(final_amount!!)
                    map["wallet"] = mywallet
                    qroot.document(document.id).set(map, SetOptions.merge())
                    Toast.makeText(applicationContext,"Wallet has been updated",Toast.LENGTH_LONG).show()
                    finish()
                }

            }
        }
            ?.addOnFailureListener{
            it.printStackTrace()
            Toast.makeText(applicationContext,"Error, try again later...",Toast.LENGTH_LONG).show()

        }
        }


    private fun checkBalance(): Boolean {
        var r = false

       q?.get()?.addOnSuccessListener {
           mywallet = it.documents[0].getDouble("wallet")!!
           if (mywallet != null) {
               if(mywallet > final_amount!!)
                   r=true
           }
       }
        return r
    }

    private fun setGasPrice(station: String) {

        firestore.collection("stations")
            .document(station)
            .addSnapshotListener{ snapshot, e->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("PAY", "Current data: ${snapshot.data}")
                    var model  = snapshot.toObject(StationModel::class.java)
                    gasltValue = model?.gas_price!!.toDouble()
                    cb_gas?.text = "Gas Price - 1 litre - ${model?.gas_price}"
                } else {
                    Log.d("PAY", "Current data: null")
                }
            }


    }

    fun setGasValue(){
        myAmount = ed_lt_amount?.text?.toString()?.toDouble() ?: 0.0
        final_amount =  myAmount * (amount!! * 0.01)
        tv_amount?.text = "You pay : $${String.format("%.2f", final_amount)}"
    }
}