package com.example.smartpest.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smartpest.models.TomatoDisease
import com.example.smartpest.ui.components.CareSteps
import com.example.smartpest.ui.components.SectionLabel

@Composable
fun FarmGuide(@Suppress("UNUSED_PARAMETER") navController: NavHostController) = TomatoGuideContent()

@Composable
fun TomatoGuideContent() {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val selected = TomatoDisease.entries.firstOrNull { it.modelIndex == selectedIndex }
    BackHandler(enabled = selected != null) { selectedIndex = null }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (selected != null) {
            key(selected.modelIndex) { TomatoConditionDetail(selected) { selectedIndex = null } }
        } else {
            Column(Modifier.widthIn(max = 720.dp).fillMaxSize().padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(20.dp))
                SectionLabel("THE TOMATO FIELD GUIDE")
                Spacer(Modifier.height(10.dp))
                Text("Know what you're looking at.", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(10.dp))
                Text("Eight diseases, one pest-damage category and healthy leaves. Learn the signs; don't diagnose by appearance alone.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search conditions or symptoms") }, singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Search, null) }, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(12.dp))
                val conditions = remember(query) { TomatoDisease.search(query) }
                if (conditions.isEmpty()) {
                    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("No matching tomato conditions", style = MaterialTheme.typography.titleMedium)
                        Text("Try a condition name or a visible symptom such as yellowing or webbing.", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { query = "" }) { Text("Clear search") }
                    }
                } else {
                    Text("${conditions.size} ${if (conditions.size == 1) "condition" else "conditions"}",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyColumn(contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)) {
                        items(conditions, key = { it.modelIndex }) { disease ->
                            Surface(onClick = { selectedIndex = disease.modelIndex }, color = MaterialTheme.colorScheme.surface) {
                                Row(Modifier.fillMaxWidth().padding(vertical = 20.dp), verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text((disease.modelIndex - 27).toString().padStart(2, '0'),
                                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(disease.displayName, style = MaterialTheme.typography.titleMedium)
                                        Text(disease.category + if (disease.urgent) " · Prompt attention" else "",
                                            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TomatoConditionDetail(disease: TomatoDisease, onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    Column(Modifier.widthIn(max = 720.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("All conditions")
        }
        SectionLabel(disease.category)
        Text(disease.displayName, style = MaterialTheme.typography.headlineLarge)
        Text(disease.summary, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Text("What to look for", style = MaterialTheme.typography.titleLarge)
        disease.symptoms.forEach { Text("•  $it", style = MaterialTheme.typography.bodyMedium) }
        Text("Care & prevention", style = MaterialTheme.typography.titleLarge)
        CareSteps(disease.actions)
        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
            Text("Similar-looking symptoms can have different causes. Confirm with a local plant adviser before treatment. Use only locally registered products and follow the label, protective equipment and harvest intervals.",
                Modifier.padding(18.dp), style = MaterialTheme.typography.bodySmall)
        }
        TextButton(onClick = { uriHandler.openUri(disease.referenceUrl) }, contentPadding = PaddingValues(0.dp)) {
            Text("Read extension guidance ↗")
        }
    }
}
