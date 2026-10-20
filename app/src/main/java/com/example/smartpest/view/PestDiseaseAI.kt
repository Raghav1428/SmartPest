package com.example.smartpest.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smartpest.viewmodels.ScanViewModel
import java.io.File

@Composable
fun PestDiseaseAI(navController: NavHostController, scanViewModel: ScanViewModel = viewModel()) {
    val state by scanViewModel.state.collectAsState()
    val context = LocalContext.current
    var pendingCameraUri by rememberSaveable { mutableStateOf<String?>(null) }
    val onPhotoPicked: (Uri?) -> Unit = { uri ->
        if (uri != null) {
            // Both pickers feed the same scan flow; cancelling keeps the current photo/result.
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { /* Some providers grant temporary access only. */ }
            scanViewModel.selectImage(uri)
        }
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia(), onPhotoPicked)
    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument(), onPhotoPicked)
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingCameraUri?.let(Uri::parse)
        pendingCameraUri = null
        if (success && uri != null) scanViewModel.selectImage(uri)
        else if (uri != null) runCatching { context.contentResolver.delete(uri, null, null) }
    }
    val openImageBrowser: () -> Unit = {
        try {
            documentPicker.launch(arrayOf("image/*"))
        } catch (_: ActivityNotFoundException) {
            scanViewModel.acquisitionError("No image browser is available. Install or enable a gallery or Files app, then try again.")
        } catch (_: SecurityException) {
            scanViewModel.acquisitionError("The image browser couldn't be opened. Check that your gallery or Files app is enabled, then try again.")
        }
    }
    val pickPhoto: () -> Unit = {
        try {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (_: ActivityNotFoundException) {
            // Some Android 13+ system images advertise the picker API without shipping its activity.
            openImageBrowser()
        } catch (_: SecurityException) {
            openImageBrowser()
        }
    }
    TomatoScanContent(
        state = state,
        onPickImage = pickPhoto,
        onTakePhoto = {
            try {
                val directory = File(context.cacheDir, "tomato_photos").apply { mkdirs() }
                val expiry = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
                directory.listFiles()?.filter { it.lastModified() < expiry }?.forEach { it.delete() }
                val photo = File.createTempFile("leaf_", ".jpg", directory)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.scanfiles", photo)
                pendingCameraUri = uri.toString()
                camera.launch(uri)
            } catch (_: Exception) {
                pendingCameraUri = null
                scanViewModel.acquisitionError("The camera couldn't be opened. Choose a photo from your gallery instead.")
            }
        },
        onRetry = { if (state.imageUri != null) scanViewModel.retry() else pickPhoto() },
        onGuide = { navController.navigate("Farm Guide") { launchSingleTop = true } }
    )
}
