package com.example.cashlessfuel

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat


class WalletActivity : AppCompatActivity() {

    var btn_add : Button?=null
    var tv_wallet : TextView? =null
    var ed_money : TextView? =null
    var progressBar : ProgressBar? =null

    val firestore = Firebase.firestore
    lateinit var auth: FirebaseAuth
    var wallet : Double? = 0.0
    val precision = DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        auth = FirebaseAuth.getInstance()

        bindView()
        getWallet()
    }


        private fun getWallet() {
            firestore.collection("users")
                .whereEqualTo("phone", auth?.currentUser?.phoneNumber)
                .addSnapshotListener{ snapshot, e->
                    if(e !=null){
                        e.printStackTrace()
                    }else{
                        var walletTest = snapshot?.documents?.get(0)?.data?.get("wallet").toString()
                        if(walletTest == null)
                            wallet = 0.00

                        tv_wallet?.text = "$ ${wallet}"
                    }
                }

        }


    private fun bindView() {
        btn_add = findViewById(R.id.btn_add)
        tv_wallet = findViewById(R.id.tv_wallet)
        ed_money = findViewById(R.id.ed_money)
        progressBar = findViewById(R.id.progressBar)

        btn_add?.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            val load = ed_money?.text?.toString()?.toDouble()
            if(load!=null)
            {
                val q = firestore.collection("users")
                q.whereEqualTo("phone", auth?.currentUser?.phoneNumber)
                    .get()
                    .addOnCompleteListener {
                        if(it.isComplete){
                            for (document in it.getResult()) {
                                val map: MutableMap<String, Double?> = HashMap()
                                wallet = wallet?.plus(load)
                                map["wallet"] = wallet
                                q.document(document.id).set(map, SetOptions.merge())
                                Toast.makeText(applicationContext,"Wallet has been updated",Toast.LENGTH_LONG).show()
                                progressBar?.visibility = View.GONE
                                finish()
                            }

                        }
                    }.addOnFailureListener{
                        it.printStackTrace()
                        progressBar?.visibility = View.GONE
                        Toast.makeText(applicationContext,"Error, try again later...",Toast.LENGTH_LONG).show()

                    }

            }
        }

    }
}