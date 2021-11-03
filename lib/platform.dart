import 'dart:convert';

import 'package:deviceconfigviewer/entity.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:universal_platform/universal_platform.dart';
import 'app_native.dart' if (dart.library.js) 'web_native.dart';

class MyPlatform {
  static const channel = MethodChannel('com.nagopy.flutter/deviceconfigviewer');

  static Future<List<DeviceConfigCategory>> getDeviceConfigs() async {
    String json;
    if (UniversalPlatform.isWeb) {
      json = getWebDeviceConfigs();
    } else {
      json = await channel.invokeMethod("getDeviceConfigs");
    }

    List decoded = jsonDecode(json);
    return decoded.map((e) => DeviceConfigCategory.fromJson(e)).toList();
  }

  static final deviceConfigsState =
      FutureProvider<List<DeviceConfigCategory>>((ref) async {
    //await Future.delayed(Duration(seconds: 5));
    return getDeviceConfigs();
  });
}
