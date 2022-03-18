package com.explorer.utils.other

import android.content.Context
import android.util.Log
import android.widget.Toast

fun showLogAssert(tag: String, msg: String) {
    Log.println(Log.ASSERT, tag, msg)
}

fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}