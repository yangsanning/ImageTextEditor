package ysn.com.editor.imagetexteditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import ysn.com.editor.imagetexteditor.bean.EditorConfig;
import ysn.com.editor.imagetexteditor.span.NotesSpan;
import ysn.com.editor.imagetexteditor.span.PhotoSpan;
import ysn.com.editor.imagetexteditor.utils.ImageUtils;

/**
 * @Author yangsanning
 * @ClassName JackEditor
 * @Description 编辑器辅助类
 * @Date 2020/3/15
 * @History 2020/3/15 author: description:
 */
public class JackEditor {

    private ImageTextEditor imageTextEditor;
    private Context context;

    private EditorConfig editorConfig;

    private static JackEditor instance;

    public static JackEditor get() {
        if (instance == null) {
            synchronized (JackEditor.class) {
                if (instance == null) {
                    instance = new JackEditor();
                }
            }
        }
        return instance;
    }

    private JackEditor() {
        editorConfig = new EditorConfig();
    }

    public static void destroy() {
        instance = null;
    }

    /**
     * 绑定编辑器
     * 这一步一定要优先调用
     *
     * @param imageTextEditor 编辑器
     */
    public JackEditor bindEditor(ImageTextEditor imageTextEditor) {
        this.imageTextEditor = imageTextEditor;
        this.context = imageTextEditor.getContext();
        return instance;
    }

    public void checkEditorNotNull() {
        if (imageTextEditor == null) {
            throw new NullPointerException("编辑器不能为 null! (请先调用 bindEditor(ImageTextEditor imageTextEditor)) 方法");
        }
    }


    /************************ {@link ysn.com.editor.imagetexteditor.span.PhotoSpan} 相关 **********/

    /**
     * 设置图片的展示宽度
     *
     * @param photoSpanWidth 图片的展示宽度
     */
    public JackEditor setPhotoSpanWidth(int photoSpanWidth) {
        editorConfig.photoSpanWidth = photoSpanWidth;
        return instance;
    }

    /**
     * 设置删除按钮相关参数
     *
     * @param deleteIconRes         删除按钮的资源id
     * @param deleteIconWidth       删除按钮的宽
     * @param deleteIconHeight      删除按钮的高度
     * @param deleteIconMarginTop   删除按钮的上边距
     * @param deleteIconMarginRight 删除按钮的右边距
     */
    public JackEditor setDeleteDrawable(int deleteIconRes, int deleteIconWidth, int deleteIconHeight,
                                        int deleteIconMarginTop, int deleteIconMarginRight) {
        editorConfig.deleteIconRes = deleteIconRes;
        editorConfig.deleteIconWidth = deleteIconWidth;
        editorConfig.deleteIconHeight = deleteIconHeight;
        editorConfig.deleteIconMarginTop = deleteIconMarginTop;
        editorConfig.deleteIconMarginRight = deleteIconMarginRight;
        return instance;
    }

    /**
     * 添加图片{@link PhotoSpan}
     *
     * @param imagePath 图片路径
     * @return {@link PhotoSpan}
     */
    public PhotoSpan addPhoto(String imagePath) {
        return addPhoto(imagePath, editorConfig.photoSpanWidth);
    }

    /**
     * 添加图片{@link PhotoSpan}
     *
     * @param imagePath      图片路径
     * @param photoSpanWidth 图片宽度
     * @return {@link PhotoSpan}
     */
    public PhotoSpan addPhoto(String imagePath, int photoSpanWidth) {
        checkEditorNotNull();

        PhotoSpan photoSpan = getPhotoSpan(imagePath, photoSpanWidth);
        return imageTextEditor.addPhoto(photoSpan);
    }

    /**
     * 获取图片
     *
     * @param imagePath      图片路径
     * @param photoSpanWidth 图片宽度
     * @return {@link PhotoSpan}
     */
    public PhotoSpan getPhotoSpan(String imagePath, int photoSpanWidth) {
        Bitmap bitmap = ImageUtils.getBitmap(imagePath);
        bitmap = ImageUtils.zoom(bitmap, photoSpanWidth);
        Drawable drawable = new BitmapDrawable(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Bitmap deleteIconBitmap = ImageUtils.drawableToBitmap(context.getResources().getDrawable(editorConfig.deleteIconRes),
                editorConfig.deleteIconWidth, editorConfig.deleteIconHeight);
        return new PhotoSpan(drawable, imagePath,
                deleteIconBitmap, editorConfig.deleteIconMarginTop, editorConfig.deleteIconMarginRight);
    }


    /************************ {@link ysn.com.editor.imagetexteditor.span.NotesSpan} 相关 **********/

    /**
     * 设置注释的展示宽度
     *
     * @param notesSpanWidth 注释的宽
     */
    public JackEditor setNotesSpanWidth(int notesSpanWidth) {
        editorConfig.notesSpanWidth = notesSpanWidth;
        return instance;
    }

    /**
     * 设置注释的文字颜色
     *
     * @param notesSpanTextColor 注释的文字颜色
     */
    public JackEditor setNotesSpanTextColor(int notesSpanTextColor) {
        editorConfig.notesSpanTextColor = notesSpanTextColor;
        return instance;
    }

    /**
     * 设置注释的文字大小
     *
     * @param notesSpanTextSize 注释的文字大小
     */
    public JackEditor setNotesSpanTextSize(float notesSpanTextSize) {
        editorConfig.notesSpanTextSize = notesSpanTextSize;
        return instance;
    }

    /**
     * 设置注释的上边距
     *
     * @param notesSpanMarginTop 注释的上边距
     */
    public JackEditor setNotesSpanMarginTop(int notesSpanMarginTop) {
        editorConfig.notesSpanMarginTop = notesSpanMarginTop;
        return instance;
    }

    /**
     * 设置注释的下边距
     *
     * @param notesSpanMarginBottom 注释的下边距
     */
    public JackEditor setNotesSpanMarginBottom(int notesSpanMarginBottom) {
        editorConfig.notesSpanMarginBottom = notesSpanMarginBottom;
        return instance;
    }

    /**
     * 添加图片{@link NotesSpan}
     *
     * @param notes 注释
     * @return {@link NotesSpan}
     */
    public NotesSpan addNotes(String notes) {
        checkEditorNotNull();

        return imageTextEditor.addNotes(getNotesSpan(notes));
    }

    /**
     * 获取注释
     *
     * @param notes 注释
     * @return {@link NotesSpan}
     */
    public NotesSpan getNotesSpan(String notes) {
        return new NotesSpan(notes, editorConfig.notesSpanWidth,
                editorConfig.notesSpanTextColor, editorConfig.notesSpanTextSize,
                editorConfig.notesSpanMarginTop, editorConfig.notesSpanMarginBottom);
    }
}
