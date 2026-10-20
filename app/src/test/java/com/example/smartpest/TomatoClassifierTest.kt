package com.example.smartpest

import com.example.smartpest.models.TomatoClassifier
import com.example.smartpest.models.TomatoDisease
import com.example.smartpest.models.TomatoPrediction
import org.junit.Assert.*
import org.junit.Test

class TomatoClassifierTest {
    private fun distribution(winner: Int, probability: Float = 0.9f): FloatArray =
        FloatArray(38) { (1f - probability) / 37f }.also { it[winner] = probability }

    @Test fun `all ten inherited tomato output indices map to the correct conditions`() {
        val names = listOf("Bacterial spot", "Early blight", "Late blight", "Leaf mold",
            "Septoria leaf spot", "Two-spotted spider mite damage", "Target spot",
            "Tomato yellow leaf curl virus", "Tomato mosaic virus", "Healthy-looking leaf")
        names.forEachIndexed { offset, name ->
            val result = TomatoClassifier.classify(distribution(28 + offset)) as TomatoPrediction.Detected
            assertEquals(name, result.match.disease.displayName)
            assertEquals(0.9f, result.match.confidence, 0.00001f)
        }
    }

    @Test fun `every non tomato global winner is declined`() {
        (0..27).forEach { index ->
            assertTrue("Index $index", TomatoClassifier.classify(distribution(index)) is TomatoPrediction.NotTomato)
        }
    }

    @Test fun `a tomato runner up never overrides another crop`() {
        val scores = FloatArray(38).also { it[0] = 0.51f; it[28] = 0.49f }
        assertTrue(TomatoClassifier.classify(scores) is TomatoPrediction.NotTomato)
    }

    @Test fun `tomato probabilities are not renormalized after dropping other crops`() {
        val scores = FloatArray(38).also { it[28] = 0.65f; it[0] = 0.35f }
        val result = TomatoClassifier.classify(scores) as TomatoPrediction.Uncertain
        assertEquals(0.65f, result.candidates.first().confidence, 0.00001f)
    }

    @Test fun `cutoff accepts seventy percent but declines lower scores`() {
        assertTrue(TomatoClassifier.classify(distribution(29, 0.70f)) is TomatoPrediction.Detected)
        assertTrue(TomatoClassifier.classify(distribution(29, 0.699f)) is TomatoPrediction.Uncertain)
    }

    @Test fun `uncertain candidates are sorted and limited without a diagnosis`() {
        val scores = FloatArray(38).also { it[30] = 0.4f; it[29] = 0.3f; it[28] = 0.2f; it[0] = 0.1f }
        val result = TomatoClassifier.classify(scores) as TomatoPrediction.Uncertain
        assertEquals(listOf(30, 29, 28), result.candidates.map { it.disease.modelIndex })
    }

    @Test fun `wrong shapes and malformed probability distributions are not interpreted`() {
        val invalid = listOf(FloatArray(0), FloatArray(10), FloatArray(37), FloatArray(39),
            FloatArray(38), FloatArray(38) { 0.1f },
            distribution(28).also { it[0] = Float.NaN },
            distribution(28).also { it[0] = Float.POSITIVE_INFINITY },
            distribution(28).also { it[0] = -0.1f },
            distribution(28).also { it[28] = 1.1f })
        invalid.forEach { assertTrue(TomatoClassifier.classify(it) is TomatoPrediction.InvalidOutput) }
    }

    @Test fun `healthy and mite damage remain distinct from infectious diseases`() {
        assertEquals("Healthy", TomatoDisease.entries.single { it.modelIndex == 37 }.category)
        assertEquals("Pest damage", TomatoDisease.entries.single { it.modelIndex == 33 }.category)
        assertEquals(8, TomatoDisease.entries.count { it.category == "Disease" })
    }

    @Test fun `search finds symptoms and ignores surrounding whitespace and case`() {
        assertTrue(TomatoDisease.search("  EARLY BLIGHT ").any { it.modelIndex == 29 })
        assertTrue(TomatoDisease.search("webbing").any { it.modelIndex == 33 })
        assertEquals(10, TomatoDisease.search(" ").size)
        assertTrue(TomatoDisease.search("apple scab").isEmpty())
    }

    @Test fun `all supported conditions provide symptoms safe actions and a reference`() {
        TomatoDisease.entries.forEach {
            assertTrue(it.symptoms.isNotEmpty())
            assertTrue(it.actions.isNotEmpty())
            assertTrue(it.referenceUrl.startsWith("https://"))
        }
        val virusAdvice = TomatoDisease.entries.filter { it.modelIndex in 35..36 }
        assertTrue(virusAdvice.all { it.actions.any { action -> action.contains("no cure", ignoreCase = true) } })
    }
}
