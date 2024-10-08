package id.anison.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import id.anison.common.AndroidLogcatLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ConnectivityRepository(
    context: Context,
    private val log: AndroidLogcatLogger,
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected//.asStateFlow()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: android.net.Network) {
                        _isConnected.value = true
                        log.e(TAG, "ONLINE")
                    }

                    override fun onLost(network: android.net.Network) {
                        _isConnected.value = false
                        log.e(TAG, "OFFLINE")
                    }
                }
            )
        }
    }
    companion object {
        const val TAG = "InternetConnectionREPO"
    }
}