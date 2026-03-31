package dev.serge.mightymaps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import javax.annotation.meta.When

@Composable
fun CircleCluster(
    color: Color,
    text: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color,
        contentColor = Color.White,
        border = BorderStroke(3.dp,Color.Green)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }

}