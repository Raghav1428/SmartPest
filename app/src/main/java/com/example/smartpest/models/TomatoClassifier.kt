package com.example.smartpest.models

import kotlin.math.abs

data class TomatoMatch(val disease: TomatoDisease, val confidence: Float)

sealed interface TomatoPrediction {
    data class Detected(val match: TomatoMatch) : TomatoPrediction
    data class Uncertain(val candidates: List<TomatoMatch>) : TomatoPrediction
    data object NotTomato : TomatoPrediction
    data object InvalidOutput : TomatoPrediction
}

/** Decodes the original 38-class softmax output without making it a tomato-only distribution. */
object TomatoClassifier {
    const val OUTPUT_CLASSES = 38
    // Inherited cutoff. This is a screening heuristic, not a calibrated accuracy guarantee.
    const val MIN_CONFIDENCE = 0.70f

    fun classify(scores: FloatArray): TomatoPrediction {
        if (scores.size != OUTPUT_CLASSES ||
            scores.any { !it.isFinite() || it < 0f || it > 1f } ||
            abs(scores.sum() - 1f) > 0.02f
        ) return TomatoPrediction.InvalidOutput

        val winner = scores.indices.maxBy { scores[it] }
        val disease = TomatoDisease.entries.firstOrNull { it.modelIndex == winner }
            ?: return TomatoPrediction.NotTomato
        val confidence = scores[winner]
        return if (confidence >= MIN_CONFIDENCE) {
            TomatoPrediction.Detected(TomatoMatch(disease, confidence))
        } else {
            TomatoPrediction.Uncertain(
                TomatoDisease.entries.map { TomatoMatch(it, scores[it.modelIndex]) }
                    .sortedByDescending { it.confidence }.take(3)
            )
        }
    }
}
