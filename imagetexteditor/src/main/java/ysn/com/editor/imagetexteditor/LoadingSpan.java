package ysn.com.editor.imagetexteditor;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * @Author yangsanning
 * @ClassName LoadingSpan
 * @Description 一句话概括作用
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */
public class LoadingSpan extends ImageSpan {

    private String imageUrl;

    public LoadingSpan(Drawable drawable, String imageUrl) {
        super(drawable);
        this.imageUrl = imageUrl;
    }
}
