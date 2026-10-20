package com.example.smartpest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Local vector artwork: no network images, permissions or image-loading state. */
@Composable
fun TomatoMark(modifier: Modifier = Modifier) {
    val green = MaterialTheme.colorScheme.primary
    val red = MaterialTheme.colorScheme.tertiary
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawOval(red, Offset(w * .12f, h * .28f), Size(w * .76f, h * .66f))
        val calyx = Path().apply {
            moveTo(w * .5f, h * .35f); lineTo(w * .25f, h * .2f)
            lineTo(w * .44f, h * .23f); lineTo(w * .55f, h * .08f)
            lineTo(w * .58f, h * .25f); lineTo(w * .78f, h * .22f)
            lineTo(w * .61f, h * .39f); close()
        }
        drawPath(calyx, green)
        drawArc(Color.White.copy(alpha = .45f), 160f, 50f, false,
            Offset(w * .23f, h * .43f), Size(w * .5f, h * .4f), style = Stroke(w * .035f, cap = StrokeCap.Round))
    }
}

@Composable
fun LeafIllustration(modifier: Modifier = Modifier) {
    val leaf = MaterialTheme.colorScheme.primary
    val vein = MaterialTheme.colorScheme.onPrimary
    val outline = MaterialTheme.colorScheme.outlineVariant
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val shape = Path().apply {
            moveTo(w * .24f, h * .8f)
            cubicTo(w * .12f, h * .4f, w * .39f, h * .15f, w * .81f, h * .13f)
            cubicTo(w * .88f, h * .53f, w * .58f, h * .92f, w * .24f, h * .8f)
            close()
        }
        drawPath(shape, leaf)
        drawLine(vein.copy(alpha = .65f), Offset(w * .25f, h * .79f), Offset(w * .72f, h * .25f), w * .012f, StrokeCap.Round)
        for (i in 0..3) {
            val x = w * (.35f + i * .09f)
            val y = h * (.67f - i * .1f)
            drawLine(vein.copy(alpha = .45f), Offset(x, y), Offset(x - w * .03f, y - h * .18f), w * .008f, StrokeCap.Round)
            drawLine(vein.copy(alpha = .45f), Offset(x, y), Offset(x + w * .2f, y + h * .015f), w * .008f, StrokeCap.Round)
        }
        drawLine(leaf, Offset(w * .12f, h * .95f), Offset(w * .28f, h * .76f), w * .025f, StrokeCap.Round)
        val d = w * .12f
        listOf(Offset(0f, 0f), Offset(w, 0f), Offset(0f, h), Offset(w, h)).forEach { corner ->
            val sx = if (corner.x == 0f) 1 else -1
            val sy = if (corner.y == 0f) 1 else -1
            drawLine(outline, corner, Offset(corner.x + d * sx, corner.y), 2.dp.toPx())
            drawLine(outline, corner, Offset(corner.x, corner.y + d * sy), 2.dp.toPx())
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary)
}

@Composable
fun CareSteps(steps: List<String>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        steps.forEachIndexed { index, step ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("${index + 1}".padStart(2, '0'), style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(24.dp))
                Text(step, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}
