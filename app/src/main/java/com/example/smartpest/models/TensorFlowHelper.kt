package com.example.smartpest.models

import android.graphics.Bitmap
import android.content.Context
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.util.Log
import com.example.smartpest.ml.AutoModel1

object TensorFlowHelper {

    val imageSize = 224

    fun ClassifyPestOrDisease(context: Context, image: Bitmap, callback: (result: String) -> Unit) {
        try {

            val model = AutoModel1.newInstance(context)

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
                "Apple Scab",
                "Apple Black Rot",
                "Apple Cedar Apple Rust",
                "Apple Healthy",
                "Blueberry Healthy",
                "Cherry Powdery Mildew",
                "Cherry Healthy",
                "Corn Cercospora Leaf Spot Gray Leaf Spot",
                "Corn Common Rust",
                "Corn (maize) Northern Leaf Blight",
                "Corn (maize) healthy",
                "Grape Black Rot",
                "Grape Esca (Black Measles)",
                "Grape Leaf Blight (Isariopsis Leaf Spot)",
                "Grape Healthy",
                "Orange Haunglongbing (Citrus Greening)",
                "Peach Bacterial Spot",
                "Peach Healthy",
                "Pepper Bell Bacterial Spot",
                "Pepper Bell Healthy",
                "Potato Early Blight",
                "Potato Late Blight",
                "Potato Healthy",
                "Raspberry Healthy",
                "Soybean Healthy",
                "Squash Powdery Mildew",
                "Strawberry Leaf Scorch",
                "Strawberry Healthy",
                "Tomato Bacterial Spot",
                "Tomato Early Blight",
                "Tomato Late Blight",
                "Tomato Leaf Mold",
                "Tomato Septoria Leaf Spot",
                "Tomato Spider Mites Two-spotted Spider Mite",
                "Tomato Target Spot",
                "Tomato Tomato Yellow Leaf Curl Virus",
                "Tomato Tomato Mosaic Virus",
                "Tomato Healthy"
            )

            //close the model for resource management of the app
            model.close()

            if (maxConfidence > 0.7) {
                callback(classes[maxPos])
            } else {
                callback("Uncertain classification")
            }

        } catch (e: Exception) {
            callback("Error in classification")
        }
    }
}