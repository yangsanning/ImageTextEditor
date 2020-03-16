package ysn.com.editor.imagetexteditor.bean;

import android.support.annotation.DrawableRes;

import ysn.com.editor.imagetexteditor.span.NotesSpan;
import ysn.com.editor.imagetexteditor.span.PhotoSpan;
import ysn.com.editor.imagetexteditor.span.PreviewPhotoSpan;

/**
 * @Author yangsanning
 * @ClassName EditorConfig
 * @Description 编辑器参数
 * @Date 2020/3/15
 * @History 2020/3/15 author: description:
 */
public class EditorConfig {

    /**************************************** {@link PhotoSpan} ***********************************/

    /**
     * {@link PhotoSpan} 的宽
     */
    public int photoSpanWidth;

    /**
     * {@link PhotoSpan} 删除按钮的资源id
     */
    @DrawableRes
    public int deleteIconRes;
    /**
     * {@link PhotoSpan} 删除按钮的宽
     */
    public int deleteIconWidth;

    /**
     * {@link PhotoSpan} 删除按钮的高
     */
    public int deleteIconHeight;

    /**
     * {@link PhotoSpan} 删除按钮的上边距
     */
    public int deleteIconMarginTop;

    /**
     * {@link PhotoSpan} 删除按钮的右边距
     */
    public int deleteIconMarginRight;


    /**************************************** {@link NotesSpan}  **********************************/

    /**
     * {@link NotesSpan} 的宽
     */
    public int notesSpanWidth;

    /**
     * {@link NotesSpan} 的文字颜色
     */
    public int notesSpanTextColor;

    /**
     * {@link NotesSpan} 的文字大小
     */
    public float notesSpanTextSize;

    /**
     * {@link NotesSpan} 的上边距
     */
    public int notesSpanMarginTop;

    /**
     * {@link NotesSpan} 的下边距
     */
    public int notesSpanMarginBottom;
}
