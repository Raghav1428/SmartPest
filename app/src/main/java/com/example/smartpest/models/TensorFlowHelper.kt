package com.example.smartpest.models

import android.content.Context
import android.graphics.Bitmap
import com.example.smartpest.ml.AutoModel1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

object TensorFlowHelper {
    const val imageSize = 224

    /** Retains the existing model's RGB [0,1] preprocessing; training metadata is unavailable. */
    fun imageToBuffer(image: Bitmap): ByteBuffer {
        val scaled = Bitmap.createScaledBitmap(image, imageSize, imageSize, true)
        try {
            val pixels = IntArray(imageSize * imageSize)
            scaled.getPixels(pixels, 0, imageSize, 0, 0, imageSize, imageSize)
            return ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
                .order(ByteOrder.nativeOrder()).apply {
                    pixels.forEach { pixel ->
                        putFloat(((pixel shr 16) and 0xFF) / 255f)
                        putFloat(((pixel shr 8) and 0xFF) / 255f)
                        putFloat((pixel and 0xFF) / 255f)
                    }
                    rewind()
                }
        } finally {
            if (scaled !== image) scaled.recycle()
        }
    }

    suspend fun classifyTomato(context: Context, image: Bitmap): TomatoPrediction =
        withContext(Dispatchers.Default) {
            val input = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            input.loadBuffer(imageToBuffer(image))
            val model = AutoModel1.newInstance(context.applicationContext)
            try {
                TomatoClassifier.classify(model.process(input).outputFeature0AsTensorBuffer.floatArray)
            } finally {
                model.close()
            }
        }
}
