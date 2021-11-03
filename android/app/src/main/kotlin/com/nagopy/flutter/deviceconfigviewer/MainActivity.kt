package com.nagopy.flutter.deviceconfigviewer

import androidx.lifecycle.ViewModelProvider
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : FlutterActivity() {

    companion object {
        private const val CHANNEL = "com.nagopy.flutter/deviceconfigviewer"
        private const val METHOD_GET_LIST = "getDeviceConfigs"
    }

    private lateinit var channel: MethodChannel

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler { methodCall: MethodCall, result: MethodChannel.Result ->
            if (methodCall.method == METHOD_GET_LIST) {
                val viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    .create(DeviceConfigViewModel::class.java)
                val ret = viewModel.load()
                result.success(Json.encodeToString(ret))
            } else
                result.notImplemented()
        }
    }
}
