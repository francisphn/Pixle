package app.pixle.ui.composable.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.TextmojiSize
import app.pixle.ui.theme.Manrope

@Composable
fun NoWinningPhoto() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .size(150.dp),
        verticalArrangement = Arrangement.spacedBy(
            8.dp, Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RandomTextmojiMessage(
            message = "No winning photo, time to go outside!",
            size = TextmojiSize.SMALL
        )
    }
}