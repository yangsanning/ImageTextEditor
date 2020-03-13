package ysn.com.editor.imagetexteditor.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import ysn.com.editor.imagetexteditor.span.IEditorSpan;
import ysn.com.editor.imagetexteditor.span.NotesSpan;
import ysn.com.editor.imagetexteditor.span.PhotoSpan;

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

    public static IEditorSpan[] getEditorSpans(Editable text) {
        return getEditorSpans(text, 0, text.length());
    }

    public static IEditorSpan[] getEditorSpans(Editable text, int start, int end) {
        return text.getSpans(start, end, IEditorSpan.class);
    }

    public static PhotoSpan[] getPhotoSpans(Editable text, int start, int end) {
        return text.getSpans(start, end, PhotoSpan.class);
    }

    public static NotesSpan getNotesSpan(Editable text, int start) {
        NotesSpan[] notesSpans = getNotesSpans(text, start, start + 2);
        return notesSpans.length > 0 ? notesSpans[0] : null;
    }

    public static NotesSpan[] getNotesSpans(Editable text, int start, int end) {
        return text.getSpans(start, end, NotesSpan.class);
    }
}