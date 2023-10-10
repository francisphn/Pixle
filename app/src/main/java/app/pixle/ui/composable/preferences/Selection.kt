package app.pixle.ui.composable.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

@Composable
fun Selection(
    isSelected: Boolean,
    label: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.surfaceVariant.opacity(0.6f)
                else
                    MaterialTheme.colorScheme.background,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(8.dp)
            .padding(bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(28.dp),
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onBackground.opacity(0.25f)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontFamily = Manrope,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = description,
                fontFamily = Manrope,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onBackground.opacity(0.75f)
            )
        }
    }
}