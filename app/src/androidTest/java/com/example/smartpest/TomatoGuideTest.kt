package com.example.smartpest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.smartpest.ui.theme.SmartPestTheme
import com.example.smartpest.view.TomatoGuideContent
import org.junit.Rule
import org.junit.Test

class TomatoGuideTest {
    @get:Rule val compose = createComposeRule()

    @Test fun largeTextOnASmallScreenCanStillReachTheLastCondition() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                SmartPestTheme {
                    Box(Modifier.size(320.dp, 480.dp)) { TomatoGuideContent() }
                }
            }
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Healthy-looking leaf"))
        compose.onNodeWithText("Healthy-looking leaf").assertIsDisplayed()
    }

    @Test fun searchShowsOnlyMatchingTomatoConditions() {
        compose.setContent { SmartPestTheme { TomatoGuideContent() } }
        compose.onNodeWithText("Search conditions or symptoms").performTextInput("webbing")
        compose.onNodeWithText("Two-spotted spider mite damage").assertIsDisplayed()
        compose.onNodeWithText("Bacterial spot").assertDoesNotExist()
    }

    @Test fun noResultsCanBeCleared() {
        compose.setContent { SmartPestTheme { TomatoGuideContent() } }
        compose.onNodeWithText("Search conditions or symptoms").performTextInput("apple scab")
        compose.onNodeWithText("No matching tomato conditions").assertExists()
        compose.onNodeWithText("Clear search").performClick()
        compose.onNodeWithText("Bacterial spot").assertExists()
    }

    @Test fun conditionOpensSymptomsCareAndReturnsToLibrary() {
        compose.setContent { SmartPestTheme(darkTheme = true) { TomatoGuideContent() } }
        compose.onNodeWithText("Bacterial spot").performClick()
        compose.onNodeWithText("What to look for").assertExists()
        compose.onNodeWithText("All conditions").performClick()
        compose.onNodeWithText("Know what you're looking at.").assertExists()
    }
}
