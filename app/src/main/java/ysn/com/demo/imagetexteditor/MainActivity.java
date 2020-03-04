package ysn.com.demo.imagetexteditor;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import ysn.com.editor.imagetexteditor.EditorEditText;
import ysn.com.editor.imagetexteditor.EditorHtmlTagHandler;
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
                drawable.setBounds(0, 0,
                        (DeviceUtils.getScreenWidth(MainActivity.this) - editorEditView.getPaddingStart() - editorEditView.getPaddingEnd()),
                        600);
                editorEditView.addImage(drawable);
            }
        });

        final NestedScrollView scrollView = findViewById(R.id.main_activity_scroll_view);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                editorEditView.setHeight(scrollView.getMeasuredHeight());
            }
        });

        setData();
    }

    private void setData() {
        editorEditView.setText(Html.fromHtml(getData(), null, new EditorHtmlTagHandler(
                DeviceUtils.getScreenWidth(MainActivity.this) - editorEditView.getPaddingStart() - editorEditView.getPaddingEnd(), 600)));
    }

    public String getData() {
        return "继续就是亟待解决< a href=\" \">莱绅通灵(603900)</ a>的亟待解决顶焦度计多久就多久能到难道难道不代表不到八点半下不咸不淡不行吧你的呢\n" +
                "<general>http://static.jiangjuncj.com/test/app/user/chat/3747edb200c8412d92110d46e2b4c079.png?width=4032.000000&height=3024.000000</general>\n" +
                "\n" +
                "博顶焦度计的嫩嫩的难道你难道难道难道你那等你的嫩嫩的那些那些那些内心呢\n" +
                "<general>http://static.jiangjuncj.com/test/app/user/chat/a21111de62a048a9914295bcfd35bf2e.png?width=3024.000000&height=4032.000000</general>\n" +
                "\n" +
                "表达你的嫩嫩的难道你难道难道你那等你那些年你那些年那些那些年那些那些那些内心呢"
                        .replace("\n", "<br />");
    }
}
