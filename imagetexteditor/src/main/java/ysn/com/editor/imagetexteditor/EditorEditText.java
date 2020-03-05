package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Method;

import ysn.com.editor.imagetexteditor.component.ClickableMovementMethod;
import ysn.com.editor.imagetexteditor.component.EditTextWithScrollView;
import ysn.com.editor.imagetexteditor.component.LoadingDrawable;
import ysn.com.editor.imagetexteditor.span.UrlImageSpan;
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
public class EditorEditText extends EditTextWithScrollView implements UrlImageSpan.OnCloseImageSpanClickListener {

    private static final String STRING_LINE_FEED = "\n";

    /**
     * closeIconRes:          关闭按钮的资源id
     * closeIconWidth:        关闭按钮的宽
     * closeIconHeight:       关闭按钮的高
     * closeIconMarginTop:    关闭图标的上边距
     * closeIconMarginRight:  关闭图标的右边距
     * imageWidth:            图片宽度
     */
    private int closeIconRes;
    private int closeIconWidth;
    private int closeIconHeight;
    private float closeIconMarginTop;
    private float closeIconMarginRight;
    private int imageWidth = 800;
    private int loadingDrawableHeight = 600;

    private int selStart, selEnd;
    private UrlImageSpan lastImageSpan;

    public EditorEditText(Context context) {
        this(context, null);
    }

    public EditorEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditorEditText);

        closeIconRes = typedArray.getResourceId(R.styleable.EditorEditText_eet_close_icon, R.drawable.editor_ic_close);
        closeIconWidth = typedArray.getDimensionPixelSize(R.styleable.EditorEditText_eet_close_icon_width, 60);
        closeIconHeight = typedArray.getDimensionPixelSize(R.styleable.EditorEditText_eet_close_icon_height, 60);
        closeIconMarginTop = typedArray.getDimensionPixelSize(R.styleable.EditorEditText_eet_close_icon_margin_top, 30);
        closeIconMarginRight = typedArray.getDimensionPixelSize(R.styleable.EditorEditText_eet_close_icon_margin_right, 30);

        typedArray.recycle();
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
        UrlImageSpan[] imageSpans = text.getSpans(selStart - 1, selStart, UrlImageSpan.class);
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
            text.setSpan(lastImageSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtils.d("dispatchKeyEvent");
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() != KeyEvent.ACTION_UP) {
            if (selStart == selEnd) {
                Editable text = getText();
                UrlImageSpan[] imageSpans = text.getSpans(selStart - 2, selStart, UrlImageSpan.class);
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
     * 点击{@link UrlImageSpan#onClick(View, int, int, UrlImageSpan, boolean)}图片-按下
     */
    @Override
    public void onImageDown(UrlImageSpan imageSpan) {
        setInputVisible(bFalse());
    }

    /**
     * 点击@link UrlImageSpan#onClick(View, int, int, UrlImageSpan, boolean)}图片-抬起
     */
    @Override
    public void onImageUp(UrlImageSpan imageSpan) {
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
            text.setSpan(lastImageSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
     * 点击{@link UrlImageSpan#onClick(View, int, int, UrlImageSpan, boolean)}关闭按钮
     */
    @Override
    public void onClose(final UrlImageSpan closeImageSpan) {
        lastImageSpan = null;
        Editable text = getText();
        int spanEnd = getSpanEnd(text, closeImageSpan);
        text.removeSpan(closeImageSpan);
        text.replace(spanEnd - closeImageSpan.length(), spanEnd, "");
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

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageWidth() {
        return imageWidth == 0 ? getWidth() : imageWidth;
    }

    public void setLoadingDrawableHeight(int loadingDrawableHeight) {
        this.loadingDrawableHeight = loadingDrawableHeight;
    }

    public void addImage(String imageUrl) {
        if (!hasFocus()) {
            return;
        }
        Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(closeIconRes), closeIconWidth, closeIconHeight);
        UrlImageSpan urlImageSpan = new UrlImageSpan(new LoadingDrawable(getImageWidth(), loadingDrawableHeight), imageUrl, getImageWidth(), this);
        urlImageSpan.bindCloseBitmap(closeBitmap, closeIconMarginTop, closeIconMarginRight);
        urlImageSpan.setOnCloseImageSpanClickListener(this);
        SpannableStringBuilder style = getStyle();
        boolean isNeedLineFeed = selStart - 1 > 0 && !String.valueOf(style.charAt(selStart - 1)).equals(STRING_LINE_FEED);
        style.insert(selStart, isNeedLineFeed ? STRING_LINE_FEED : "");
        int start = selStart + (isNeedLineFeed ? STRING_LINE_FEED.length() : 0);
        int end = start + urlImageSpan.length();
        style.insert(start, urlImageSpan.getText());
        style.setSpan(urlImageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.insert(end, STRING_LINE_FEED);
        setText(style);
        setSelection(end+1);

        setMovementMethod(ClickableMovementMethod.get());
    }

    public String getEditTexts() {
        return getText().toString();
    }
}
