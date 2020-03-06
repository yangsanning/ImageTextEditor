package ysn.com.editor.imagetexteditor.span;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;
import android.view.View;

import ysn.com.editor.imagetexteditor.component.ClickableMovementMethod;

/**
 * @Author yangsanning
 * @ClassName EditorImageSpan
 * @Description 编辑器专用 ImageSpan
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */

public class EditorImageSpan extends ImageSpan implements IEditorSpan {

    /**
     * marginTop:   关闭图标的上边距
     * marginRight: 关闭图标的右边距
     */
    private float closeIconMarginTop;
    private float closeIconMarginRight;

    private Bitmap closeBitmap;
    private Rect closeRect;

    private String sourceText;
    private String imageUrl;

    private boolean isInit;
    private boolean isSelect;

    private OnCloseImageSpanClickListener onCloseImageSpanClickListener;

    public EditorImageSpan(@NonNull Drawable drawable, String imageUrl) {
        super(drawable);
        this.imageUrl = imageUrl;
    }

    @Override
    public String getStartTag() {
        return "<general>";
    }

    @Override
    public String getEndTag() {
        return "</general>";
    }

    @Override
    public String getText() {
        return sourceText == null ? (sourceText = (getStartTag() + imageUrl + getEndTag())) : sourceText;
    }

    @Override
    public int length() {
        return this.getText() == null ? 0 : sourceText.length();
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

    /**
     * 提供给{@link ClickableMovementMethod}使用的点击事件
     * 这里进行不同点击事件的回调处理
     */
    public void onClick(View view, int x, int y, EditorImageSpan imageSpan, boolean isDown) {
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
        void onImageDown(EditorImageSpan imageSpan);

        /**
         * 点击图片-抬起
         */
        void onImageUp(EditorImageSpan imageSpan);

        /**
         * 点击关闭按钮
         */
        void onClose(EditorImageSpan closeImageSpan);
    }
}

