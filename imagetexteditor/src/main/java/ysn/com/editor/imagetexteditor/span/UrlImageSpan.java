package ysn.com.editor.imagetexteditor.span;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.reflect.Field;

import ysn.com.editor.imagetexteditor.component.ClickableMovementMethod;
import ysn.com.editor.imagetexteditor.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName UrlImageSpan
 * @Description 一句话概括作用
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */

public class UrlImageSpan extends ImageSpan {

    /**
     * marginTop:   关闭图标的上边距
     * marginRight: 关闭图标的右边距
     */
    private float closeIconMarginTop;
    private float closeIconMarginRight;

    private boolean isInit;
    private boolean isSelect;

    private Bitmap closeBitmap;
    private Rect closeRect;

    private String imageUrl;
    private int imageWidth;
    private TextView textView;

    private boolean icDownload;
    private OnCloseImageSpanClickListener onCloseImageSpanClickListener;

    public UrlImageSpan(@NonNull Drawable drawable, String imageUrl, int imageWidth, TextView textView) {
        super(drawable);
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.textView = textView;
    }

    /**
     * 绘制关闭按钮
     *
     * @param x 基线的横坐标（以 TextView 左上角为坐标原点）
     * @param y 基线的纵坐标（以 TextView 左上角为坐标原点）
     */
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        if (isInit && isSelect && closeBitmap != null) {
            Rect drawableRect = getDrawable().getBounds();
            float closeBitmapLeft = x + drawableRect.right - closeBitmap.getWidth() - closeIconMarginRight;
            float closeBitmapTop = y - drawableRect.bottom + closeIconMarginTop;

            closeRect = new Rect((int) closeBitmapLeft, (int) closeBitmapTop,
                    ((int) closeBitmapLeft + closeBitmap.getWidth()), ((int) closeBitmapTop + closeBitmap.getHeight()));

            canvas.drawBitmap(closeBitmap, closeBitmapLeft, closeBitmapTop, paint);
        } else {
            closeRect = null;
        }
        isInit = true;
    }

    @Override
    public Drawable getDrawable() {
        if (!icDownload) {
            Glide.with(textView.getContext()).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(textView.getContext().getResources(), ImageUtils.zoom(resource, imageWidth));
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
     * 提供给{@link ClickableMovementMethod}使用的点击事件
     * 这里进行不同点击事件的回调处理
     */
    public void onClick(View view, int x, int y, UrlImageSpan imageSpan, boolean isDown) {
        if (onCloseImageSpanClickListener == null) {
            return;
        }

        if (closeRect != null && closeRect.contains(x, y)) {
            onCloseImageSpanClickListener.onClose(this);
        } else {
            if (isDown) {
                onCloseImageSpanClickListener.onImageDown(imageSpan);
            } else {
                onCloseImageSpanClickListener.onImageUp(imageSpan);
            }
        }
    }

    public void bindCloseBitmap(Bitmap closeBitmap, float closeIconMarginTop, float closeIconMarginRight) {
        this.closeBitmap = closeBitmap;
        this.closeIconMarginTop = closeIconMarginTop;
        this.closeIconMarginRight = closeIconMarginRight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    /**
     * 设置点击事件回调
     *
     * @param onCloseImageSpanClickListener 击事件回调
     */
    public void setOnCloseImageSpanClickListener(OnCloseImageSpanClickListener onCloseImageSpanClickListener) {
        this.onCloseImageSpanClickListener = onCloseImageSpanClickListener;
    }

    public interface OnCloseImageSpanClickListener {

        /**
         * 点击图片-按下
         */
        void onImageDown(UrlImageSpan imageSpan);

        /**
         * 点击图片-抬起
         */
        void onImageUp(UrlImageSpan imageSpan);

        /**
         * 点击关闭按钮
         */
        void onClose(UrlImageSpan closeImageSpan);
    }
}

