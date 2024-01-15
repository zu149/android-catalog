package com.example.registerfirebasebinding.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sourse(
    val id: String?,
    val name: String
) : Parcelable
