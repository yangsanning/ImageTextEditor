package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.lang.reflect.Method;

import ysn.com.editor.imagetexteditor.component.ClickableMovementMethod;
import ysn.com.editor.imagetexteditor.component.EditTextWithScrollView;
import ysn.com.editor.imagetexteditor.span.IEditorSpan;
import ysn.com.editor.imagetexteditor.span.NotesSpan;
import ysn.com.editor.imagetexteditor.span.PhotoSpan;
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
public class ImageTextEditor extends EditTextWithScrollView implements PhotoSpan.OnPhotoSpanEventListener,
        NotesSpan.OnNotesSpanClickListener {

    private static final String STRING_LINE_FEED = "\n";

    private boolean isDelete;
    private int selStart, selEnd;
    private PhotoSpan lastPhotoSpan;
    private OnImageTextEditorEventListener onImageTextEditorEventListener;

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
        dealPhotoSpanSelection(text);

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
                    // 如果是图片, 则进行选中
                    PhotoSpan[] photoSpans;
                    NotesSpan[] notesSpans = SpanUtils.getNotesSpans(text, (selStart - 2), selStart);
                    if (notesSpans.length > 0) {
                        // 如果是注解, 则往回移动
                        int spanStart = getSpanStart(text, notesSpans[0]);
                        photoSpans = SpanUtils.getCloseImageSpans(text, spanStart - 2, spanStart);
                    } else {
                        photoSpans = SpanUtils.getCloseImageSpans(text, (selStart - 2), selStart);
                    }
                    if (photoSpans.length > 0) {
                        selectPhotoSpan(text, photoSpans[0]);
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
     * 点击{@link PhotoSpan#onClick(View, int, int, IEditorSpan, boolean)}}图片-按下
     */
    @Override
    public void onImageDown(PhotoSpan photoSpan) {
        setInputVisible(bFalse());
    }

    /**
     * 点击{@link PhotoSpan#onClick(View, int, int, IEditorSpan, boolean)}图片-抬起
     */
    @Override
    public void onImageUp(PhotoSpan photoSpan) {
        unSelectPhotoSpan(getText());
        selectPhotoSpan(getText(), photoSpan);
        hideSoftInput();
    }

    /**
     * 点击{@link PhotoSpan#onClick(View, int, int, IEditorSpan, boolean)}关闭按钮
     */
    @Override
    public void onClose(final PhotoSpan photoSpan) {
        lastPhotoSpan = null;
        Editable text = getText();
        int spanEnd = getSpanEnd(text, photoSpan);
        text.removeSpan(photoSpan);
        text.replace(spanEnd - photoSpan.getShowTextLength(), spanEnd, "");
        setText(text);
        setSelection(Math.min(spanEnd, text.length()));
    }

    @Override
    public void onConfig(PhotoSpan.Config config) {
        if (onImageTextEditorEventListener != null) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            config.y += getTop() - viewGroup.getPaddingTop();
            onImageTextEditorEventListener.onPhotoSpanConfig(config);
        }
    }

    @Override
    public void onNoteSpanClick(View view, IEditorSpan iEditorSpan) {
        unSelectPhotoSpan(getText());
        hideSoftInput();
        if (onImageTextEditorEventListener != null) {
            onImageTextEditorEventListener.onNotesSpanClick(view, iEditorSpan);
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
     * 处理{@link PhotoSpan} 选中与非选中状态
     */
    private void dealPhotoSpanSelection(Editable text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        PhotoSpan[] photoSpans = SpanUtils.getCloseImageSpans(text, (selStart - 1), selStart);
        if (photoSpans.length > 0) {
            selectPhotoSpan(text, photoSpans[0]);
        } else {
            unSelectPhotoSpan(text);
        }
    }

    private void selectPhotoSpan(Editable text, PhotoSpan photoSpan) {
        lastPhotoSpan = photoSpan;
        lastPhotoSpan.setSelect(bTrue());
        updateLastPhotoSpan(text);
    }

    private void unSelectPhotoSpan(Editable text) {
        if (lastPhotoSpan == null) {
            return;
        }
        lastPhotoSpan.setSelect(bFalse());
        updateLastPhotoSpan(text);
        lastPhotoSpan = null;
    }

    /**
     * 切换imageSpan
     */
    private void updateLastPhotoSpan(Editable text) {
        int spanStart = text.getSpanStart(lastPhotoSpan);
        int spanEnd = text.getSpanEnd(lastPhotoSpan);
        if (spanStart >= 0 || spanEnd >= 0) {
            text.setSpan(lastPhotoSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
     * 添加自定义 Span
     *
     * @param editorSpan 实现了{@link IEditorSpan} 的Span
     */
    public void addEditorSpan(IEditorSpan editorSpan) {
        if (!hasFocus()) {
            return;
        }
        if (editorSpan instanceof PhotoSpan) {
            addImage((PhotoSpan) editorSpan);
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
     * 添加图片 ImageSpan
     * 注意: 这里可能有换行操作
     */
    public PhotoSpan addImage(PhotoSpan photoSpan) {
        if (!hasFocus()) {
            return null;
        }
        if (photoSpan != null) {
            photoSpan.setOnPhotoSpanEventListener(this);
            SpannableStringBuilder style = getStyle();
            // 判断是否需要换行, 若已有行则不换
            boolean isNeedLineFeed = selStart - 1 > 0 && !String.valueOf(style.charAt(selStart - 1)).equals(STRING_LINE_FEED);
            style.insert(selStart, isNeedLineFeed ? STRING_LINE_FEED : "");
            int start = selStart + (isNeedLineFeed ? STRING_LINE_FEED.length() : 0);
            int end = start + photoSpan.getShowTextLength();
            style.insert(start, photoSpan.getShowText());
            style.setSpan(photoSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.insert(end, STRING_LINE_FEED);
            setText(style);
            setSelection(end + 1);

            setMovementMethod(ClickableMovementMethod.get());
        }
        return photoSpan;
    }

    /**
     * 添加注释 {@link NotesSpan}
     * 注意: 需要结合{@link PhotoSpan} 使用, 如需单独使用请参考并自定义
     */
    public NotesSpan addNotes(NotesSpan notesSpan) {
        if (notesSpan != null) {
            notesSpan.setOnNotesSpanClickListener(this);
            SpannableStringBuilder style = getStyle();
            int start = selStart + STRING_LINE_FEED.length();
            int end = start + notesSpan.getShowTextLength();
            style.insert(start, notesSpan.getShowText());
            style.setSpan(notesSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.insert(end, STRING_LINE_FEED);
            setText(style);
            setSelection(end + STRING_LINE_FEED.length());

            setMovementMethod(ClickableMovementMethod.get());
        }
        return notesSpan;
    }

    /**
     * 获取最终的编辑结果
     */
    public String getEditTexts() {
        return SpanUtils.getEditTexts(getText());
    }

    public void setOnImageTextEditorEventListener(OnImageTextEditorEventListener onImageTextEditorEventListener) {
        this.onImageTextEditorEventListener = onImageTextEditorEventListener;
    }

    /**
     * 返回被点击的{@link PhotoSpan}的左下角坐标
     */
    public interface OnImageTextEditorEventListener {

        /**
         * {@link PhotoSpan.Config} 配置参数
         */
        void onPhotoSpanConfig(PhotoSpan.Config config);

        /**
         * {@link NotesSpan#onClick(View, int, int, IEditorSpan, boolean)}
         */
        void onNotesSpanClick(View view, IEditorSpan iEditorSpan);
    }
}