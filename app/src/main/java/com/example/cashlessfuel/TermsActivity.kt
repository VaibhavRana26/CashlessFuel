package com.example.cashlessfuel

import android.content.res.Resources
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream


class TermsActivity : AppCompatActivity() {
    var tv_privacy : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        tv_privacy = findViewById(R.id.tv_privacy_policy)
        try {
            val res: Resources = resources
            val in_s: InputStream = res.openRawResource(R.raw.terms)
            val b = ByteArray(in_s.available())
            in_s.read(b)
            tv_privacy?.setText(String(b))
        } catch (e: Exception) {
            tv_privacy?.setText("Error: can't show terms.")
        }
    }
}