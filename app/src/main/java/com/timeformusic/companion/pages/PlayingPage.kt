package com.timeformusic.companion.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import com.timeformusic.companion.R
import com.timeformusic.companion.composables.DragIndicator
import com.timeformusic.companion.viewModels.SpotifyViewModel

@Composable
fun PlayingPage(spotifyViewModel: SpotifyViewModel, backToQRPage: () -> Unit) {
    val isPlaying by spotifyViewModel.isPlaying.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                },
                onDrag = { change, _ ->
                    change.consume()
                },
                onDragEnd = {
                    if(isPlaying) spotifyViewModel.toggleTrack()
                    backToQRPage()
                },
                onDragCancel = {
                }
            )
        }
    ){
        Image(
            modifier = Modifier.align(Alignment.Center).clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                spotifyViewModel.toggleTrack()
            },
            painter = painterResource(
                if (isPlaying) R.drawable.circle_pause_regular else R.drawable.circle_play_regular
            ),
            contentDescription = ""
        )
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            DragIndicator()
        }
    }
}