package ysn.com.editor.imagetexteditor.component;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

import ysn.com.editor.imagetexteditor.component.LoadingDrawable;
import ysn.com.editor.imagetexteditor.span.UrlImageSpan;

/**
 * @Author yangsanning
 * @ClassName EditorHtmlTagHandler
 * @Description 转换 Html tag
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */
public class EditorHtmlTagHandler implements Html.TagHandler {

    private static final String TAG_IMAGE = "general";

    private int imageWidth;

    private Context context;
    private TextView textView;

    private int startIndex = 0;
    private int stopIndex = 0;
    private final HashMap<String, String> attributes = new HashMap<String, String>();

    public EditorHtmlTagHandler(Context context, TextView textView, int imageWidth) {
        this.context = context;
        this.textView = textView;
        this.imageWidth = imageWidth;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        processAttributes(xmlReader);

        if (tag.equalsIgnoreCase(TAG_IMAGE)) {
            if (opening) {
                startFont(tag, output, xmlReader);
            } else {
                endFont(tag, output, xmlReader);
            }
        }
    }

    public void startFont(String tag, Editable output, XMLReader xmlReader) {
        startIndex = output.length();
    }

    public void endFont(String tag, Editable output, XMLReader xmlReader) {
        stopIndex = output.length();
        String url = output.subSequence(startIndex, stopIndex).toString();

        LoadingDrawable loadingDrawable = new LoadingDrawable(imageWidth, 600);
        UrlImageSpan urlImageSpan = new UrlImageSpan(loadingDrawable, url, imageWidth, textView);
        output.setSpan(urlImageSpan, startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void processAttributes(final XMLReader xmlReader) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);

            for (int i = 0; i < len; i++) {
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
            }
        } catch (Exception e) {

        }
    }
}
