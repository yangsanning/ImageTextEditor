package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import ysn.com.editor.imagetexteditor.listener.OnCloseImageSpanClickListener;
import ysn.com.editor.imagetexteditor.utils.ImageUtils;

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
    private ClickableMovementMethod clickableMovementMethod;
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

        Editable text = getText();
        CloseImageSpan[] imageSpans = text.getSpans(selStart - 1, selStart, CloseImageSpan.class);
        if (imageSpans.length > 0) {
            lastImageSpan = imageSpans[0];
            lastImageSpan.setSelect(Boolean.TRUE);
        } else if (lastImageSpan != null) {
            lastImageSpan.setSelect(Boolean.FALSE);
            lastImageSpan = null;
        }

        Log.d("test", "selStart: " + selStart);

        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() != KeyEvent.ACTION_UP) {
            Editable text = getText();
            CloseImageSpan[] imageSpans = text.getSpans(selStart - 1, selStart, CloseImageSpan.class);
            if (imageSpans.length > 0) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void addImage(Drawable drawable) {
        Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.editor_ic_close), 60, 60);
        ImageSpan imageSpan = new CloseImageSpan(drawable, closeBitmap, this);
        SpannableStringBuilder style = getStyle();
        boolean isNeedLineFeed = selStart != 0 && !String.valueOf(style.charAt(selStart - 1)).equals(STRING_LINE_FEED);
        style.insert(selStart, isNeedLineFeed ? STRING_LINE_FEED : "");
        int start = selStart + (isNeedLineFeed ? STRING_LINE_FEED.length() : 0);
        int end = start + 1;
        style.insert(start, "*");
        style.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        style.insert(end, STRING_LINE_FEED);
        setText(style);
        setSelection(getSpanEnd(imageSpan) + 1);

        setMovementMethod();
    }

    /**
     * {@link CloseImageSpan#onClick(View, int, int)}的点击图片回调
     */
    @Override
    public void onImageClick() {
        Toast.makeText(getContext(), "点击图片", Toast.LENGTH_SHORT).show();
    }

    /**
     * {@link CloseImageSpan#onClick(View, int, int)}的点击关闭按钮回调
     */
    @Override
    public void onClose(final CloseImageSpan closeImageSpan) {
        post(new Runnable() {
            @Override
            public void run() {
                SpannableStringBuilder style = getStyle();
                int spanEnd = style.getSpanEnd(closeImageSpan);
                style.removeSpan(closeImageSpan);
                style.replace(spanEnd - 1, spanEnd, "");
                setText(style);
                setSelection(Math.min(spanEnd, style.length()));
            }
        });
    }

    private SpannableStringBuilder getStyle() {
        return new SpannableStringBuilder(getText());
    }

    private int getSpanEnd(ImageSpan span) {
        return getText().getSpanEnd(span);
    }

    private void setMovementMethod() {
        if (clickableMovementMethod == null) {
            clickableMovementMethod = new ClickableMovementMethod();
        }
        setMovementMethod(clickableMovementMethod);
    }
}
