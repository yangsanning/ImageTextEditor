package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;

import java.lang.reflect.Method;

import ysn.com.editor.imagetexteditor.component.EditTextWithScrollView;
import ysn.com.editor.imagetexteditor.listener.OnCloseImageSpanClickListener;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;
import ysn.com.editor.imagetexteditor.utils.ImageUtils;
import ysn.com.editor.imagetexteditor.utils.LogUtils;

/**
 * @Author yangsanning
 * @ClassName ImageTextEditor
 * @Description 一句话概括作用
 * @Date 2020/2/26
 * @History 2020/2/26 author: description:
 */
public class EditorEditText extends EditTextWithScrollView implements OnCloseImageSpanClickListener {

    private static final String STRING_LINE_FEED = "\n";

    private int selStart, selEnd;
    private CloseImageSpan lastImageSpan;

    public EditorEditText(Context context) {
        super(context);
    }

    public EditorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
        this.selStart = selStart;
        this.selEnd = selEnd;

        LogUtils.d("onSelectionChanged " + " selStart: " + selStart + " selEnd: " + selEnd);

        Editable text = getText();
        CloseImageSpan[] imageSpans = text.getSpans(selStart - 1, selStart, CloseImageSpan.class);
        if (imageSpans.length > 0) {
            lastImageSpan = imageSpans[0];
            lastImageSpan.setSelect(Boolean.TRUE);
            updateImageSpan(selStart, selEnd, text);
        } else if (lastImageSpan != null) {
            lastImageSpan.setSelect(Boolean.FALSE);
            updateImageSpan(selStart, selEnd, text);
            lastImageSpan = null;
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    /**
     * 切换imageSpan
     */
    private void updateImageSpan(int selStart, int selEnd, Editable text) {
        int spanStart = getSpanStart(text, lastImageSpan);
        int spanEnd = getSpanEnd(text, lastImageSpan);
        if (selStart >= 0 || selEnd >= 0) {
            text.setSpan(lastImageSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.d("dispatchKeyEvent");
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() != KeyEvent.ACTION_UP) {
            if (selStart == selEnd) {
                Editable text = getText();
                CloseImageSpan[] imageSpans = text.getSpans(selStart - 2, selStart, CloseImageSpan.class);
                if (imageSpans.length > 0) {
                    lastImageSpan = imageSpans[0];
                    lastImageSpan.setSelect(Boolean.TRUE);
                    updateImageSpan(selStart, selEnd, text);
                    hideSoftInput();
                    return true;
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
     * 点击{@link CloseImageSpan}图片-按下
     */
    @Override
    public void onImageDown(CloseImageSpan imageSpan) {
        setInputVisible(bFalse());
    }

    /**
     * 点击{@link CloseImageSpan}图片-抬起
     */
    @Override
    public void onImageUp(CloseImageSpan imageSpan) {
        resetLastImageSpan();
        lastImageSpan = imageSpan;
        lastImageSpan.setSelect(Boolean.TRUE);
        updateImageSpan(selStart, selEnd, getText());
        hideSoftInput();
    }

    /**
     * 重置lastImageSpan
     */
    private void resetLastImageSpan() {
        if (lastImageSpan == null) {
            return;
        }
        Editable text = getText();
        int spanStart = getSpanStart(text, lastImageSpan);
        int spanEnd = getSpanEnd(text, lastImageSpan);
        if (spanStart >= 0 || spanEnd >= 0) {
            lastImageSpan.setSelect(Boolean.FALSE);
            text.setSpan(lastImageSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            lastImageSpan = null;
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

    /**
     * 点击{@link CloseImageSpan}关闭按钮
     */
    @Override
    public void onClose(final CloseImageSpan closeImageSpan) {
        lastImageSpan = null;
        Editable text = getText();
        int spanEnd = getSpanEnd(text, closeImageSpan);
        text.removeSpan(closeImageSpan);
        text.replace(spanEnd - 1, spanEnd, "");
        setText(text);
        setSelection(Math.min(spanEnd, text.length()));
    }

    private SpannableStringBuilder getStyle() {
        return new SpannableStringBuilder(getText());
    }

    private int getSpanStart(ImageSpan span) {
        return getSpanStart(getText(), span);
    }

    private int getSpanStart(Editable text, ImageSpan span) {
        return text.getSpanStart(span);
    }

    private int getSpanEnd(ImageSpan span) {
        return getSpanEnd(getText(), span);
    }

    private int getSpanEnd(Editable text, ImageSpan span) {
        return text.getSpanEnd(span);
    }

    private boolean bFalse() {
        return Boolean.FALSE;
    }

    private boolean bTrue() {
        return Boolean.TRUE;
    }

    public void addImage(Drawable drawable) {
        if (!hasFocus()) {
            return;
        }
        Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.editor_ic_close), 60, 60);
        ImageSpan imageSpan = new CloseImageSpan(drawable, closeBitmap, this);
        SpannableStringBuilder style = getStyle();
        boolean isNeedLineFeed = selStart - 1 > 0 && !String.valueOf(style.charAt(selStart - 1)).equals(STRING_LINE_FEED);
        style.insert(selStart, isNeedLineFeed ? STRING_LINE_FEED : "");
        int start = selStart + (isNeedLineFeed ? STRING_LINE_FEED.length() : 0);
        int end = start + 1;
        style.insert(start, "*");
        style.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        style.insert(end, STRING_LINE_FEED);
        setText(style);
        setSelection(getSpanEnd(imageSpan) + 1);

        setMovementMethod(ClickableMovementMethod.get());
    }
}
