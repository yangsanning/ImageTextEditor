package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.graphics.Point;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Method;

import ysn.com.editor.imagetexteditor.component.ClickableMovementMethod;
import ysn.com.editor.imagetexteditor.component.EditTextWithScrollView;
import ysn.com.editor.imagetexteditor.span.BaseCloseImageSpan;
import ysn.com.editor.imagetexteditor.span.IEditorSpan;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;
import ysn.com.editor.imagetexteditor.utils.LogUtils;
import ysn.com.editor.imagetexteditor.utils.SpanUtils;

/**
 * @Author yangsanning
 * @ClassName ImageTextEditor
 * @Description 一句话概括作用
 * @Date 2020/2/26
 * @History 2020/2/26 author: description:
 */
public class ImageTextEditor extends EditTextWithScrollView implements BaseCloseImageSpan.OnImageSpanEventListener {

    private static final String STRING_LINE_FEED = "\n";

    private boolean isDelete;
    private int selStart, selEnd;
    private BaseCloseImageSpan lastCloseImageSpan;
    private OnDrawablePointListener onDrawablePointListener;

    public ImageTextEditor(Context context) {
        this(context, null);
    }

    public ImageTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    @Override
    public void setSelection(int index) {
        super.setSelection(index);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (isDelete) {
            isDelete = false;
            setSelection(this.selStart);
            return;
        }
        this.selStart = selStart;
        this.selEnd = selEnd;

        LogUtils.d("onSelectionChanged " + " selStart: " + selStart + " selEnd: " + selEnd);

        Editable text = getText();

        // 处理 IEditorSpan 的焦点事件
        if (delaEditorSpanSelection(text)) {
            return;
        }

        // 处理 EditorImageSpan 选中与非选中状态
        dealCloseImageSpanSelection(text);

        super.onSelectionChanged(selStart, selEnd);
    }

    /**
     * 处理删除事件
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.d("dispatchKeyEvent");
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() != KeyEvent.ACTION_UP) {
            Editable text = getText();
            if (!TextUtils.isEmpty(text)) {
                if (selStart == selEnd) {
                    BaseCloseImageSpan[] closeImageSpans = SpanUtils.getCloseImageSpans(text, (selStart - 2), selStart);
                    if (closeImageSpans.length > 0) {
                        selectImageSpan(text, closeImageSpans[0]);
                        hideSoftInput();
                        return true;
                    }

                    IEditorSpan[] editorSpans = SpanUtils.getEditorSpans(text, (selStart - 1), selStart);
                    if (editorSpans.length > 0) {
                        IEditorSpan editorSpan = editorSpans[0];
                        selStart = text.getSpanStart(editorSpan);
                        isDelete = true;
                        SpannableStringBuilder style = new SpannableStringBuilder(getText());
                        SpannableStringBuilder newStyle = new SpannableStringBuilder();
                        newStyle.append(style.subSequence(0, getText().getSpanStart(editorSpan)));
                        newStyle.append(style.subSequence(getSpanEnd(text, editorSpan), getText().length()));
                        setText(newStyle);
                        return true;
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                enableFocusable();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 点击{@link BaseCloseImageSpan#onClick(View, int, int, BaseCloseImageSpan, boolean)}图片-按下
     */
    @Override
    public void onImageDown(BaseCloseImageSpan closeImageSpan) {
        setInputVisible(bFalse());
    }

    /**
     * 点击{@link BaseCloseImageSpan#onClick(View, int, int, BaseCloseImageSpan, boolean)}图片-抬起
     */
    @Override
    public void onImageUp(BaseCloseImageSpan closeImageSpan) {
        unSelectImageSpan(getText());
        selectImageSpan(getText(), closeImageSpan);
        hideSoftInput();
    }

    /**
     * 点击{@link BaseCloseImageSpan#onClick(View, int, int, BaseCloseImageSpan, boolean)}关闭按钮
     */
    @Override
    public void onClose(final BaseCloseImageSpan closeImageSpan) {
        lastCloseImageSpan = null;
        Editable text = getText();
        int spanEnd = getSpanEnd(text, closeImageSpan);
        text.removeSpan(closeImageSpan);
        text.replace(spanEnd - closeImageSpan.getShowTextLength(), spanEnd, "");
        setText(text);
        setSelection(Math.min(spanEnd, text.length()));
    }

    @Override
    public void onDrawablePoint(Point drawablePaint) {
        if (onDrawablePointListener != null) {
            drawablePaint.y += getTop();
            onDrawablePointListener.onDrawablePoint(drawablePaint);
        }
    }

    /****************************************************  私有方法  **********************************************/

    /**
     * 处理 IEditorSpan 的焦点事件
     *
     * @return true 为时间已处理, false 时间未处理
     */
    private boolean delaEditorSpanSelection(Editable text) {
        IEditorSpan[] editorSpans = SpanUtils.getEditorSpans(text);
        for (IEditorSpan editorSpan : editorSpans) {
            int spanStart = getSpanStart(text, editorSpan);
            int spanEnd = getSpanEnd(text, editorSpan);

            if (selStart == selEnd) {
                if (selStart > spanStart && selStart < spanEnd) {
                    // 如果点击偏前面则移动焦点移动到前面, 反之则移动到后面
                    setSelection(((selStart - spanStart) > (spanEnd - selStart)) ? spanEnd : spanStart);
                    return true;
                }
            } else if (selEnd > spanStart && selEnd < spanEnd) {
                if (selStart < spanStart) {
                    setSelection(selStart, spanStart);
                } else {
                    setSelection(spanStart, spanEnd);
                }
                return true;
            } else if (selStart > spanStart && selStart < spanEnd) {
                setSelection(spanStart, Math.max(selEnd, spanEnd));
                return true;
            }
        }
        return false;
    }

