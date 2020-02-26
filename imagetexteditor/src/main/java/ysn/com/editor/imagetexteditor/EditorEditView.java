package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
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
public class EditorEditView extends EditTextWithScrollView {

    private static final String STRING_LINE_FEED = "\n";

    private int selStart, selEnd;
    private ClickableMovementMethod clickableMovementMethod;

    public EditorEditView(Context context) {
        super(context);
    }

    public EditorEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        this.selStart = selStart;
        this.selEnd = selEnd;
        super.onSelectionChanged(selStart, selEnd);
    }

    public void addImage(Drawable drawable) {
        Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.editor_ic_close), 60, 60);
        ImageSpan imageSpan = new CloseImageSpan(drawable, closeBitmap, new OnCloseImageSpanClickListener() {
            @Override
            public void onImageClick() {
                Toast.makeText(getContext(), "点击图片", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(getContext(), "点击关闭按钮", Toast.LENGTH_SHORT).show();
            }
        });
        SpannableStringBuilder style = new SpannableStringBuilder(getText());
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

    private int getSpanEnd(ImageSpan span) {
        return getText().getSpanEnd(span);
    }

    private void setMovementMethod() {
        if (clickableMovementMethod==null) {
            clickableMovementMethod = new ClickableMovementMethod();
        }
        setMovementMethod(clickableMovementMethod);
    }
}
