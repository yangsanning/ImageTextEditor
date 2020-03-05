package ysn.com.editor.imagetexteditor.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import ysn.com.editor.imagetexteditor.span.UrlImageSpan;

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
        UrlImageSpan[] spans = builder.getSpans(0, builder.length(), UrlImageSpan.class);
        for (int i = spans.length - 1; i >= 0; i--) {
            UrlImageSpan span = spans[i];
            int start = builder.getSpanStart(span);
            int end = builder.getSpanEnd(span);
            builder.insert(end, "</general>");
            builder.replace(start, start + 1, span.getImageUrl());
            builder.insert(start, "<general>");
        }
        return builder.toString();
    }
}