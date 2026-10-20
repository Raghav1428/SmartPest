package com.example.smartpest

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.smartpest.models.*
import com.example.smartpest.view.TomatoScanContent
import com.example.smartpest.viewmodels.ScanUiState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class TomatoScanTest {
    @get:Rule val compose = createComposeRule()

    @Test fun rgbNormalizationResizesAndPreservesChannelOrder() {
        val bitmap = Bitmap.createBitmap(13, 29, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.rgb(255, 128, 0)) }
        val buffer = TensorFlowHelper.imageToBuffer(bitmap)
        assertEquals(224 * 224 * 3 * 4, buffer.remaining())
        assertEquals(1f, buffer.float, 0.00001f)
        assertEquals(128f / 255f, buffer.float, 0.00001f)
        assertEquals(0f, buffer.float, 0.00001f)
        bitmap.recycle()
    }

    @Test fun bundledModelExecutesWithRealOutputContract() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GREEN) }
        val result = TensorFlowHelper.classifyTomato(context, bitmap)
        // This is an integration smoke test, not an accuracy test: a solid color has no ground truth.
        assertFalse(result is TomatoPrediction.InvalidOutput)
        bitmap.recycle()
    }

    @Test fun emptyScreenOffersBothAcquisitionMethods() {
        var picked = false
        var camera = false
        compose.setContent { MaterialTheme { TomatoScanContent(ScanUiState(), { picked = true }, { camera = true }, {}, {}) } }
        compose.onNodeWithText("Choose a photo").performClick()
        compose.onNodeWithText("Take a photo").performClick()
        assertTrue(picked)
        assertTrue(camera)
        compose.onNodeWithText("One leaf. A clearer answer.").assertIsDisplayed()
    }

    @Test fun loadingDoesNotShowAnOldDiagnosis() {
        compose.setContent { MaterialTheme { TomatoScanContent(ScanUiState(isLoading = true), {}, {}, {}, {}) } }
        compose.onNodeWithText("Checking your tomato leaf").assertExists()
        compose.onNodeWithText("Possible match").assertDoesNotExist()
    }

    @Test fun uncertainResultHasNoDiseaseTreatment() {
        val disease = TomatoDisease.entries.first { it.modelIndex == 30 }
        compose.setContent { MaterialTheme { TomatoScanContent(
            ScanUiState(prediction = TomatoPrediction.Uncertain(listOf(TomatoMatch(disease, 0.4f)))), {}, {}, {}, {}) } }
        compose.onNodeWithText("No reliable match yet").assertExists()
        compose.onNodeWithText("What to do next").assertDoesNotExist()
    }

    @Test fun nonTomatoResultDoesNotPresentACondition() {
        compose.setContent { MaterialTheme { TomatoScanContent(ScanUiState(prediction = TomatoPrediction.NotTomato), {}, {}, {}, {}) } }
        compose.onNodeWithText("Tomato leaf not recognized").assertExists()
        compose.onNodeWithText("Possible match").assertDoesNotExist()
    }

    @Test fun errorProvidesAnActualRetryAction() {
        var retried = false
        compose.setContent { MaterialTheme { TomatoScanContent(ScanUiState(error = "The photo could not be read."), {}, {}, { retried = true }, {}) } }
        compose.onNodeWithText("Try again").performScrollTo().performClick()
        assertTrue(retried)
    }

    @Test fun detectedResultExplainsConfidenceAndShowsCare() {
        compose.setContent { MaterialTheme { TomatoScanContent(ScanUiState(prediction = TomatoPrediction.Detected(
            TomatoMatch(TomatoDisease.entries.first(), 0.9f))), {}, {}, {}, {}) } }
        compose.onNodeWithText("Bacterial spot").assertExists()
        compose.onNodeWithText("90% model confidence").assertExists()
        compose.onNodeWithText("What to do next").performScrollTo().assertIsDisplayed()
    }
}
