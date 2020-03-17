package ysn.com.editor.phototexteditor.utils;

import android.util.Log;

/**
 * @Author yangsanning
 * @ClassName LogUtils
 * @Description 日志输出
 * @Date 2020/2/28
 * @History 2020/2/28 author: description:
 */
public class LogUtils {

    public static final boolean ENABLE = Boolean.TRUE;
    public static final String TAG = "test";

    public static void d(int text) {
        d(String.valueOf(text));
    }

    public static void d(String text) {
        if (ENABLE) {
            Log.d(TAG, text);
        }
    }
}
