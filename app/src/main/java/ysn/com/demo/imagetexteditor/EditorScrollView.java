package ysn.com.demo.imagetexteditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * @Author yangsanning
 * @ClassName EditorScrollview
 * @Description 一句话概括作用
 * @Date 2020/3/10
 * @History 2020/3/10 author: description:
 */
public class EditorScrollView extends NestedScrollView {

    private OnScrollChangedListener onScrollChangedListener;

    public EditorScrollView(@NonNull Context context) {
        super(context);
    }

    public EditorScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    public interface OnScrollChangedListener {

        /**
         * @param newX    变化后的X轴位置
         * @param newY    变化后的Y轴的位置
         * @param oldX 原先的X轴的位置
         * @param oldY 原先的Y轴的位置
         */
        void onScrollChanged(int newX, int newY, int oldX, int oldY);
    }
}
