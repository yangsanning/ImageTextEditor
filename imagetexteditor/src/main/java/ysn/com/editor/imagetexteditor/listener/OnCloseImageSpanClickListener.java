package ysn.com.editor.imagetexteditor.listener;

import ysn.com.editor.imagetexteditor.span.CloseImageSpan;

/**
 * @Author yangsanning
 * @ClassName OnCloseImageSpanClickListener
 * @Description {@link CloseImageSpan}点击事件回调
 * @Date 2020/2/26
 * @History 2020/2/26 author: description:
 */
public interface OnCloseImageSpanClickListener {

    /**
     * 点击{@link CloseImageSpan}图片-按下
     */
    void onImageDown(CloseImageSpan imageSpan);

    /**
     * 点击{@link CloseImageSpan}图片-抬起
     */
    void onImageUp(CloseImageSpan imageSpan);

    /**
     * 点击{@link CloseImageSpan}关闭按钮
     */
    void onClose(CloseImageSpan closeImageSpan);
}
