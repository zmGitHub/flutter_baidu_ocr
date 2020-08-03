package com.baidu.ocr.ui.util;

import android.content.Context;

import java.io.File;

public class OcrFileUtil {
    public static File getSaveFile(Context context) {
        return new File(context.getFilesDir(), "ocr_text.jpg");
    }
}
