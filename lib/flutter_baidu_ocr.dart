import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBaiduOcr {
  static const MethodChannel _channel =
      const MethodChannel('flutter_baidu_ocr');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<String> intSdk() async {
    final String res = await _channel.invokeMethod('init');
    return res;
  }
}
