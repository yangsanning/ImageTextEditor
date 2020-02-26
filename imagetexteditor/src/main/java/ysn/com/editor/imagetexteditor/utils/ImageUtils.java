package ysn.com.editor.imagetexteditor.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * @Author yangsanning
 * @ClassName ImageUtils
 * @Description 一句话概括作用
 * @Date 2020/2/25
 * @History 2020/2/25 author: description:
 */
public class ImageUtils {

    /**
     * Drawable转换成一个Bitmap
     *
     * @param width  目标宽
     * @param height 目标高
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
