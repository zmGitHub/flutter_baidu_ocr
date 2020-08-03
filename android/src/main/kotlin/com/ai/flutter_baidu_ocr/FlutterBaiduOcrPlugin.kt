package com.ai.flutter_baidu_ocr

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.sdk.model.IDCardParams
import com.baidu.ocr.sdk.model.IDCardResult
import com.baidu.ocr.ui.camera.CameraActivity
import com.baidu.ocr.ui.util.OcrFileUtil
import com.baidu.ocr.ui.util.RecognizeService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File


/** FlutterBaiduOcrPlugin */
public class FlutterBaiduOcrPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_baidu_ocr")

    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {

    var activity: Activity?=null
    private val activityResultMap: HashMap<Int, Result> = linkedMapOf()
    private const val REQUEST_CODE_CAMERA = 102 //身份证
    private const val REQUEST_CODE_GENERAL = 105 //通用文字识别（含位置信息版）
    private const val REQUEST_CODE_GENERAL_BASIC = 106 //通用文字识别
    private const val REQUEST_CODE_ACCURATE_BASIC = 107 //通用文字识别(高精度版)
    private const val REQUEST_CODE_ACCURATE = 108 //通用文字识别（含位置信息高精度版）
    private const val REQUEST_CODE_GENERAL_ENHANCED = 109 //通用文字识别（含生僻字版）
    private const val REQUEST_CODE_GENERAL_WEBIMAGE = 110 //网络图片识别
    private const val REQUEST_CODE_BANKCARD = 111 //银行卡识别
    private const val REQUEST_CODE_VEHICLE_LICENSE = 120 //行驶证识别
    private const val REQUEST_CODE_DRIVING_LICENSE = 121 // 驾驶证识别
    private const val REQUEST_CODE_LICENSE_PLATE = 122 //车牌识别
    private const val REQUEST_CODE_BUSINESS_LICENSE = 123 //营业执照
    private const val REQUEST_CODE_RECEIPT = 124 //通用票据识别


    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_baidu_ocr")
      channel.setMethodCallHandler(FlutterBaiduOcrPlugin())
      // 监听页面数据回调
      registrar.addActivityResultListener { requestCode, resultCode, data ->
        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
          RecognizeService.recGeneralBasic(activity, OcrFileUtil.getSaveFile(activity).absolutePath
          ) { result ->
            Log.d("mrliuys", result)
            val result1: Result? = activityResultMap[REQUEST_CODE_GENERAL_BASIC]
            result1?.success(result)
          }
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {

        }
        return@addActivityResultListener true
      }
    }
  }
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
        "getPlatformVersion" -> {
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
        }
        "init" -> {
          OCR.getInstance(activity).initAccessTokenWithAkSk(object: OnResultListener<AccessToken> {
            override fun onResult(p0: AccessToken?) {
              Log.i("*******", "初始化成功")
              Log.d("INIT: ", p0?.tokenJson);
            }
            override fun onError(p0: OCRError?) {
              Log.i("!!!!!!!", "初始化失败")
              p0?.printStackTrace()
            }
          },  activity, "oWhYHquxoOWI1V4k0BgASNP5", "AiEINj1Iww46TzyTjOeo9qW50z7kz9YY")
          result.success("Android: success")
        }
        "textOCR" -> {
          // 通用文字识别
          val intent = Intent(activity, CameraActivity::class.java)
          intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, OcrFileUtil.getSaveFile(activity?.applicationContext).absolutePath)
          intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
          activity!!.startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC)
          activityResultMap[REQUEST_CODE_GENERAL_BASIC] = result
        }
        "idcardOCR" -> {
          // 身份证识别
          val intent = Intent(activity, CameraActivity::class.java)
          intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, OcrFileUtil.getSaveFile(activity?.applicationContext).absolutePath)
          intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
          activity!!.startActivityForResult(intent, REQUEST_CODE_CAMERA)
          activityResultMap[REQUEST_CODE_CAMERA] = result
        }
        else -> {
          result.notImplemented()
        }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    // 释放资源
    OCR.getInstance(activity).release()
  }

  override fun onDetachedFromActivity() {
    Log.d("Baidu OCR:", "onDetachedFromActivity")
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d("Baidu OCR:", "onAttachedToActivity")
    activity = binding.activity
    binding.addActivityResultListener { requestCode, resultCode, data ->
      // 识别成功回调，通用文字识别
      if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
        RecognizeService.recGeneralBasic(activity, OcrFileUtil.getSaveFile(activity).absolutePath
        ) { result ->
          Log.d("mrliuys", result)
          val res: Result? = activityResultMap[REQUEST_CODE_GENERAL_BASIC]
          res?.success(result)
        }
      } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
        // 获取临时存储路径
        val filePath = OcrFileUtil.getSaveFile(activity).absolutePath
        val param = IDCardParams()
        param.imageFile = File(filePath);
        // 设置身份证正反面
        param.idCardSide = IDCardParams.ID_CARD_SIDE_FRONT;
        // 设置方向检测
        param.isDetectDirection = true;
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.imageQuality = 20;
        OCR.getInstance(activity).recognizeIDCard(param, object : OnResultListener<IDCardResult?> {
          override fun onResult(result: IDCardResult?) {
            if (result != null) {
              Log.d("OCRLOG_SUCCESS:", result.jsonRes)
              val res: Result? = activityResultMap[REQUEST_CODE_CAMERA]
              res?.success(result.jsonRes)
            }
          }
          override fun onError(error: OCRError) {
            Log.d("OCRLOG_SUCCESS:", error.message)
            val res: Result? = activityResultMap[REQUEST_CODE_CAMERA]
            res?.error(error.errorCode.toString(), error.message, null)
          }
        })
      }
      return@addActivityResultListener true
    }
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }
}
