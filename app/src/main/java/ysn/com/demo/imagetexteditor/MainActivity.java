package ysn.com.demo.imagetexteditor;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import ysn.com.editor.imagetexteditor.EditorEditText;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;

public class MainActivity extends AppCompatActivity {

    private EditorEditText editorEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        editorEditView = findViewById(R.id.main_activity_text);
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取图片
                Drawable drawable = getResources().getDrawable(R.drawable.image);
                // 设置固有宽高
                drawable.setBounds(0, 0, DeviceUtils.getScreenWidth(MainActivity.this), 150);
                editorEditView.addImage(drawable);
            }
        });
    }
}
