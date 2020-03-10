package ysn.com.demo.imagetexteditor.span;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import ysn.com.editor.imagetexteditor.span.BaseCloseImageSpan;
import ysn.com.editor.imagetexteditor.span.IEditorSpan;

/**
 * @Author yangsanning
 * @ClassName EditorImageSpan
 * @Description 编辑器专用 ImageSpan
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */
public class EditorImageSpan extends BaseCloseImageSpan implements IEditorSpan {

    private final Bitmap closeBitmap;
    private String imagePath;

    private String showText = "[图片]";

    /**
     *
     * @param drawable
     * @param closeBitmap 关闭按钮 Bitmap
     * @param imagePath 图片路径
     */
    public EditorImageSpan(@NonNull Drawable drawable, Bitmap closeBitmap, String imagePath) {
        super(drawable);
        this.closeBitmap = closeBitmap;
        this.imagePath = imagePath;
    }

    @Override
    protected Bitmap getCloseIcon() {
        return closeBitmap;
    }

    @Override
    protected float getCloseIconMarginTop() {
        return 40;
    }

    @Override
    protected float getCloseIconMarginRight() {
        return 40;
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
        return showText.length();
    }

    @Override
    public String getResult() {
        return getStartTag() + imagePath + getEndTag();
    }
}