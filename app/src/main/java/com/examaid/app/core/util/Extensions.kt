package com.examaid.app.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toFormattedString(pattern: String = "dd MMM yyyy, HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale("tr"))
    return formatter.format(this)
}

fun Long.toDate(): Date = Date(this)

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

