package id.anison.amanogawa.core.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import id.anison.amanogawa.core.common.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class ConnectivityRepo(
    context: Context,
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
                        Log.e(tag = TAG) { "ONLINE" }
                    }

                    override fun onLost(network: Network) {
                        _isConnected.value = false
                        Log.e(tag = TAG) { "OFFLINE" }
                    }
                }
            )
        }
    }

    companion object {
        private const val TAG = "InternetConnectionRepo"
    }

}