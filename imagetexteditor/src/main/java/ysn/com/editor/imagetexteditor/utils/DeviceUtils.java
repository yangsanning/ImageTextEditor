package ysn.com.editor.imagetexteditor.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * @Author yangsanning
 * @ClassName DeviceUtils
 * @Description 设备的一些方法
 * @Date 2018/11/12
 * @History 2018/11/12 author: description:
 */
public class DeviceUtils {

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
