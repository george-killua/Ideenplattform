package com.killua.ideenplattform.data.utils

import android.os.Build
import java.util.*

object DeEnCoderHandler {
    fun toBase64(string: String) = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getEncoder().encodeToString(string.toByteArray())
    } else {
        android.util.Base64.encodeToString(string.toByteArray(), android.util.Base64.DEFAULT)
    })!!

    fun fromBase64(string: String) = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getDecoder().decode(string)
    } else {
        android.util.Base64.decode(string, android.util.Base64.DEFAULT)
    })!!

}
