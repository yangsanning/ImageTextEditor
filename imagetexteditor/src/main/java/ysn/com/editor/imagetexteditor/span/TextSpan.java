package ysn.com.editor.imagetexteditor.span;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * @Author yangsanning
 * @ClassName TextSpan
 * @Description 一句话概括作用
 * @Date 2020/3/6
 * @History 2020/3/6 author: description:
 */
public class TextSpan extends ClickableSpan implements IEditorSpan {

    private String text;

    public TextSpan(String text) {
        this.text = text;
    }

    @Override
    public String getStartTag() {
        return "<a href=\"jiangjun://stockDetails?name=润达医疗&amp;code=603108\">";
    }

    @Override
    public String getEndTag() {
        return "</a>";
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int length() {
        return TextUtils.isEmpty(text) ? 0 : text.length();
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(Color.parseColor("#308ef2"));
    }

    @Override
    public void onClick(View view) {

    }
}
