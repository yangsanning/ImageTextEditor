package ysn.com.editor.imagetexteditor.utils;

import android.graphics.Paint;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yangsanning
 * @ClassName PaintUtils
 * @Description 一句话概括作用
 * @Date 2020/3/12
 * @History 2020/3/12 author: description:
 */
public class PaintUtils {

    /**
     * 按行分割文字
     *
     * @return 按行分割文字集合
     */
    public static List<String> cutTextByLine(TextPaint textPaint, String text, int lineWidth) {
        List<String> lineTextList = new ArrayList<>();

        char[] spanChars = text.toCharArray();
        int currentLineTextWidth = 0;
        StringBuilder textStringBuilder = new StringBuilder();
        for (int i = 0, length = spanChars.length; i < length; i++) {
            char textChar = spanChars[i];
            currentLineTextWidth += getSingleCharWidth(textPaint, textChar);
            if (currentLineTextWidth > lineWidth) {
                lineTextList.add(textStringBuilder.toString());
                textStringBuilder.delete(0, textStringBuilder.length());
                currentLineTextWidth = 0;
                i--;
            } else {
                textStringBuilder.append(textChar);
                if (i == length - 1) {
                    lineTextList.add(textStringBuilder.toString());
                }
            }
        }
        return lineTextList;
    }

    /**
     * 得到单个char的宽度
     */
    public static float getSingleCharWidth(TextPaint textPaint, char textChar) {
        float[] width = new float[1];
        textPaint.getTextWidths(new char[]{textChar}, 0, 1, width);
        return width[0];
    }

    /**
     * 获取文字高度
     */
    public static int getFontHeight(TextPaint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }
}
