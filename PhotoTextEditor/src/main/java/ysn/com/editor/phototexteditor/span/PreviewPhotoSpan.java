package ysn.com.editor.phototexteditor.span;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.reflect.Field;

import ysn.com.editor.phototexteditor.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName PreviewPhotoSpan
 * @Description 预览的 ImageSpan
 * @Date 2020/3/6
 * @History 2020/3/6 author: description:
 */
public class PreviewPhotoSpan extends ImageSpan {

    private String imageUrl;
    private int targetImageWidth;
    private TextView textView;

    private boolean isDownloadComplete;

    /**
     * @param textView         需要展示内容的 TextView, 用于刷新
     * @param imageUrl         图片链接
     * @param imageTargetWidth 图片目标宽度,
     */
    public PreviewPhotoSpan(TextView textView, String imageUrl, int imageTargetWidth) {
        super((Bitmap) null);
        this.imageUrl = imageUrl;
        this.targetImageWidth = imageTargetWidth;
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable() {
        if (!isDownloadComplete) {
            if (imageUrl.startsWith("http")) {
                Glide.with(textView.getContext()).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        update(ImageUtils.zoom(resource, targetImageWidth));
                    }
                });
            } else {
                update(ImageUtils.zoom(ImageUtils.getBitmap(imageUrl), targetImageWidth));
            }
        }
        return super.getDrawable();
    }

    private void update(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(textView.getContext().getResources(), bitmap);
        bitmapDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Field mDrawable;
        Field mDrawableRef;
        try {
            mDrawable = ImageSpan.class.getDeclaredField("mDrawable");
            mDrawable.setAccessible(true);
            mDrawable.set(PreviewPhotoSpan.this, bitmapDrawable);

            mDrawableRef = DynamicDrawableSpan.class.getDeclaredField("mDrawableRef");
            mDrawableRef.setAccessible(true);
            mDrawableRef.set(PreviewPhotoSpan.this, null);

            isDownloadComplete = true;
            textView.setText(textView.getText());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}

