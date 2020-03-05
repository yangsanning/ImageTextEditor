package ysn.com.demo.imagetexteditor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import ysn.com.editor.imagetexteditor.component.EditorHtmlTagHandler;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;

/**
 * @Author yangsanning
 * @ClassName PreviewActivity
 * @Description 一句话概括作用
 * @Date 2020/3/5
 * @History 2020/3/5 author: description:
 */
public class PreviewActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "EXTRA_TEXT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        TextView textView = findViewById(R.id.preview_activity_text);

        String text = getIntent().getStringExtra(EXTRA_TEXT);
        int imageWidth = DeviceUtils.getScreenWidth(this) - textView.getPaddingStart() - textView.getPaddingEnd();
        textView.setText(Html.fromHtml(text, null, new EditorHtmlTagHandler(this, textView, imageWidth)));
    }
}
