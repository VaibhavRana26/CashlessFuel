package com.example.cashlessfuel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashlessfuel.qrscan.ScanResultModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import java.text.DecimalFormat


class DashboardActivity : AppCompatActivity(),View.OnClickListener {

    var ll_scan_to_pay : LinearLayout? = null
    var ll_find_gas_station : LinearLayout? = null
    var ll_my_profile : LinearLayout? = null
    var ll_chat_now : LinearLayout? = null
    var ll_receipt : LinearLayout? = null
    var ll_about : LinearLayout? = null

    var btn_add_wallet : Button? = null
    var tv_wallet : TextView? = null
    val firestore = Firebase.firestore
    lateinit var auth: FirebaseAuth

    val precision = DecimalFormat("0.00")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        bindView()

        getWallet()
    }

    private fun getWallet() {
        firestore.collection("users")
            .whereEqualTo("phone",auth?.currentUser?.phoneNumber)
            .addSnapshotListener{snapshot,e->
                if(e !=null){
                    e.printStackTrace()
                }else{
                    var wallet = snapshot?.documents?.get(0)?.data?.get("wallet")
                    if(wallet=="")
                       wallet = "0.0"
                    tv_wallet?.text = "Your Wallet \n$${precision.format(wallet)}"
                }
            }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity", "Scanned")
                //Toast.makeText(this, "Scanned -> " + result.contents, Toast.LENGTH_SHORT).show()
                //textView.text = String.format("Scanned Result: %s", result)
                Log.d("Barcode Result", result.contents)
                Log.d("Barcode Result", result.toString())
                var data = result.contents
                val gasModel = Gson().fromJson(data, ScanResultModel::class.java)
                Log.d("Barcode Result g", gasModel.gas_station)
                Log.d("Barcode Result p", gasModel.pump)

                val intent = Intent(applicationContext, PayActivity::class.java)
                intent.putExtra("gas_station",gasModel.gas_station)
                intent.putExtra("pump",gasModel.pump)
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindView() {
         ll_scan_to_pay = findViewById(R.id.ll_scan_to_pay)
         ll_find_gas_station = findViewById(R.id.ll_find_gas_station)
         ll_my_profile = findViewById(R.id.ll_my_profile)
         ll_chat_now = findViewById(R.id.ll_chat_now)
         ll_receipt =  findViewById(R.id.ll_receipt)
         ll_about = findViewById(R.id.ll_about)

         tv_wallet = findViewById(R.id.tv_wallet)
         btn_add_wallet = findViewById(R.id.btn_add_wallet)

        ll_scan_to_pay?.setOnClickListener(this)
        ll_find_gas_station?.setOnClickListener(this)
        ll_my_profile?.setOnClickListener(this)
        ll_chat_now?.setOnClickListener(this)
        ll_receipt?.setOnClickListener(this)
        ll_about?.setOnClickListener(this)

        btn_add_wallet?.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ll_scan_to_pay ->  scanQR()
            //R.id.ll_scan_to_pay ->  payActivity()
            R.id.ll_find_gas_station -> mapsActivity()
            R.id.ll_my_profile -> myProfileActivity()
            R.id.ll_chat_now -> chatActivityIntent()
            R.id.ll_receipt -> receiptActivityIntent()
            R.id.ll_about -> aboutUsActivityIntent()
            R.id.btn_add_wallet -> addWalletActivity()
        }
    }

    private fun addWalletActivity() {

        val intent = Intent(this, WalletActivity::class.java)
        startActivity(intent)
    }

    private fun payActivity(){
        val intent = Intent(this, PayActivity::class.java)
        startActivity(intent)
    }

    private fun scanQR() {
        val intentIntegrator = IntentIntegrator(this@DashboardActivity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.setCameraId(0)
        intentIntegrator.setPrompt("SCAN")
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.initiateScan()
    }


    private fun mapsActivity(){
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun myProfileActivity(){
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("Profile","getdata")
        startActivity(intent)
    }
    private fun chatActivityIntent(){
        Toast.makeText(applicationContext,"Not available now.",Toast.LENGTH_SHORT).show()
//
//        val intent = Intent(this, DashboardActivity::class.java)
//        startActivity(intent)
    }
    private fun aboutUsActivityIntent(){
        val intent = Intent(this, AboutUsActivity::class.java)
        startActivity(intent)
    }
    private fun receiptActivityIntent(){
        val intent = Intent(this, ReceiptActivity::class.java)
        startActivity(intent)
    }



}