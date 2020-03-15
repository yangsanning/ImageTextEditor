package ysn.com.editor.imagetexteditor.bean;

import android.support.annotation.DrawableRes;

/**
 * @Author yangsanning
 * @ClassName EditorConfig
 * @Description 编辑器参数
 * @Date 2020/3/15
 * @History 2020/3/15 author: description:
 */
public class EditorConfig {

    /**
     * {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 的宽
     */
    public int photoSpanWidth;

    /**
     * {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 删除按钮的资源id
     */
    @DrawableRes
    public int deleteIconRes;
    /**
     * {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 删除按钮的宽
     */
    public int deleteIconWidth;

    /**
     * {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 删除按钮的高
     */
    public int deleteIconHeight;

    /**
     * {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 删除按钮的上边距
     */
    public int deleteIconMarginTop;


    /**
     * {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 删除按钮的右边距
     */
    public int deleteIconMarginRight;
}
