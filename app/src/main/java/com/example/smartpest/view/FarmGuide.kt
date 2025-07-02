package com.example.smartpest.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FarmGuide(navController: NavHostController) {

    BackHandler {
        navController.navigate("Home") {
            popUpTo("Home") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // Add padding to the top (e.g., 16.dp) and display the problems list
        ProblemList(
            problemsList = listOf(
                ProblemItem(
                    "Pest Infestation",
                    "Use integrated pest management (IPM) techniques and select resistant crop varieties."
                ),
                ProblemItem(
                    "Soil Erosion",
                    "Implement contour plowing, terracing, and cover crops to reduce soil erosion."
                ),
                ProblemItem(
                    "Water Scarcity",
                    "Utilize drip irrigation systems, rainwater harvesting, and drought-resistant crops."
                ),
                ProblemItem(
                    "Weed Control",
                    "Apply mulching, herbicides, or manual weeding to manage weed growth."
                ),
                ProblemItem(
                    "Nutrient Deficiency",
                    "Perform soil testing and apply the necessary fertilizers based on the test results."
                ),
                ProblemItem(
                    "Crop Diseases",
                    "Choose disease-resistant varieties and apply appropriate fungicides or crop rotation."
                ),
                ProblemItem(
                    "Climate Change",
                    "Adapt to climate change by diversifying crops and using climate-smart farming practices."
                ),
                ProblemItem(
                    "Low Yield",
                    "Enhance soil fertility, adopt modern farming techniques, and use high-yield crop varieties."
                ),
                ProblemItem(
                    "Market Fluctuations",
                    "Diversify your crops, enter into cooperatives, or sell through contract farming to reduce risk."
                ),
                ProblemItem(
                    "Post-Harvest Losses",
                    "Use proper storage techniques, timely harvesting, and pest control to minimize losses."
                ),
                ProblemItem(
                    "Salinity in Soil",
                    "Use salt-tolerant crops and implement improved irrigation management techniques."
                ),
                ProblemItem(
                    "Flooding and Water logging",
                    "Improve drainage systems, use raised beds, or grow flood-resistant crops."
                ),
                ProblemItem(
                    "Soil Compaction",
                    "Use deep tillage or subsoiling, and avoid overuse of heavy machinery."
                ),
                ProblemItem(
                    "Crop Frost Damage",
                    "Use frost protection techniques like irrigation, wind machines, or covering crops."
                ),
                ProblemItem(
                    "Heat Stress on Crops",
                    "Use shade netting, mulching, and drought-tolerant crop varieties."
                ),
                ProblemItem(
                    "Overuse of Fertilizers",
                    "Follow soil testing results and apply the appropriate amount of fertilizers."
                ),
                ProblemItem(
                    "Seed Germination Failure",
                    "Ensure good seed quality, proper moisture levels, and adequate soil temperature."
                ),
                ProblemItem(
                    "Inconsistent Rainfall",
                    "Implement rainwater harvesting, and adopt water-saving irrigation techniques."
                ),
                ProblemItem(
                    "Invasive Species",
                    "Use biological control methods, crop rotation, and early detection strategies."
                )
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

data class ProblemItem(
    val problem: String,
    val solution: String
)

@Composable
fun ProblemList(problemsList: List<ProblemItem>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(problemsList) { problemItem ->
            ExpandableProblemItem(problemItem = problemItem)
        }
    }
}

@Composable
fun ExpandableProblemItem(problemItem: ProblemItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = problemItem.problem,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp)
                )
            }


            if (isExpanded) {
                Spacer(
                    modifier = Modifier.height(8.dp),
                )
                Text(
                    text = problemItem.solution,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400
                )
            }
        }
    }
}
