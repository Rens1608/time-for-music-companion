package com.timeformusic.companion.repositories

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val redirectUri = "http://localhost:3000/callback"
    private val clientId = "a181c05e883243428e2fce221df7f9a2"

    init {
        SpotifyAppRemote.setDebugMode(true)
        (context as Application).registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityDestroyed(activity: Activity) {
                disconnect()
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        })
    }

    fun authenticate(activity: Activity, onResult: (String?) -> Unit) {
        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        ).setScopes(arrayOf("streaming", "user-read-playback-state", "user-modify-playback-state"))
            .build()

        AuthorizationClient.openLoginActivity(activity, 1, request)
    }

    fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent?, onResult: (String?) -> Unit) {
        if (requestCode == 1) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> onResult(response.accessToken)
                AuthorizationResponse.Type.ERROR -> onResult(null)
                else -> onResult(null)
            }
        }
    }

    fun connectToSpotifyAppRemote(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                onConnected()
            }

            override fun onFailure(error: Throwable) {
                println(error)
                onError(error)
            }
        })
    }

    fun playTrack(url: String, onError: () -> Unit, onSuccess: () -> Unit) {
        try {
            val id = url.substringAfterLast('/')
            spotifyAppRemote?.playerApi?.play("spotify:track:$id")
            onSuccess()
        } catch (e: Exception) {
            println(e.message)
            onError()
        }
    }

    fun pauseTrack() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resumeTrack() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun checkIfConnected(): Boolean {
        return spotifyAppRemote?.isConnected == true
    }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}