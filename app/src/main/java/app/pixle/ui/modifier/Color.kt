package app.pixle.ui.modifier

import androidx.compose.ui.graphics.Color

fun Color.opacity(alpha: Float): Color {
    return Color(
        red = this.red,
        green = this.green,
        blue = this.blue,
        alpha = alpha
    )
}
