package com.agrihub.platform.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agrihub.platform.android.ui.theme.*

@Composable
fun AgriHubButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: String = "primary"
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when(variant) {
                "secondary" -> SurfaceWhite
                "danger" -> ErrorRed
                else -> ForestGreen
            },
            contentColor = when(variant) {
                "secondary" -> ForestGreen
                else -> TextOnPrimary
            }
        )
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    change: String? = null,
    color: Color = ForestGreen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = SlateGrey, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            change?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, fontSize = 12.sp, color = if(it.startsWith("+")) SuccessGreen else ErrorRed)
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    icon: String,
    color: Color = ForestGreen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 14.sp, color = Charcoal, fontWeight = FontWeight.Medium)
                Text(value, fontSize = 18.sp, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AlertBanner(
    message: String,
    severity: String = "INFO",
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    val (bgColor, textColor) = when(severity) {
        "HIGH" -> ErrorRed.copy(alpha = 0.1f) to ErrorRed
        "MEDIUM" -> WarningOrange.copy(alpha = 0.1f) to WarningOrange
        else -> InfoBlue.copy(alpha = 0.1f) to InfoBlue
    }
    
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(message, Modifier.weight(1f), fontSize = 14.sp, color = textColor)
            action?.let {
                TextButton(onClick = { onAction?.invoke() }) {
                    Text(it, color = textColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Charcoal)
        action?.let {
            TextButton(onClick = { onAction?.invoke() }) { Text(it, color = ForestGreen) }
        }
    }
}

@Composable
fun DataTable(
    columns: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                columns.forEach { col ->
                    Text(col, Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateGrey)
                }
            }
            Divider()
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    row.forEach { cell ->
                        Text(cell, Modifier.weight(1f), fontSize = 14.sp, color = Charcoal)
                    }
                }
                Divider(color = OffWhite)
            }
        }
    }
}