    /**
     * 处理{@link BaseCloseImageSpan} 选中与非选中状态
     */
    private void dealCloseImageSpanSelection(Editable text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        BaseCloseImageSpan[] closeImageSpans = SpanUtils.getCloseImageSpans(text, (selStart - 1), selStart);
        if (closeImageSpans.length > 0) {
            selectImageSpan(text, closeImageSpans[0]);
        } else {
            unSelectImageSpan(text);
        }
    }

    private void selectImageSpan(Editable text, BaseCloseImageSpan closeImageSpan) {
        lastCloseImageSpan = closeImageSpan;
        lastCloseImageSpan.setSelect(bTrue());
        updateLastImageSpan(text);
    }

    private void unSelectImageSpan(Editable text) {
        if (lastCloseImageSpan == null) {
            return;
        }
        lastCloseImageSpan.setSelect(bFalse());
        updateLastImageSpan(text);
        lastCloseImageSpan = null;
    }

    /**
     * 切换imageSpan
     */
    private void updateLastImageSpan(Editable text) {
        int spanStart = text.getSpanStart(lastCloseImageSpan);
        int spanEnd = text.getSpanEnd(lastCloseImageSpan);
        if (spanStart >= 0 || spanEnd >= 0) {
            text.setSpan(lastCloseImageSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 启用焦点
     */
    private void enableFocusable() {
        setFocusable(bTrue());
        setFocusableInTouchMode(bTrue());
        setCursorVisible(bTrue());
        setInputVisible(bTrue());
    }

    /**
     * 显示光标时是否弹起键盘
     */
    private void setInputVisible(Boolean visible) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(this, visible);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    /**
     * 隐藏键盘并禁用焦点
     */
    private void hideSoftInput() {
        setFocusable(bFalse());
        setFocusableInTouchMode(bFalse());
        setCursorVisible(bFalse());
        DeviceUtils.hideSoftInput(getContext(), this);
    }

    private SpannableStringBuilder getStyle() {
        return new SpannableStringBuilder(getText());
    }

    private int getSpanStart(Editable text, IEditorSpan span) {
        return text.getSpanStart(span);
    }

    private int getSpanEnd(Editable text, IEditorSpan span) {
        return text.getSpanEnd(span);
    }

    private boolean bFalse() {
        return Boolean.FALSE;
    }

    private boolean bTrue() {
        return Boolean.TRUE;
    }

    /****************************************************  公开方法  **********************************************/

    /**
     * 添加图片 ImageSpan
     * 注意: 这里有换行操作
     */
    public BaseCloseImageSpan addImage(BaseCloseImageSpan closeImageSpan) {
        if (!hasFocus()) {
            return null;
        }
        if (closeImageSpan != null) {
            closeImageSpan.setOnImageSpanEventListener(this);
            SpannableStringBuilder style = getStyle();
            boolean isNeedLineFeed = selStart - 1 > 0 && !String.valueOf(style.charAt(selStart - 1)).equals(STRING_LINE_FEED);
            style.insert(selStart, isNeedLineFeed ? STRING_LINE_FEED : "");
            int start = selStart + (isNeedLineFeed ? STRING_LINE_FEED.length() : 0);
            int end = start + closeImageSpan.getShowTextLength();
            style.insert(start, closeImageSpan.getShowText());
            style.setSpan(closeImageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.insert(end, STRING_LINE_FEED);
            setText(style);
            setSelection(end + 1);

            setMovementMethod(ClickableMovementMethod.get());
        }
        return closeImageSpan;
    }

    /**
     * 添加自定义 Span
     *
     * @param editorSpan 实现了{@link IEditorSpan} 的Span
     */
    public void addEditorSpan(IEditorSpan editorSpan) {
        if (!hasFocus()) {
            return;
        }
        if (editorSpan instanceof BaseCloseImageSpan) {
            addImage((BaseCloseImageSpan) editorSpan);
            return;
        }
        int selEnd = selStart + editorSpan.getShowTextLength();
        SpannableStringBuilder style = getStyle();
        String showText = editorSpan.getShowText();
        style.insert(selStart, showText);
        style.setSpan(editorSpan, selStart, selEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(style);
        setSelection(selEnd);
    }

    /**
     * 获取最终的编辑结果
     */
    public String getEditTexts() {
        return SpanUtils.getEditTexts(getText());
    }

    public void setOnDrawablePointListener(OnDrawablePointListener onDrawablePointListener) {
        this.onDrawablePointListener = onDrawablePointListener;
    }

    /**
     * 返回被点击的{@link BaseCloseImageSpan}的左下角坐标
     */
    public interface OnDrawablePointListener {

        /**
         * {@link BaseCloseImageSpan}图片的左下角坐标
         */
        void onDrawablePoint(Point drawablePoint);
    }
}