package com.example.cashlessfuel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cashlessfuel.util.SharedPrefs
import com.poovam.pinedittextfield.CirclePinField
import com.poovam.pinedittextfield.PinField.OnTextCompleteListener
import org.jetbrains.annotations.NotNull
import java.lang.Exception


class PinActivity : AppCompatActivity() {

    var circlePinField : CirclePinField? = null
    var btn_next : Button? = null
    var frompage : String? = "q"
    var tv_setup : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        try {
            frompage = intent?.extras?.getString("Profile","q")!!
        }catch (e : Exception){
            e.printStackTrace()
        }


        circlePinField = findViewById(R.id.circleField);
        btn_next = findViewById(R.id.btn_next);
        tv_setup = findViewById(R.id.tv_setup);

        if (frompage?.equals("p")!!){
            tv_setup?.visibility = View.VISIBLE
        }else{
            tv_setup?.visibility = View.GONE
        }
        val prefs = SharedPrefs()
        val pinValue = prefs.getPin(context = applicationContext)

        btn_next?.isEnabled = false

        circlePinField?.onTextCompleteListener = object : OnTextCompleteListener {
            override fun onTextComplete(@NotNull enteredText: String): Boolean {

                if(frompage == "p"){
                    prefs.setPin(applicationContext, enteredText);
                    btn_next?.isEnabled = true
                }else if(pinValue==enteredText){
                    btn_next?.isEnabled = true
                    navigateDashboard()
                }else{
                    btn_next?.isEnabled = false
                    Toast.makeText(applicationContext,"Please Enter correct 4 Digit Pin !",Toast.LENGTH_SHORT).show()
                }
                return true // Return false to keep the keyboard open else return true to close the keyboard
            }
        }

        btn_next?.setOnClickListener {
            if(btn_next?.isEnabled!!)
                navigateDashboard()
        }
    }

    fun navigateDashboard(){
        val intent = Intent(this,DashboardActivity::class.java)
        startActivity(intent)
    }
}