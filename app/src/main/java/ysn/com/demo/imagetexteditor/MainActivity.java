package ysn.com.demo.imagetexteditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import ysn.com.demo.imagetexteditor.span.StockSpan;
import ysn.com.editor.imagetexteditor.ImageTextEditor;
import ysn.com.editor.imagetexteditor.JackEditor;
import ysn.com.editor.imagetexteditor.span.IEditorSpan;
import ysn.com.editor.imagetexteditor.span.PhotoSpan;
import ysn.com.editor.imagetexteditor.utils.DeviceUtils;
import ysn.com.jackphotos.JackPhotos;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PAGE_REQUEST_CODE_JACK_PHOTOS = 2020;
    private static final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL = 0x00000012;

    private PhotoSpan.Config config;

    private EditorScrollView editorScrollView;
    private ImageTextEditor editorEditView;
    private View editorLayout;
    private View notesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        editorScrollView = findViewById(R.id.main_activity_editor_scroll_view);
        editorLayout = findViewById(R.id.main_activity_editor_layout);
        editorEditView = findViewById(R.id.main_activity_editor_edit_text);
        notesView = findViewById(R.id.main_activity_editor_notes);

        // 初始化编辑器相关参数
        int editorWidth = getEditorWidth();
        JackEditor.get().bindEditor(editorEditView)
                .setPhotoSpanWidth(editorWidth)
                .setDeleteDrawable(R.drawable.ic_delete, 60, 60, 40, 40)
                .setNotesSpanWidth(editorWidth)
                .setNotesSpanTextColor(Color.parseColor("#999999"))
                .setNotesSpanTextSize(editorEditView.getTextSize())
                .setNotesSpanMarginTop(0)
                .setNotesSpanMarginBottom(40);

        editorEditView.setOnImageTextEditorEventListener(new ImageTextEditor.OnImageTextEditorEventListener() {
            @Override
            public void onPhotoSpanConfig(PhotoSpan.Config config) {
                MainActivity.this.config = config;
                if (config.isSelect) {
                    int x = config.x + config.width / 2 - ViewUtils.getWidth(notesView) / 2;
                    int y = config.y - config.height - ViewUtils.getHeight(notesView);
                    ViewUtils.setLayout(notesView, x, y);
                    notesView.setVisibility(View.VISIBLE);
                } else {
                    notesView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNotesSpanClick(View view, IEditorSpan iEditorSpan) {
                showMessage("点击了注释");
            }

            @Override
            public void onPhotoDelete() {
                MainActivity.this.config = null;
                notesView.setVisibility(View.GONE);
            }
        });

        editorScrollView.setOnScrollChangedListener(new EditorScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int newX, int newY, int oldX, int oldY) {
                if (config != null && config.isSelect) {
                    if (ViewUtils.isViewCovered(notesView)) {
                        int x = config.x + config.width / 2 - ViewUtils.getWidth(notesView) / 2;
                        int y = newY > oldY ? config.y : config.y - config.height - ViewUtils.getHeight(notesView);
                        ViewUtils.setLayout(notesView, x, y);
                    }
                }
            }
        });

        findViewById(R.id.main_activity_preview).setOnClickListener(this);
        findViewById(R.id.main_activity_text).setOnClickListener(this);
        findViewById(R.id.main_activity_editor_notes).setOnClickListener(this);
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
            case R.id.main_activity_editor_notes:
                JackEditor.get().addNotes("玉树临风风流倜傥英俊潇洒才高八斗貌似潘安号称一朵梨花压海棠人送绰号玉面小飞龙");
                break;
            case R.id.main_activity_preview:
                String data = editorEditView.getEditTexts();
                if (TextUtils.isEmpty(data)) {
                    showMessage("数据为空");
                    return;
                }
                Intent intent = new Intent(this, PreviewSpanActivity.class);
                intent.putExtra(PreviewSpanActivity.EXTRA_TEXT, data);
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
            JackEditor.get().addPhoto(photoPathList.get(0));
        }
    }

    private int getEditorWidth() {
        return DeviceUtils.getScreenWidth(this) - editorLayout.getPaddingStart() - editorLayout.getPaddingEnd();
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
