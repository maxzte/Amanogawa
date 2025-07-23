package id.anison.amanogawa.core.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import id.anison.amanogawa.core.common.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConnectivityRepo(
    context: Context,
    private val log: Logger,
) {
    private val connectivityManager = context
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected.asStateFlow()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isConnected.value = true
                        log.e(TAG, "ONLINE")
                    }

                    override fun onLost(network: Network) {
                        _isConnected.value = false
                        log.e(TAG, "OFFLINE")
                    }
                }
            )
        }
    }

    companion object {
        private const val TAG = "InternetConnectionRepo"
    }

}