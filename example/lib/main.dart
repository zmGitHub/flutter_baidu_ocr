import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_baidu_ocr/flutter_baidu_ocr.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  String _textOcr = '';
  String _idcardOcr = '';
  
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterBaiduOcr.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Text('Running on: $_platformVersion\n'),
            FlatButton(
              child: Text("初始化"),
              onPressed: () async {
                String res = await FlutterBaiduOcr.intSdk();
                print(res);
              },
            ),
            FlatButton(
              child: Text("文本ocr识别"),
              color: Colors.blue,
              textColor: Colors.white,
              disabledColor: Colors.grey,
              disabledTextColor: Colors.black,
              padding: EdgeInsets.all(8.0),
              splashColor: Colors.blueAccent,
              onPressed: () async {
                Map<String, dynamic> res = await FlutterBaiduOcr.ocrText();
                print("文本ocr识别: ${res.toString()}");
                setState(() {
                  _textOcr = res.toString();
                });
              },
            ),
            Text(_textOcr),
            Divider(),
            FlatButton(
              child: Text("身份证识别"),
              color: Colors.blue,
              textColor: Colors.white,
              disabledColor: Colors.grey,
              disabledTextColor: Colors.black,
              padding: EdgeInsets.all(8.0),
              splashColor: Colors.blueAccent,
              onPressed: () async {
                Map<String, dynamic> res = await FlutterBaiduOcr.idcardOCR();
                print("身份证识别: ${res.toString()}");
                setState(() {
                  _idcardOcr = res.toString();
                });
              },
            ),
            Text(_idcardOcr),
          ],

        ),
      ),
    );
  }
}
