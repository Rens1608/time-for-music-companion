package com.timeformusic.companion.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timeformusic.companion.R

@Composable
fun DragIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(R.drawable.arrow_left_solid), "", modifier = Modifier.size(32.dp))
        Text(stringResource(R.string.drag_to_scan_the_next_song), style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold))
        Image(painterResource(R.drawable.arrow_right_solid), "",modifier = Modifier.size(32.dp))
    }
}

@Preview
@Composable
fun DragIndicatorPreview() {
    DragIndicator()
}