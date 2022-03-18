package com.explorer.utils.other

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import java.io.FileDescriptor

fun uriToBitmap(activity: ComponentActivity, uri: Uri): Bitmap {
    val parcelFileDescriptor: ParcelFileDescriptor? =
        activity.contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image
}