package global.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import global.common.ConnectionState.Companion.of

/** Connection state of meant for [NetworkCapabilities]
 * @see of */
data class ConnectionState(
    val wifi: Boolean,
    val cellular: Boolean,
    val ethernet: Boolean,
    val bluetooth: Boolean,
    val vpn: Boolean,
    val wifiAware: Boolean,
    /** This is null on SDK<31 */
    val usb: Boolean?,
    /** This is null on SDK<27 */
    val lowpan: Boolean?,
) {
    companion object {
        /** Generate current [ConnectionState] from [context] */
        fun of(context: Context): ConnectionState? {
            val capabilities = context.networkCapabilities ?: return null
            return of(capabilities)
        }

        /** Generate current [ConnectionState] from [NetworkCapabilities] */
        fun of(capabilities: NetworkCapabilities): ConnectionState {
            return ConnectionState(
                wifi = capabilities.isConnectedWifi,
                cellular = capabilities.isConnectedCellular,
                ethernet = capabilities.isConnectedEthernet,
                bluetooth = capabilities.isConnectedBluetooth,
                vpn = capabilities.isConnectedVPN,
                wifiAware = capabilities.isConnectedWifiAware,
                usb = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    capabilities.isConnectedUSB else null,
                lowpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
                    capabilities.isConnectedLowpan else null
            )
        }
    }
}

private val Context.connectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
private val Context.networkCapabilities: NetworkCapabilities?
    get() {
        val m = connectivityManager
        return m.getNetworkCapabilities(m.activeNetwork)
    }
private val NetworkCapabilities.isConnectedWifi get() = hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
private val NetworkCapabilities.isConnectedCellular get() = hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
private val NetworkCapabilities.isConnectedEthernet get() = hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
private val NetworkCapabilities.isConnectedBluetooth get() = hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
private val NetworkCapabilities.isConnectedVPN get() = hasTransport(NetworkCapabilities.TRANSPORT_VPN)
private val NetworkCapabilities.isConnectedWifiAware get() = hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)

@get:RequiresApi(Build.VERSION_CODES.S)
private val NetworkCapabilities.isConnectedUSB get() = hasTransport(NetworkCapabilities.TRANSPORT_USB)

@get:RequiresApi(Build.VERSION_CODES.O_MR1)
private val NetworkCapabilities.isConnectedLowpan get() = hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)