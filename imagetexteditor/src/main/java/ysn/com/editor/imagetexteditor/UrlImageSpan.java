package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.reflect.Field;

/**
 * @Author yangsanning
 * @ClassName UrlImageSpan
 * @Description 一句话概括作用
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */

public class UrlImageSpan extends ImageSpan {

    private String url;
    private int imageWidth;
    private TextView textView;

    private boolean icDownload;

    public UrlImageSpan(Context context, String url, int imageWidth, TextView textView) {
        super(context, R.drawable.image);
        this.url = url;
        this.imageWidth = imageWidth;
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable() {
        if (!icDownload) {
            Glide.with(textView.getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(textView.getContext().getResources(), zoom(resource, imageWidth));
                    bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

                    Field mDrawable;
                    Field mDrawableRef;
                    try {
                        mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
                        mDrawable.setAccessible(true);
                        mDrawable.set(UrlImageSpan.this, bitmapDrawable);

                        mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
                        mDrawableRef.setAccessible(true);
                        mDrawableRef.set(UrlImageSpan.this, null);

                        icDownload = true;
                        textView.setText(textView.getText());
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return super.getDrawable();
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
}

