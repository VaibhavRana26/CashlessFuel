package com.example.cashlessfuel.model

import com.google.firebase.firestore.FieldValue

data class TransactionModel(
    var amount: Double?=null,
    var pump: String?=null,
    var quantity: Double? = null,
    var rate: Double? = null,
    var station: String? = null,
    var time: FieldValue? = null,
    var user_phone: String? = null

    )