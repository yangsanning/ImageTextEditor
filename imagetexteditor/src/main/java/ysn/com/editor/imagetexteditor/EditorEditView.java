package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.Toast;

import ysn.com.editor.imagetexteditor.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName ImageTextEditor
 * @Description 一句话概括作用
 * @Date 2020/2/26
 * @History 2020/2/26 author: description:
 */
public class EditorEditView extends EditTextWithScrollView {

    public EditorEditView(Context context) {
        super(context);
    }

    public EditorEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addImage(Drawable drawable) {
        Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.editor_ic_close), 60, 60);
        ImageSpan imageSpan = new CloseImageSpan(drawable, closeBitmap, new CloseImageSpan.OnCloseImageSpanClickListener() {
            @Override
            public void onImageClick() {
                Toast.makeText(getContext(), "点击图片", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(getContext(), "点击关闭按钮", Toast.LENGTH_SHORT).show();
            }
        });
        SpannableStringBuilder style = new SpannableStringBuilder(getText());
        style.setSpan(imageSpan, style.length() - 1, style.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        style.insert(style.length(), "\n ");
        setText(style);
    }
}
