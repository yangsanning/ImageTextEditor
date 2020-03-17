package ysn.com.editor.imagetexteditor.span;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;
import android.view.View;

import ysn.com.editor.imagetexteditor.component.EditorMovementMethod;

/**
 * @Author yangsanning
 * @ClassName PhotoSpan
 * @Description 图片Span, 带有关闭按钮的 ImageSpan
 * @Date 2020/3/9
 * @History 2020/3/9 author: description:
 */
public class PhotoSpan extends ImageSpan implements IEditorSpan {

    /**
     * closeIconBitmap: 关闭按钮 Bitmap
     * closeIconMarginTop: 关闭按钮上边距
     * closeIconMarginRight: 关闭按钮右边距
     * imagePath: 图片路径
     */
    private final Bitmap deleteIconBitmap;
    private int deleteIconMarginTop;
    private int deleteIconMarginRight;
    private String imagePath;
    private String showText = "[图片]";

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
    private OnPhotoSpanEventListener onPhotoSpanEventListener;

    /**
     * @param drawable
     * @param imagePath            图片路径
     * @param deleteIconBitmap      删除按钮 Bitmap
     * @param deleteIconMarginTop   删除按钮上边距
     * @param deleteIconMarginRight 删除按钮右边距
     */
    public PhotoSpan(@NonNull Drawable drawable, String imagePath, Bitmap deleteIconBitmap, int deleteIconMarginTop, int deleteIconMarginRight) {
        super(drawable);
        this.deleteIconBitmap = deleteIconBitmap;
        this.imagePath = imagePath;
        this.deleteIconMarginTop = deleteIconMarginTop;
        this.deleteIconMarginRight = deleteIconMarginRight;
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
        if (isInit && isSelect && deleteIconBitmap != null) {
            float closeBitmapLeft = x + drawableRect.right - deleteIconBitmap.getWidth() - deleteIconMarginRight;
            float closeBitmapTop = y - drawableRect.bottom + deleteIconMarginTop;

            closeRect = new Rect((int) closeBitmapLeft, (int) closeBitmapTop,
                    ((int) closeBitmapLeft + deleteIconBitmap.getWidth()), ((int) closeBitmapTop + deleteIconBitmap.getHeight()));

            canvas.drawBitmap(deleteIconBitmap, closeBitmapLeft, closeBitmapTop, paint);
        } else {
            closeRect = null;
        }

        // 配置参数
        config.x = (int) x;
        config.y = y + space;
        config.width = drawableRect.right;
        config.height = drawableRect.bottom;
        config.isSelect = isSelect;
        if (onPhotoSpanEventListener != null) {
            onPhotoSpanEventListener.onConfig(config);
        }

        isInit = true;
    }

    @Override
    public String getStartTag() {
        return "<image>";
    }

    @Override
    public String getEndTag() {
        return "</image>";
    }

    @Override
    public String getShowText() {
        return showText;
    }

    @Override
    public int getShowTextLength() {
        return getShowText().length();
    }

    @Override
    public String getResult() {
        return getStartTag() + imagePath + getEndTag();
    }

    /**
     * 提供给{@link EditorMovementMethod}使用的点击事件
     * 这里进行不同点击事件的回调处理
     */
    @Override
    public void onClick(View view, int x, int y, IEditorSpan photoSpan, boolean isDown) {
        if (onPhotoSpanEventListener == null) {
            return;
        }
        if (closeRect != null && closeRect.contains(x, y)) {
            onPhotoSpanEventListener.onClickDelete(this);
        } else if (isDown) {
            onPhotoSpanEventListener.onImageDown((PhotoSpan) photoSpan);
        } else {
            onPhotoSpanEventListener.onImageUp((PhotoSpan) photoSpan);
        }
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setOnPhotoSpanEventListener(OnPhotoSpanEventListener onPhotoSpanEventListener) {
        this.onPhotoSpanEventListener = onPhotoSpanEventListener;
    }

    public interface OnPhotoSpanEventListener {

        /**
         * 点击图片-按下
         */
        void onImageDown(PhotoSpan photoSpan);

        /**
         * 点击图片-抬起
         */
        void onImageUp(PhotoSpan photoSpan);

        /**
         * 点击关闭按钮
         */
        void onClickDelete(PhotoSpan photoSpan);

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
