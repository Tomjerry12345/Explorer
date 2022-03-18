package com.explorer.ui.main.identifkasi

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.explorer.ml.ModelHewan
import com.explorer.ml.ModelUnquant
import com.explorer.utils.other.showLogAssert
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class IdentifikasiViewModel : ViewModel() {

    private val MAX_RESULT_DISPLAY = 3 // Maximum number of results displayed

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

//    fun onSelectImage(view: View) {
//        MaterialAlertDialogBuilder(view.context)
//            .setTitle("Action")
//            .setItems(items) { dialog, which ->
//                showLogAssert("select dialog", "$which")
//            }
//            .show()
//    }

    fun classifyImage(context: Context, image: Bitmap, imageSize:Int): String {
        this.context = context
        var result = ""
        try {
            result = runModel(context, image, imageSize)
            testModel(image)
        } catch (e: IOException) {
            showLogAssert("error", "${e.message}")
        }

        return result
    }

    private fun runModel(context: Context, image: Bitmap, imageSize: Int): String {
        var result = ""
        val model: ModelUnquant = ModelUnquant.newInstance(context)

        // Creates inputs for reference.
        val inputFeature0: TensorBuffer =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(imageSize * imageSize)
        image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
        var pixel = 0
        //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val values = intValues[pixel++] // RGB
                byteBuffer.putFloat((values shr 16 and 0xFF) * (1f / 1))
                byteBuffer.putFloat((values shr 8 and 0xFF) * (1f / 1))
                byteBuffer.putFloat((values and 0xFF) * (1f / 1))
            }
        }

        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs: ModelUnquant.Outputs = model.process(inputFeature0)
        val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
        val confidences: FloatArray = outputFeature0.floatArray
        // find the index of the class with the biggest confidence.
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            showLogAssert("confidence", "${confidences[i]}")
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }
//            val classes = arrayOf("Banana", "Orange", "Pen", "Sticky Notes")
        val classes = arrayOf("Lumba-lumba", "Ikan raja laut", "Komodo", "Cendrawasih", "Orang utan", "Paus")
//            result.setText(classes[maxPos])
        var s = ""
        for (i in classes.indices) {
            s += java.lang.String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100)
        }
//            result = classes[maxPos]
        result = s
        // Releases model resources if no longer used.
        model.close()
        return result
    }

    private val testModel: ModelHewan by lazy{

        // TODO 6. Optional GPU acceleration
        val compatList = CompatibilityList()

        val options = if(compatList.isDelegateSupportedOnThisDevice) {
            Log.d(TAG, "This device is GPU Compatible ")
            Model.Options.Builder().setDevice(Model.Device.GPU).build()
        } else {
            Log.d(TAG, "This device is GPU Incompatible ")
            Model.Options.Builder().setNumThreads(4).build()
        }

        // Initialize the Flower Model
        ModelHewan.newInstance(this.context, options)
    }

    private fun testModel(image: Bitmap) {
        // TODO 2: Convert Image to Bitmap then to TensorImage
//        val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))
        val tfImage = TensorImage.fromBitmap(image)

        // TODO 3: Process the image using the trained model, sort and pick out the top results
        val outputs = testModel.process(tfImage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score } // Sort with highest confidence first
            }.take(MAX_RESULT_DISPLAY) // take the top results

        // TODO 4: Converting the top probability items into a list of recognitions
        for (output in outputs) {
//            items.add(Recognition(output.label, output.score))
            showLogAssert("result", "label: ${output.label} skor: ${output.score}")
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IdentifikasiViewModel() as T
        }
    }

}