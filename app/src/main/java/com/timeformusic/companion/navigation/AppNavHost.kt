package com.timeformusic.companion.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.timeformusic.companion.pages.HomePage
import com.timeformusic.companion.pages.QRPage
import com.timeformusic.companion.R
import com.timeformusic.companion.pages.PlayingPage
import com.timeformusic.companion.viewModels.SpotifyViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val spotifyViewModel: SpotifyViewModel = hiltViewModel()

    Box {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
            .fillMaxSize()
            .background(color = Color(0,0,0, 51))
        )
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_PAGE,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            composable(route = Routes.HOME_PAGE) {
                HomePage(
                    spotifyViewModel,
                    onNavigateToNext = { route ->
                        navController.navigate(route)
                    }
                )
            }
            composable(route = Routes.QR_PAGE) {
                QRPage(
                    spotifyViewModel,
                    onNavigateToNext = { route ->
                        navController.navigate(route)
                    }
                )
            }
            composable(route = Routes.PLAYING_PAGE) {
                PlayingPage (
                    spotifyViewModel,
                    backToQRPage = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}