package com.explorer.utils.system

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.explorer.utils.other.showLogAssert
import com.explorer.utils.other.showToast
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream
import java.util.*


class GetFilesFromStorage(private val activity: ComponentActivity): DefaultLifecycleObserver {

    private lateinit var getContent: ActivityResultLauncher<String>

    lateinit var uri: Uri

    val succes = MutableLiveData(false)

    override fun onCreate(owner: LifecycleOwner) {
        getContent = activity.activityResultRegistry.register("key", owner, GetContent()) {
            uri = it
            succes.postValue(true)
        }

    }

    fun setFile(mimeType: String) {
        getContent.launch(mimeType)
    }

    fun getMimeType(): String? {
        if (::uri.isInitialized) {
            val mimeType: String? = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                val cr: ContentResolver = activity.contentResolver
                cr.getType(uri)
            } else {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                    uri
                        .toString()
                )
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.lowercase(Locale.getDefault())
                )
            }
            return mimeType
        } else {
            showToast(activity, "file belum di pilih")
            return null
        }

    }

    @Throws(IOException::class)
    fun getBitmap(): Bitmap? {
        return if (::uri.isInitialized) {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                activity.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
            image
        } else {
            showToast(activity, "file belum di pilih")
            null
        }
    }

    fun getInputStream(): InputStream? {
        return if (::uri.isInitialized) {
            activity.contentResolver.openInputStream(uri)
        } else {
            showToast(activity, "file belum di pilih")
            null
        }
    }

}