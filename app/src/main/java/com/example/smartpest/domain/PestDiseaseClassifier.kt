package com.example.smartpest.domain

import android.graphics.Bitmap

interface PestDiseaseClassifier {
    fun classify(bitmap: Bitmap,rotation: Int): List<Classification>
}