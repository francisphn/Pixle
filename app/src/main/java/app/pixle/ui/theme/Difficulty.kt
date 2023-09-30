package app.pixle.ui.theme

import androidx.compose.ui.graphics.Color


fun rarityColour(difficulty: String): Color {
    return when (difficulty) {
        "easiest" -> Color(0xFF3B82F6)
        "easy" -> Color(0xFFA855F7)
        "medium" -> Color(0xFFE879F9)
        "hard" -> Color(0xFFF87171)
        else -> Color(0xFFF97316)
    }
}