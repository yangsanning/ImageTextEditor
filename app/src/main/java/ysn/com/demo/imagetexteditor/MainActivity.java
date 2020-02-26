package ysn.com.demo.imagetexteditor;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ysn.com.demo.imagetexteditor.utils.ImageUtils;
import ysn.com.editor.imagetexteditor.ClickableMovementMethod;
import ysn.com.editor.imagetexteditor.CloseImageSpan;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.main_activity_text);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("哈哈\n哈哈\n哈哈\n");
        spannableStringBuilder.append("我是一个很\n占\n的字符串");
        // 获取图片
        Drawable drawable = getResources().getDrawable(R.drawable.image);
        // 设置固有宽高
        drawable.setBounds(0, 0, 800, 700);
        Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.close), 60, 60);
        ImageSpan imageSpan = new CloseImageSpan(drawable, closeBitmap, new CloseImageSpan.OnCloseImageSpanClickListener() {
                    @Override
                    public void onImageClick() {
                        Toast.makeText(MainActivity.this, "点击图片", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClose() {
                        Toast.makeText(MainActivity.this, "点击关闭按钮", Toast.LENGTH_SHORT).show();
                    }
                });
        //替换一个文字为图片
        spannableStringBuilder.setSpan(imageSpan, 15, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBuilder);
        textView.setMovementMethod(new ClickableMovementMethod());

        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.invalidate();
            }
        });
    }
}
