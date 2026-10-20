package com.example.smartpest.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.smartpest.models.TomatoPrediction
import com.example.smartpest.ui.components.CareSteps
import com.example.smartpest.ui.components.LeafIllustration
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.viewmodels.ScanUiState
import kotlin.math.roundToInt

@Composable
fun TomatoScanContent(
    state: ScanUiState,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
    onRetry: () -> Unit,
    onGuide: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            Modifier.widthIn(max = 720.dp).fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                SectionLabel("TOMATO LEAF SCAN")
                Spacer(Modifier.height(10.dp))
                Text("One leaf. A clearer answer.", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(10.dp))
                Text("Photograph a single tomato leaf in natural light. Keep the affected area sharp and fill the frame.",
                    style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 210.dp).aspectRatio(1.5f), contentAlignment = Alignment.Center) {
                        if (state.bitmap != null) {
                            Image(state.bitmap.asImageBitmap(), "Selected tomato leaf for analysis",
                                Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)), contentScale = ContentScale.Fit)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                LeafIllustration(Modifier.size(132.dp))
                                Text(if (state.isLoading) "Preparing your photo…" else "A close-up works best",
                                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onPickImage, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)) {
                        Icon(Icons.Outlined.AddPhotoAlternate, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(if (state.bitmap == null) "Choose a photo" else "Choose another photo")
                    }
                    OutlinedButton(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)) {
                        Icon(Icons.Outlined.CameraAlt, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Take a photo")
                    }
                }
            }
            item {
                Column(Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                    when {
                        state.isLoading -> Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Checking your tomato leaf", style = MaterialTheme.typography.titleMedium)
                                LinearProgressIndicator(Modifier.fillMaxWidth())
                                Text("Analysis stays on this device. You can choose another photo at any time.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        state.error != null -> NoticePanel("We couldn't complete the scan", state.error, error = true) {
                            TextButton(onClick = onRetry) { Text("Try again") }
                        }
                        state.prediction is TomatoPrediction.Detected -> DetectedTomato(state.prediction)
                        state.prediction is TomatoPrediction.Uncertain -> NoticePanel("No reliable match yet",
                            "The model's prediction is below the screening threshold. No disease diagnosis was made. Try a sharper close-up of one leaf, then seek local advice if symptoms persist.")
                        state.prediction is TomatoPrediction.NotTomato -> NoticePanel("Tomato leaf not recognized",
                            "The model's strongest match was outside its tomato categories. Use a clear photo of a tomato leaf. This model cannot reliably verify the plant species or reject every unrelated image.")
                        state.prediction is TomatoPrediction.InvalidOutput -> NoticePanel("Analysis unavailable",
                            "The model returned an unexpected result. We haven't made a diagnosis. Try another photo or contact a local plant adviser.", error = true) {
                            TextButton(onClick = onRetry) { Text("Try again") }
                        }
                        else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SectionLabel("BEFORE YOU SCAN")
                            CareSteps(listOf("Use one leaf, not the whole plant or a tomato fruit.",
                                "Avoid blur, deep shadows and strong reflections.", "Check the leaf underside too; a second view can help an adviser."))
                        }
                    }
                }
            }
            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Info, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("A photo provides a screening result, not a confirmed diagnosis. Model confidence is not measured accuracy. Other diseases and nutrient problems may look similar.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                }
                TextButton(onClick = onGuide, contentPadding = PaddingValues(vertical = 12.dp)) { Text("Explore the tomato care guide") }
            }
        }
    }
}

@Composable
private fun DetectedTomato(result: TomatoPrediction.Detected) {
    val match = result.match
    val disease = match.disease
    val uriHandler = LocalUriHandler.current
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (disease.category == "Healthy") "No supported disease detected" else "Possible match", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(disease.displayName, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("${(match.confidence * 100).roundToInt()}% model confidence", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(disease.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        if (disease.urgent) {
            NoticePanel("Seek local advice promptly", "Late blight can spread quickly. Ask a plant clinic or agricultural extension adviser to confirm the cause before treatment.")
        }
        Text("What to look for", style = MaterialTheme.typography.titleLarge)
        disease.symptoms.forEach { Text("•  $it", style = MaterialTheme.typography.bodyMedium) }
        Text("What to do next", style = MaterialTheme.typography.titleLarge)
        CareSteps(disease.actions)
        Text("Confirm the cause before using a pesticide. Follow local registration, the product label, protective equipment and harvest intervals. Advice here does not prescribe a dose.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextButton(onClick = { uriHandler.openUri(disease.referenceUrl) }, contentPadding = PaddingValues(0.dp)) {
            Text("Read extension guidance ↗")
        }
    }
}

@Composable
private fun NoticePanel(title: String, body: String, error: Boolean = false, action: @Composable () -> Unit = {}) {
    Surface(color = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(body, style = MaterialTheme.typography.bodyMedium)
            action()
        }
    }
}
