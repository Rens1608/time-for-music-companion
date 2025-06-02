package com.timeformusic.companion.repositories

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import javax.inject.Singleton
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ExternalSpotifyRepository(
    @ApplicationContext private val context: Context
) {
    private fun isSpotifyInstalled(): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo("com.spotify.music", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openSpotifyApp(trackId: String) {
        val intent = Intent(Intent.ACTION_VIEW, "spotify:track:$trackId".toUri())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun playTrack(trackId: String): Boolean {
        if(isSpotifyInstalled()) {
            openSpotifyApp(trackId)
            return true
        } else {
            return false
        }
    }
}