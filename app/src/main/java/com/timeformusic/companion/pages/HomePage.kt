package com.timeformusic.companion.pages

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timeformusic.companion.LoadingScreen
import com.timeformusic.companion.R
import com.timeformusic.companion.composables.Modal
import com.timeformusic.companion.navigation.Routes
import com.timeformusic.companion.viewModels.SpotifyViewModel

@Composable
fun HomePage(spotifyViewModel: SpotifyViewModel, onNavigateToNext: (String) -> Unit) {
    val isLoading by spotifyViewModel.isLoading.collectAsState()
    val isConnected by spotifyViewModel.isConnected.collectAsState()
    val isSpotifyMissingModalOpen by spotifyViewModel.isSpotifyMissingModalOpen.collectAsState()
    val activity = LocalContext.current as ComponentActivity

    LaunchedEffect(Unit) {
        spotifyViewModel.checkIfConnected()
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        Box {
            if (isSpotifyMissingModalOpen) {
                Modal(
                    "Missing Spotify app",
                    "It looks like Spotify is not installed on your device, please download the app before you can continue."
                ) {
                    spotifyViewModel.setIsSpotifyMissingModalOpen(false)
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = 80.dp,
                        bottom = 100.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(180.dp)
                    )
                    //                Text(
                    //                    stringResource(R.string.the_music_party_game),
                    //                    style = TextStyle(
                    //                        color = Color.White,
                    //                        fontWeight = FontWeight.Bold,
                    //                        fontSize = 24.sp
                    //                    )
                    //                )
                }
                Button(
                    modifier = Modifier
                        .height(55.dp)
                        .wrapContentWidth(),
                    onClick = {
                        if (isConnected) {
                            onNavigateToNext(Routes.QR_PAGE)
                        } else {
                            spotifyViewModel.setIsLoading(true)
                            spotifyViewModel.authenticate(activity)
                            spotifyViewModel.connectToSpotify({
                                println("UI")
                                spotifyViewModel.setIsLoading(false)
                                onNavigateToNext(Routes.QR_PAGE)
                            }, {
                                spotifyViewModel.setIsLoading(false)
                            })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        stringResource(if (isConnected) R.string.start_game else R.string.connect_to_spotify),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    )
                }
            }
        }
    }
}