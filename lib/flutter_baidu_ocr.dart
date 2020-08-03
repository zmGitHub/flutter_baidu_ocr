import 'dart:async';
import 'dart:convert';

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

  // 普通文字识别
  static Future<Map<String, dynamic>> ocrText() async {
    final String jsonStr = await _channel.invokeMethod('textOCR');
    final Map<String, dynamic> res = json.decode(jsonStr);
    return res;
  }

  // 身份证正面识别/身份证反面识别
  static Future<Map<String, dynamic>> idcardOCR() async {
    final String jsonStr = await _channel.invokeMethod('idcardOCR');
    final Map<String, dynamic> res = json.decode(jsonStr);
    return res;
  }
  
}
