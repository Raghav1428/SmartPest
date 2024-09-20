package com.example.smartpest.data

import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import android.content.Context
import android.graphics.Bitmap
import com.example.smartpest.domain.Classification
import com.example.smartpest.domain.PestDiseaseClassifier

class TfLitePestDiseaseClassifier(
    private val context: Context,
    private val threshold: Float = 0.5f,
    private val maxResults: Int = 1
) :PestDiseaseClassifier {

    private var classifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder().build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.  
    }

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {

    }
}