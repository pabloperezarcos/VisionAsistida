package com.example.visionasistida.util

import android.util.Patterns

fun String.isValidEmail(): Boolean =
    isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trim()).matches()

fun String.isStrongPassword(min: Int = 6): Boolean =
    length >= min
