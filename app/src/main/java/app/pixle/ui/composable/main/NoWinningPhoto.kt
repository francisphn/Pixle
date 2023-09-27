package app.pixle.ui.composable.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.ui.theme.Manrope

@Composable
fun NoWinningPhoto() {
    Column(
        modifier = Modifier
            .padding(top = 28.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No winning photo",
            fontFamily = Manrope,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "You have not snapped the photo of the day yet",
            fontFamily = Manrope,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )
        Text(
            text = "Take more photos →",
            fontFamily = Manrope,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textDecoration = TextDecoration.Underline,
        )
    }
}