package ysn.com.editor.phototexteditor.span;

import android.view.View;

import ysn.com.editor.phototexteditor.component.EditorMovementMethod;

/**
 * @Author yangsanning
 * @ClassName IEditSpan
 * @Description 编辑器Span需要继承的接口
 * @Date 2020/3/5
 * @History 2020/3/5 author: description:
 */
public interface IEditorSpan {

    /**
     * .获取标签头
     *
     * @return 标签头
     */
    String getStartTag();

    /**
     * 获取标签尾
     *
     * @return 标签尾
     */
    String getEndTag();

    /**
     * 获取在编辑器展示的文本
     *
     * @return 在编辑器展示的文本
     */
    String getShowText();

    /**
     * 获取在编辑器展示的文本长度
     *
     * @return 在编辑器展示的文本长度
     */
    int getShowTextLength();

    /**
     * 获取带有标签的文本（即编辑后的结果）
     *
     * @return 带有标签的文本（即编辑后的结果）
     */
    String getResult();

    /**
     * span 的点击事件统一走自定义
     *
     * @param view        控件
     * @param x           x坐标
     * @param y           y坐标
     * @param iEditorSpan 编辑器Span需要继承的接口
     * @param isDown      是否是down事件( {@link EditorMovementMethod} 仅回调down up 事件)
     */
    void onClick(View view, int x, int y, IEditorSpan iEditorSpan, boolean isDown);
}
