package ysn.com.demo.imagetexteditor.span;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import ysn.com.editor.imagetexteditor.span.IEditorSpan;

/**
 * @Author yangsanning
 * @ClassName StockSpan
 * @Description 自定义的 Span, 用于演示
 * @Date 2020/3/6
 * @History 2020/3/6 author: description:
 */
public class StockSpan extends ClickableSpan implements IEditorSpan {

    private String showText;
    private String startTag;
    private String result;

    public StockSpan(String stockName, String stockCode) {
        showText = stockName + "(" + stockCode + ")";
        startTag = String.format("<a href=\"jiangjun://stockDetails?name=%s&amp;code=%s\">", stockName, stockCode);
    }

    @Override
    public String getStartTag() {
        return startTag;
    }

    @Override
    public String getEndTag() {
        return "</a>";
    }

    @Override
    public String getShowText() {
        return showText;
    }

    @Override
    public int getShowTextLength() {
        return TextUtils.isEmpty(showText) ? 0 : showText.length();
    }

    @Override
    public String getResult() {
        return result == null ? (result = getStartTag() + showText + getEndTag()) : result;
    }

    @Override
    public void onClick(View view, int x, int y, IEditorSpan iEditorSpan, boolean isDown) {

    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(Color.parseColor("#308ef2"));
    }

    @Override
    public void onClick(View view) {

    }
}
