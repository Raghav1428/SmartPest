package com.example.smartpest

import android.graphics.Bitmap
import android.content.Context
import com.example.smartpest.ml.Pestdisease
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.util.Log

object TensorFlowHelper {

    val imageSize = 224

    fun ClassifyPestOrDisease(context: Context, image: Bitmap, callback: (result: String) -> Unit) {
        try {
            Log.d("TensorFlowHelper", "Loading the model...")
            val model = Pestdisease.newInstance(context)

            Log.d("TensorFlowHelper", "Preparing input buffer...")
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0

            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            Log.d("TensorFlowHelper", "Running model inference...")
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray

            // Log confidence values for debugging
            confidences.forEachIndexed { index, confidence ->
                Log.d("TensorFlowHelper", "Class $index: Confidence $confidence")
            }

            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            val classes = arrayOf(
                "Tomato healthy", "Tomato mosaic virus",
                "Tomato yellow leaf curl virus", "Tomato target spot", "Tomato spider mites",
                "Tomato septoria leaf spot", "Tomato leaf mold", "Tomato late blight",
                "Tomato early blight", "Tomato bacterial spot"
            )

            //close the model for resource management of the app
            model.close()

            Log.d("TensorFlowHelper", "Returning classification result: ${classes[maxPos]}")
            callback(classes[maxPos])

        } catch (e: Exception) {
            Log.e("TensorFlowHelper", "Error during classification", e)
            callback("Error in classification")
        }
    }
}