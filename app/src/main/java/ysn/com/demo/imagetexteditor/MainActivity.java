package ysn.com.demo.imagetexteditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import ysn.com.editor.imagetexteditor.EditorEditText;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL = 0x00000012;

    private EditorEditText editorEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        editorEditView = findViewById(R.id.main_activity_text);
        editorEditView.setImageWidth(DeviceUtils.getScreenWidth(MainActivity.this) - editorEditView.getPaddingStart() - editorEditView.getPaddingEnd());

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorEditView.addImage("http://static.jiangjuncj.com/test/app/user/chat/3747edb200c8412d92110d46e2b4c079.png?width=4032.000000&height=3024.000000");
            }
        });

        checkPermission();

        findViewById(R.id.main_activity_preview).setOnClickListener(this);
    }

    private void checkPermission() {
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            setData();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE_EXTERNAL);
        }
    }

    private void setData() {
//        int imageWidth = DeviceUtils.getScreenWidth(MainActivity.this) - editorEditView.getPaddingStart() - editorEditView.getPaddingEnd();
//        editorEditView.setText(Html.fromHtml(getData(), null, new EditorHtmlTagHandler(
//                this, editorEditView, imageWidth)));
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

    @Override
    public void onClick(View v) {
        String data = editorEditView.getEditTexts();
        if (TextUtils.isEmpty(data)) {
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_TEXT, data);
        startActivity(intent);
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setData();
            }
        }
    }
}
