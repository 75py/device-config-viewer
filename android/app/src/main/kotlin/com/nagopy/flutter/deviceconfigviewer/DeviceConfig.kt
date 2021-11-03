package com.nagopy.flutter.deviceconfigviewer

import kotlinx.serialization.Serializable

@Serializable
data class DeviceConfigCategory(val name: String, val configs: List<DeviceConfig>)

@Serializable
data class DeviceConfig(val name: String, val value: String)
