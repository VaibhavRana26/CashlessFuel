package com.example.cashlessfuel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashlessfuel.model.mapModel.Invoice

class ReceiptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)


        val recyclerView = findViewById(R.id.rv_receipt) as RecyclerView

        //adding a layoutmanager
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        //crating an arraylist to store users using the data class user
        val users = ArrayList<Invoice>()

        //adding some dummy data to the list
        users.add(Invoice("Seven Eleven", "23 sept 2020","$50"))
        users.add(Invoice("Shell", "22 Sept 2020","$100"))
        users.add(Invoice("Caltex", "20 August 2020","$30"))
        users.add(Invoice("BP", "11 August 2020","$100"))

        //creating our adapter
        val adapter = ReceiptAdapter(users)

        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter
    }
}