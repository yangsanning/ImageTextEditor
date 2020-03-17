package ysn.com.editor.phototexteditor.component;

import android.text.Editable;
import android.text.Html;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @Author yangsanning
 * @ClassName ImageTextParser
 * @Description 图文解析器 (转换 Html tag)， 继承使用
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */
public abstract class BasePhotoTextParser implements Html.TagHandler {

    protected final HashMap<String, String> attributes = new HashMap<>();

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        processAttributes(xmlReader);

        if (opening) {
            tagStart(tag, output, xmlReader);
        } else {
            tagEnd(tag, output, xmlReader);
        }
    }

    protected abstract void tagStart(String tag, Editable output, XMLReader xmlReader);

    protected abstract void tagEnd(String tag, Editable output, XMLReader xmlReader);

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
