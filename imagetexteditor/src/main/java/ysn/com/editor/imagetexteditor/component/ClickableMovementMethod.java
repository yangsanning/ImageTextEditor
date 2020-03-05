package ysn.com.editor.imagetexteditor.component;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import ysn.com.editor.imagetexteditor.span.UrlImageSpan;

/**
 * @Author yangsanning
 * @ClassName ClickableMovementMethod
 * @Description 为ImageSpans点击而生
 * @Date 2020/2/25
 * @History 2020/2/25 author: description:
 */
public class ClickableMovementMethod extends LinkMovementMethod {

    private static ClickableMovementMethod instance;

    public static ClickableMovementMethod get() {
        if (instance == null) {
            synchronized (ClickableMovementMethod.class) {
                if (instance == null) {
                    instance = new ClickableMovementMethod();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean onTouchEvent(TextView textView, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] clickableSpans = buffer.getSpans(off, off, ClickableSpan.class);
            UrlImageSpan[] imageSpans = buffer.getSpans(off, off, UrlImageSpan.class);

            if (clickableSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    clickableSpans[0].onClick(textView);
                } else {
                    Selection.setSelection(buffer, buffer.getSpanStart(clickableSpans[0]), buffer.getSpanEnd(clickableSpans[0]));
                }
                return true;
            } else if (imageSpans.length != 0) {
                imageSpans[0].onClick(textView, x, y, imageSpans[0], action == MotionEvent.ACTION_DOWN);
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return false;
    }
}
