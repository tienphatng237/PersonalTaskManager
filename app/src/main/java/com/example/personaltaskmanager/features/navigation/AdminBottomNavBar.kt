package com.example.personaltaskmanager.features.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource

@Composable
fun AdminBottomNavBar(
    current: AdminNavItem,
    onSelect: (AdminNavItem) -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AdminNavItem.values().forEach { item ->

                val selected = (item == current)

                val tint by animateColorAsState(
                    targetValue = if (selected) Color(0xFF5AE4D9) else Color.Gray
                )

                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.1f else 1f
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(item) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        tint = tint,
                        modifier = Modifier.size(28.dp).scale(scale)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = item.label, color = tint)
                }
            }
        }
    }
}
