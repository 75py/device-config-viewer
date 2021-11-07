package com.nagopy.flutter.deviceconfigviewer

import android.app.Application
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

class DeviceConfigViewModel(application: Application) : AndroidViewModel(application) {

    fun load(): List<DeviceConfigCategory> = runBlocking {
        val deferreds = listOf(
            async { loadBuild() },
            async { loadWiFi() },
            async { loadPropByShall() },
            async { loadGlobalSettings() }
        )
        val res = deferreds.awaitAll()
        return@runBlocking listOf(
            DeviceConfigCategory(name = "android.os.Build", configs = res[0]),
            DeviceConfigCategory(name = "android.net.wifi.WifiManager", configs = res[1]),
            DeviceConfigCategory(name = "getprop", configs = res[2]),
            DeviceConfigCategory(name = "android.provider.Settings.Global", configs = res[3])
        )
    }

    private suspend fun loadBuild(): List<DeviceConfig> = withContext(Dispatchers.Default) {
        return@withContext Build::class.java.fields.map { field ->
            DeviceConfig(
                field.name,
                field.get(null)?.let { v ->
                    when (v) {
                        is Array<*> -> v.joinToString(
                            prefix = "[ ",
                            postfix = " ]"
                        ) { it.toString() }
                        else -> v.toString()
                    }
                } ?: ""
            )
        }
    }

    private suspend fun loadWiFi(): List<DeviceConfig> = withContext(Dispatchers.Default) {
        val wifiManager = getApplication<Application>().getSystemService(WifiManager::class.java)
        return@withContext WifiManager::class.java.methods.filter { method ->
            (method.name.startsWith("get") || method.name.startsWith("is"))
                    && method.parameterCount == 0
        }.map { method ->
            DeviceConfig(
                method.name,
                try {
                    method.invoke(wifiManager)?.toString()
                } catch (t: Throwable) {
                    t.toString()
                } ?: ""
            )
        }
    }

    private suspend fun loadPropByShall(): List<DeviceConfig> = withContext(Dispatchers.Default) {
        val regex = """\[(.+)]: \[(.*)]""".toRegex()
        val process = Runtime.getRuntime().exec("getprop")
        return@withContext BufferedReader(InputStreamReader(process.inputStream)).useLines { lines ->
            lines.map { line ->
                val result = regex.matchEntire(line)
                if (result == null) {
                    DeviceConfig(line, "")
                } else {
                    DeviceConfig(result.groupValues[1], result.groupValues[2])
                }
            }.toList()
        }
    }

    private suspend fun loadGlobalSettings(): List<DeviceConfig> =
        withContext(Dispatchers.Default) {
            val cr = getApplication<Application>().contentResolver
            return@withContext Settings.Global::class.java.fields.filter { it.type == String::class.java }
                .filter { it.name == it.name.uppercase() }
                .filterNot { it.name == "NAME" || it.name == "VALUE" || it.name == "_COUNT" || it.name == "_ID" }
                .mapNotNull { it.get(null)?.toString() }
                .map {
                    val value = Settings.Global.getString(cr, it)
                    DeviceConfig(it, value ?: "")
                }
        }


}
