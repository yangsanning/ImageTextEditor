package ysn.com.editor.phototexteditor.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

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

    /**
     * 按宽度缩放图片
     *
     * @param originalBitmap 需要缩放的图片源
     * @param newWidth       需要缩放成的图片宽度
     * @return 缩放后的图片
     */
    public static Bitmap zoom(@NonNull Bitmap originalBitmap, int newWidth) {
        // 获得图片的宽高
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // 计算缩放比例
        float scale = ((float) newWidth) / width;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 得到新的图片
        return Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 根据图片路径获取 Bitmap
     */
    public static Bitmap getBitmap(String imagePath) {
        return BitmapFactory.decodeFile(imagePath, new BitmapFactory.Options());
    }
}
