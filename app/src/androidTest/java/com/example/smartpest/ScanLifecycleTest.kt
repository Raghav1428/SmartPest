package com.example.smartpest

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.SystemClock
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.example.smartpest.models.ScanImageLoader
import com.example.smartpest.viewmodels.ScanViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ScanLifecycleTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val app get() = instrumentation.targetContext.applicationContext as Application

    private fun photo(width: Int = 300, height: Int = 200): Pair<File, Uri> {
        val directory = File(app.cacheDir, "tomato_photos").apply { mkdirs() }
        val file = File.createTempFile("test_", ".png", directory)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GREEN) }
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        return file to FileProvider.getUriForFile(app, "${app.packageName}.scanfiles", file)
    }

    private fun waitForResult(vm: ScanViewModel) {
        val deadline = SystemClock.uptimeMillis() + 15_000
        while (vm.state.value.isLoading && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(30)
        assertFalse("Scan didn't finish within 15 seconds", vm.state.value.isLoading)
    }

    @Test fun largePhotosDecodeToBoundedSoftwareBitmaps() = runBlocking {
        val (file, uri) = photo(4000, 2500)
        try {
            val bitmap = ScanImageLoader.load(app, uri)
            assertEquals(1600, bitmap.width)
            assertEquals(1000, bitmap.height)
            assertNotEquals(Bitmap.Config.HARDWARE, bitmap.config)
            bitmap.recycle()
        } finally { file.delete() }
    }

    @Test fun newSelectionClearsOldPredictionAndAnUnreadablePhotoShowsError() {
        val (file, uri) = photo()
        val store = ViewModelStore()
        lateinit var vm: ScanViewModel
        instrumentation.runOnMainSync {
            vm = ScanViewModel(app, SavedStateHandle())
            store.put("scan", vm)
            vm.selectImage(uri)
        }
        try {
            waitForResult(vm)
            assertNotNull(vm.state.value.prediction)
            instrumentation.runOnMainSync {
                vm.selectImage(Uri.parse("content://${app.packageName}.scanfiles/tomato_photos/does-not-exist.png"))
                assertTrue(vm.state.value.isLoading)
                assertNull(vm.state.value.prediction)
                assertNull(vm.state.value.bitmap)
            }
            waitForResult(vm)
            assertNotNull(vm.state.value.error)
            assertNull(vm.state.value.prediction)
        } finally {
            instrumentation.runOnMainSync { store.clear() }
            file.delete()
        }
    }

    @Test fun latestSelectionWinsAndSavedUriCanBeRestored() {
        val (file, uri) = photo()
        val savedState = SavedStateHandle()
        val store = ViewModelStore()
        lateinit var vm: ScanViewModel
        instrumentation.runOnMainSync {
            vm = ScanViewModel(app, savedState)
            store.put("scan", vm)
            vm.selectImage(Uri.parse("content://not-a-provider/invalid"))
            vm.selectImage(uri)
        }
        try {
            waitForResult(vm)
            assertEquals(uri, vm.state.value.imageUri)
            assertNull(vm.state.value.error)
            assertNotNull(vm.state.value.prediction)
            instrumentation.runOnMainSync {
                store.clear()
                vm = ScanViewModel(app, savedState)
                store.put("restored", vm)
            }
            waitForResult(vm)
            assertEquals(uri, vm.state.value.imageUri)
            assertNotNull(vm.state.value.prediction)
        } finally {
            instrumentation.runOnMainSync { store.clear() }
            file.delete()
        }
    }
}
