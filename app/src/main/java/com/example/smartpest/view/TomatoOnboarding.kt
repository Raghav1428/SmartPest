package com.example.smartpest.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartpest.ui.components.CareSteps
import com.example.smartpest.ui.components.LeafIllustration
import com.example.smartpest.ui.components.SectionLabel
import com.example.smartpest.ui.components.TomatoMark

@Composable
fun TomatoOnboarding(onGetStarted: () -> Unit) {
    Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 560.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TomatoMark(Modifier.size(36.dp))
                Text("TomatoCare", style = MaterialTheme.typography.titleLarge)
            }
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large) {
                Box(Modifier.fillMaxWidth().height(185.dp), contentAlignment = Alignment.Center) {
                    LeafIllustration(Modifier.size(145.dp))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel("FOR THE LEAVES YOU LOOK AFTER")
                Text("Better care starts with a closer look.", style = MaterialTheme.typography.headlineLarge)
                Text("A focused companion for tomato growers. Check leaf symptoms and learn what to do next.",
                    style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            CareSteps(listOf("Choose or take a clear photo of one tomato leaf.",
                "Get an on-device screening result, with uncertainty made clear.", "Compare symptoms and read practical tomato care guidance."))
            Text("The model covers a limited set of conditions. Photo screening is not a confirmed diagnosis, and confidence is not measured accuracy.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) { Text("Get started") }
        }
    }
}
