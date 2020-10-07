package com.example.cashlessfuel.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var firstName: String? = "",
    var lastName: String? = "",
    var phone: String? = "",
    var email: String? = "",
    var address: String? = "",
    var wallet: Double? = 0.0
)