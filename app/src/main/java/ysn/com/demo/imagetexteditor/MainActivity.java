package ysn.com.demo.imagetexteditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import ysn.com.demo.imagetexteditor.span.EditorImageSpan;
import ysn.com.demo.imagetexteditor.span.StockSpan;
import ysn.com.editor.imagetexteditor.ImageTextEditor;
import ysn.com.editor.imagetexteditor.span.BaseCloseImageSpan;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;
import ysn.com.editor.imagetexteditor.utils.ImageUtils;
import ysn.com.jackphotos.JackPhotos;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PAGE_REQUEST_CODE_JACK_PHOTOS = 2020;
    private static final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL = 0x00000012;

    private BaseCloseImageSpan.Config config;

    private EditorScrollView editorScrollView;
    private ImageTextEditor editorEditView;
    private View editorLayout;
    private View anchorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        editorScrollView = findViewById(R.id.main_activity_editor_scroll_view);
        editorLayout = findViewById(R.id.main_activity_editor_layout);
        editorEditView = findViewById(R.id.main_activity_editor_edit_text);
        anchorView = findViewById(R.id.main_activity_editor_anchor);

        editorEditView.setOnDrawablePointListener(new ImageTextEditor.OnCloseImageSpanConfigListener() {
            @Override
            public void onCloseImageSpanConfig(BaseCloseImageSpan.Config config) {
                MainActivity.this.config = config;
                if (config.isSelect) {
                    int x = config.x + config.width / 2 - ViewUtils.getWidth(anchorView) / 2;
                    int y = config.y - config.height - ViewUtils.getHeight(anchorView);
                    ViewUtils.setLayout(anchorView, x, y);
                    anchorView.setVisibility(View.VISIBLE);
                } else {
                    anchorView.setVisibility(View.GONE);
                }
            }
        });

        editorScrollView.setOnScrollChangedListener(new EditorScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int newX, int newY, int oldX, int oldY) {
                if (config != null && config.isSelect) {
                    if (ViewUtils.isViewCovered(anchorView)) {
                        int x = config.x + config.width / 2 - ViewUtils.getWidth(anchorView) / 2;
                        int y = newY > oldY ? config.y : config.y - config.height - ViewUtils.getHeight(anchorView);
                        ViewUtils.setLayout(anchorView, x, y);
                    }
                }
            }
        });

        findViewById(R.id.main_activity_preview).setOnClickListener(this);
        findViewById(R.id.main_activity_text).setOnClickListener(this);
        findViewById(R.id.main_activity_jack_photos).setOnClickListener(this);

        checkPermission();
    }

    private void showMessage(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            preload();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE_EXTERNAL);
        }
    }

    /**
     * 预加载手机图片
     */
    private void preload() {
        JackPhotos.preload(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_jack_photos:
                //多选(最多9张)
                JackPhotos.create()
                        // 设置是否使用拍照
                        .useCamera(true)
                        // 设置是否单选
                        .setSingle(false)
                        // 是否点击放大图片查看,，默认为true
                        .canPreview(false)
                        // 图片的最大选择数量，小于等于0时，不限数量。
                        .setMaxSelectCount(9)
                        // 打开相册
                        .start(this, PAGE_REQUEST_CODE_JACK_PHOTOS);
                break;
            case R.id.main_activity_text:
                editorEditView.addEditorSpan(new StockSpan(("润达医疗"), ("603108")));
                break;
            case R.id.main_activity_preview:
                String data = editorEditView.getEditTexts();
                if (TextUtils.isEmpty(data)) {
                    showMessage("数据为空");
                    return;
                }
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra(PreviewActivity.EXTRA_TEXT, data);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAGE_REQUEST_CODE_JACK_PHOTOS && data != null) {
            ArrayList<String> photoPathList = data.getStringArrayListExtra(JackPhotos.EXTRA_PHOTOS);
            if (photoPathList == null || photoPathList.isEmpty()) {
                return;
            }
            String imagePath = photoPathList.get(0);
            Bitmap bitmap = ImageUtils.getBitmap(imagePath);
            bitmap = ImageUtils.zoom(bitmap, DeviceUtils.getScreenWidth(this) - editorLayout.getPaddingStart() - editorLayout.getPaddingEnd());
            Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Bitmap closeBitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.close), 60, 60);
            editorEditView.addImage(new EditorImageSpan(drawable, closeBitmap, imagePath));
        }
    }

    /**
     * 处理权限申请的回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 预加载手机图片
                preload();
            }
        }
    }
}
