package ysn.com.editor.imagetexteditor.component;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * @Author yangsanning
 * @ClassName LoadingDrawable
 * @Description 加载种rawable
 * @Date 2020/3/4
 * @History 2020/3/4 author: description:
 */
public class LoadingDrawable extends Drawable {
    private Paint paint;
    private Rect rect;

    public LoadingDrawable(int width, int height) {
        super();
        init(width, height);
    }

    private void init(int width, int height) {
        setBounds(0, 0, width, height);

        rect = new Rect(0, 0, width, height);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#33308ef2"));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}