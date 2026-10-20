package com.example.smartpest

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.example.smartpest.ui.theme.SmartPestTheme
import com.example.smartpest.view.PestDiseaseAI
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class PhotoPickerIntegrationTest {
    @get:Rule val compose = createComposeRule()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    @Test fun choosePhotoOpensARealImageBrowserAndCanBeCancelledAndReopened() {
        compose.setContent { SmartPestTheme { PestDiseaseAI(rememberNavController()) } }
        repeat(2) {
            compose.onNodeWithText("Choose a photo").performScrollTo().performClick()
            val deadline = SystemClock.uptimeMillis() + 10_000
            var browserPackage = ""
            while (SystemClock.uptimeMillis() < deadline) {
                browserPackage = instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString().orEmpty()
                if (browserPackage.contains("documentsui") || browserPackage.contains("providers.media")) break
                SystemClock.sleep(100)
            }
            assertTrue("Expected the system image browser, got $browserPackage",
                browserPackage.contains("documentsui") || browserPackage.contains("providers.media"))
            assertTrue(instrumentation.uiAutomation.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK))
            // Returning from an external activity is asynchronous; wait for the scan to resume.
            compose.waitUntil(timeoutMillis = 10_000) {
                runCatching {
                    compose.onAllNodesWithText("Choose a photo").fetchSemanticsNodes().isNotEmpty()
                }.getOrDefault(false)
            }
            compose.onNodeWithText("Choose a photo").assertExists()
            compose.onNodeWithText("We couldn't complete the scan").assertDoesNotExist()
        }
    }

    @Test fun missingAllImageBrowsersShowsAnInlineErrorInsteadOfCrashing() {
        val registry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
                throw ActivityNotFoundException("No image browser installed")
            }
        }
        showWithRegistry(registry)
        compose.onNodeWithText("Choose a photo").performScrollTo().performClick()
        compose.onNodeWithText("We couldn't complete the scan").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("No image browser is available. Install or enable a gallery or Files app, then try again.").assertExists()
    }

    @Test fun deniedImageBrowserShowsAnInlineErrorInsteadOfCrashing() {
        val registry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
                throw SecurityException("Image browser access denied")
            }
        }
        showWithRegistry(registry)
        compose.onNodeWithText("Choose a photo").performScrollTo().performClick()
        compose.onNodeWithText("We couldn't complete the scan").performScrollTo().assertIsDisplayed()
    }

    @Test fun fallbackImageResultIsDecodedAndAnalyzed() {
        val context = instrumentation.targetContext
        val directory = File(context.cacheDir, "tomato_photos").apply { mkdirs() }
        val file = File.createTempFile("picker_test_", ".png", directory)
        val bitmap = Bitmap.createBitmap(320, 240, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GREEN) }
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.scanfiles", file)
        val registry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I, options: ActivityOptionsCompat?) {
                if (contract is ActivityResultContracts.PickVisualMedia) throw ActivityNotFoundException("Native picker unavailable")
                val intent = contract.createIntent(context, input)
                assertEquals(Intent.ACTION_OPEN_DOCUMENT, intent.action)
                // OpenDocument carries the allowed formats in EXTRA_MIME_TYPES (its base type is */*).
                assertArrayEquals(arrayOf("image/*"), intent.getStringArrayExtra(Intent.EXTRA_MIME_TYPES))
                dispatchResult(requestCode, Activity.RESULT_OK, Intent().setData(uri))
            }
        }
        try {
            showWithRegistry(registry)
            compose.onNodeWithText("Choose a photo").performScrollTo().performClick()
            compose.waitUntil(timeoutMillis = 20_000) {
                compose.onAllNodesWithContentDescription("Selected tomato leaf for analysis").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("Selected tomato leaf for analysis").assertExists()
            compose.onNodeWithText("Choose another photo").assertExists()
            compose.onNodeWithText("We couldn't complete the scan").assertDoesNotExist()
        } finally { file.delete() }
    }

    private fun showWithRegistry(registry: ActivityResultRegistry) {
        val owner = object : ActivityResultRegistryOwner { override val activityResultRegistry = registry }
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides owner) {
                SmartPestTheme { PestDiseaseAI(rememberNavController()) }
            }
        }
    }
}
