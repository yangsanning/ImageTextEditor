package ysn.com.editor.imagetexteditor.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import ysn.com.editor.imagetexteditor.span.IEditorSpan;

/**
 * @Author yangsanning
 * @ClassName SpanUtils
 * @Description 一句话概括作用
 * @Date 2020/3/5
 * @History 2020/3/5 author: description:
 */
public class SpanUtils {

    public static String getEditTexts(Editable text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        IEditorSpan[] editorSpans = builder.getSpans(0, builder.length(), IEditorSpan.class);
        for (int i = editorSpans.length - 1; i >= 0; i--) {
            IEditorSpan editorSpan = editorSpans[i];
            int start = builder.getSpanStart(editorSpan);
            int end = builder.getSpanEnd(editorSpan);
            builder.replace(start, end, editorSpan.getResult());
        }
        return builder.toString();
    }
}