package ysn.com.editor.phototexteditor.component;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import ysn.com.editor.phototexteditor.span.IEditorSpan;

/**
 * @Author yangsanning
 * @ClassName EditorMovementMethod
 * @Description 编辑器的 LinkMovementMethod, 赋予 IEditorSpan 点击事件
 * @Date 2020/2/25
 * @History 2020/2/25 author: description:
 */
public class EditorMovementMethod extends LinkMovementMethod {

    private static EditorMovementMethod instance;

    public static EditorMovementMethod get() {
        if (instance == null) {
            synchronized (EditorMovementMethod.class) {
                if (instance == null) {
                    instance = new EditorMovementMethod();
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

            // 优先走自定义 Span 的点击事件
            IEditorSpan[] iEditorSpans = buffer.getSpans(off, off, IEditorSpan.class);
            if (iEditorSpans.length != 0) {
                iEditorSpans[0].onClick(textView, x, y, iEditorSpans[0], action == MotionEvent.ACTION_DOWN);
                return true;
            }

            ClickableSpan[] clickableSpans = buffer.getSpans(off, off, ClickableSpan.class);
            if (clickableSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    clickableSpans[0].onClick(textView);
                } else {
                    Selection.setSelection(buffer, buffer.getSpanStart(clickableSpans[0]), buffer.getSpanEnd(clickableSpans[0]));
                }
                return true;
            }
            Selection.removeSelection(buffer);
        }
        return false;
    }
}
