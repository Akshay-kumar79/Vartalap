package com.example.vartalap.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    var image: String,
    var token: String
) : Parcelable
