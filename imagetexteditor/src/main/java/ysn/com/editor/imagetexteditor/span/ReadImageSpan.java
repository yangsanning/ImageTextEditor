package ysn.com.editor.imagetexteditor.span;

import android.graphics.Bitmap;
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

import ysn.com.editor.imagetexteditor.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName ReadImageSpan
 * @Description 展示内容专用的 ImageSpan
 * @Date 2020/3/6
 * @History 2020/3/6 author: description:
 */
public class ReadImageSpan extends ImageSpan {

    private String imageUrl;
    private int targetImageWidth;
    private TextView textView;

    private boolean icDownloadComplete;

    /**
     * @param textView 需要展示内容的 TextView, 用于刷新
     * @param loadingDrawable 加载时的占位图
     * @param imageUrl 图片链接
     * @param imageTargetWidth 图片目标宽度,
     */
    public ReadImageSpan(TextView textView, @NonNull Drawable loadingDrawable, String imageUrl, int imageTargetWidth) {
        super(loadingDrawable);
        this.imageUrl = imageUrl;
        this.targetImageWidth = imageTargetWidth;
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable() {
        if (!icDownloadComplete) {
            Glide.with(textView.getContext()).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(textView.getContext().getResources(), ImageUtils.zoom(resource, targetImageWidth));
                    bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

                    Field mDrawable;
                    Field mDrawableRef;
                    try {
                        mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
                        mDrawable.setAccessible(true);
                        mDrawable.set(ReadImageSpan.this, bitmapDrawable);

                        mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
                        mDrawableRef.setAccessible(true);
                        mDrawableRef.set(ReadImageSpan.this, null);

                        icDownloadComplete = true;
                        textView.setText(textView.getText());
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return super.getDrawable();
    }
}

