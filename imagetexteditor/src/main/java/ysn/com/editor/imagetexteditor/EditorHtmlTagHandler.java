package ysn.com.editor.imagetexteditor;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @Author yangsanning
 * @ClassName EditorHtmlTagHandler
 * @Description 转换 Html tag
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */
public class EditorHtmlTagHandler implements Html.TagHandler {

    private static final String TAG_IMAGE = "general";

    private int width, height;

    private int startIndex = 0;
    private int stopIndex = 0;
    private final HashMap<String, String> attributes = new HashMap<String, String>();

    public EditorHtmlTagHandler(int width, int height) {
        this.width = width;
        this.height = height;
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
        Drawable drawable = new LoadingDrawable(width, height);
        drawable.setBounds(0, 0, width, height);
        output.setSpan(new LoadingSpan(drawable, url), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
