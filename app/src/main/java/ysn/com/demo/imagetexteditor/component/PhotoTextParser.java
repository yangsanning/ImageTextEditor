package ysn.com.demo.imagetexteditor.component;

import android.text.Editable;
import android.text.Spanned;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import ysn.com.editor.imagetexteditor.JackEditor;
import ysn.com.editor.imagetexteditor.component.BasePhotoTextParser;
import ysn.com.editor.imagetexteditor.span.NotesSpan;
import ysn.com.editor.imagetexteditor.span.PreviewPhotoSpan;

/**
 * @Author yangsanning
 * @ClassName PhotoTextParser
 * @Description 图文解析器
 * @Date 2020/3/16
 * @History 2020/3/16 author: description:
 */
public class PhotoTextParser extends BasePhotoTextParser {

    private static final String TAG_IMAGE = "image";
    private static final String TAG_NOTES = "notes";

    private TextView textView;
    private int imageWidth;

    private int startIndex = 0;
    private int endIndex = 0;

    public PhotoTextParser(TextView textView, int imageWidth) {
        this.textView = textView;
        this.imageWidth = imageWidth;
    }

    @Override
    protected void tagStart(String tag, Editable output, XMLReader xmlReader) {
        startIndex = output.length();
    }

    @Override
    protected void tagEnd(String tag, Editable output, XMLReader xmlReader) {
        endIndex = output.length();

        switch (tag) {
            case TAG_IMAGE:
                String url = output.subSequence(startIndex, endIndex).toString().trim();
                PreviewPhotoSpan previewPhotoSpan = new PreviewPhotoSpan(textView, url, imageWidth);
                output.setSpan(previewPhotoSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case TAG_NOTES:
                String notes = output.subSequence(startIndex, endIndex).toString().trim();
                NotesSpan notesSpan = JackEditor.get().getNotesSpan(notes);
                output.setSpan(notesSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }
    }
}
