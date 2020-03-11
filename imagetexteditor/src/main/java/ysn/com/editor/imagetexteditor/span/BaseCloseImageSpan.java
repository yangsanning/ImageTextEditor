package ysn.com.editor.imagetexteditor.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;
import android.view.View;

/**
 * @Author yangsanning
 * @ClassName BaseCloseImageSpan
 * @Description 带有关闭按钮的 ImageSpan, 请继承使用
 * @Date 2020/3/9
 * @History 2020/3/9 author: description:
 */
public abstract class BaseCloseImageSpan extends ImageSpan implements IEditorSpan {

    /**
     * 点击按钮的有效范围
     */
    private Rect closeRect;
    private boolean isInit;
    private boolean isSelect;

    /**
     * 属性
     */
    private Config config = new Config();
    private OnImageSpanEventListener onImageSpanEventListener;

    public BaseCloseImageSpan(@NonNull Bitmap b) {
        super(b);
    }

    public BaseCloseImageSpan(@NonNull Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public BaseCloseImageSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        super(context, bitmap);
    }

    public BaseCloseImageSpan(@NonNull Context context, @NonNull Bitmap bitmap, int verticalAlignment) {
        super(context, bitmap, verticalAlignment);
    }

    public BaseCloseImageSpan(@NonNull Drawable drawable) {
        super(drawable);
    }

    public BaseCloseImageSpan(@NonNull Drawable drawable, int verticalAlignment) {
        super(drawable, verticalAlignment);
    }

    public BaseCloseImageSpan(@NonNull Drawable drawable, @NonNull String source) {
        super(drawable, source);
    }

    public BaseCloseImageSpan(@NonNull Drawable drawable, @NonNull String source, int verticalAlignment) {
        super(drawable, source, verticalAlignment);
    }

    public BaseCloseImageSpan(@NonNull Context context, @NonNull Uri uri) {
        super(context, uri);
    }

    public BaseCloseImageSpan(@NonNull Context context, @NonNull Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public BaseCloseImageSpan(@NonNull Context context, int resourceId) {
        super(context, resourceId);
    }

    public BaseCloseImageSpan(@NonNull Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    /**
     * 绘制关闭按钮
     *
     * @param x 基线的横坐标（以 TextView 左上角为坐标原点）
     * @param y 基线的纵坐标（以 TextView 左上角为坐标原点）
     */
    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        // 图片居中显示
        canvas.save();
        Drawable drawable = getDrawable();
        Rect drawableRect = drawable.getBounds();
        // 余距 = 文本高度 - 图片高度除2
        int space = ((bottom - top) - drawableRect.bottom) / 2;
        //  + top(换行情况)
        int transY = space + top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();

        // 绘制关闭按钮
        Bitmap closeIconBitmap = getCloseIcon();
        if (isInit && isSelect && closeIconBitmap != null) {
            float closeBitmapLeft = x + drawableRect.right - closeIconBitmap.getWidth() - getCloseIconMarginRight();
            float closeBitmapTop = y - drawableRect.bottom + getCloseIconMarginTop();

            closeRect = new Rect((int) closeBitmapLeft, (int) closeBitmapTop,
                    ((int) closeBitmapLeft + closeIconBitmap.getWidth()), ((int) closeBitmapTop + closeIconBitmap.getHeight()));

            canvas.drawBitmap(closeIconBitmap, closeBitmapLeft, closeBitmapTop, paint);
        } else {
            closeRect = null;
        }

        // 配置参数
        config.x = (int) x;
        config.y = y + space;
        config.width = drawableRect.right;
        config.height = drawableRect.bottom;
        config.isSelect = isSelect;
        if (onImageSpanEventListener != null) {
            onImageSpanEventListener.onConfig(config);
        }

        isInit = true;
    }

    /**
     * 获取关闭按钮图标
     *
     * @return 关闭按钮图标 Bitmap
     */
    protected abstract Bitmap getCloseIcon();

    /**
     * 获取关闭图标的上边距
     *
     * @return 关闭图标的上边距
     */
    protected abstract float getCloseIconMarginTop();

    /**
     * 获取关闭图标的右边距
     *
     * @return 关闭图标的右边距
     */
    protected abstract float getCloseIconMarginRight();

    /**
     * 提供给{@link ysn.com.editor.imagetexteditor.component.ClickableMovementMethod}使用的点击事件
     * 这里进行不同点击事件的回调处理
     */
    public void onClick(View view, int x, int y, BaseCloseImageSpan closeImageSpan, boolean isDown) {
        if (onImageSpanEventListener == null) {
            return;
        }
        if (closeRect != null && closeRect.contains(x, y)) {
            onImageSpanEventListener.onClose(this);
        } else if (isDown) {
            onImageSpanEventListener.onImageDown(closeImageSpan);
        } else {
            onImageSpanEventListener.onImageUp(closeImageSpan);
        }
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setOnImageSpanEventListener(OnImageSpanEventListener onImageSpanEventListener) {
        this.onImageSpanEventListener = onImageSpanEventListener;
    }

    public interface OnImageSpanEventListener {

        /**
         * 点击图片-按下
         */
        void onImageDown(BaseCloseImageSpan closeImageSpan);

        /**
         * 点击图片-抬起
         */
        void onImageUp(BaseCloseImageSpan closeImageSpan);

        /**
         * 点击关闭按钮
         */
        void onClose(BaseCloseImageSpan closeImageSpan);

        /**
         * 图片的左下角坐标
         */
        void onConfig(Config config);
    }

    /**
     * 参数
     */
    public static class Config {

        /**
         * 图片左下角x坐标
         */
        public int x;

        /**
         * 图片左下角xy坐标
         */
        public int y;

        /**
         * 图片宽
         */
        public int width;

        /**
         * 图片高
         */
        public int height;

        /**
         * 是否选中
         */
        public boolean isSelect;
    }
}
