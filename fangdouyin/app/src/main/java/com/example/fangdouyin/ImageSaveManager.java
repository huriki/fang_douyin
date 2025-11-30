package com.example.fangdouyin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片提取+存储工具类（单例模式）
 */
public class ImageSaveManager {
    // 单例实例
    private static ImageSaveManager instance;
    // 上下文（全局）
    private Context context;
    // 子线程池（异步执行存储操作，避免阻塞主线程）
    private static final ExecutorService SAVE_EXECUTOR = Executors.newSingleThreadExecutor();

    // 存储目录（APP 专属目录，Android 10+ 无需权限）
    private static final String ROOT_DIR = Environment.DIRECTORY_PICTURES;
    private static final String POST_IMAGE_DIR = "Waterfall/PostImages"; // 内容图目录
    private static final String AVATAR_DIR = "Waterfall/Avatars";       // 头像目录

    // 单例初始化（避免重复创建）
    public static synchronized ImageSaveManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageSaveManager(context);
        }
        return instance;
    }

    private ImageSaveManager(Context context) {
        this.context = context;
    }

    /**
     * 1. 从 ImageView 提取 bitmap 并存储到本地（核心方法）
     * @param imageView 已加载图片的 ImageView
     * @param localPath 目标存储路径（通过 getPostImageLocalPath/ getAvatarLocalPath 获取）
     * @param callback 保存结果回调（主线程）
     */
    public void saveImageFromImageView(ImageView imageView, String localPath, SaveCallback callback) {
        // 先判断是否已保存（避免重复存储）
        if (isImageExists(localPath)) {
            callback.onSuccess(localPath);
            return;
        }

        // 从 ImageView 提取 bitmap
        Bitmap bitmap = extractBitmapFromImageView(imageView);
        if (bitmap == null) {
            callback.onFailure("无法提取图片（图片未加载或为空）");
            return;
        }

        // 子线程异步存储 bitmap
        SAVE_EXECUTOR.execute(() -> {
            try {
                // 创建存储目录（不存在则创建）
                File targetFile = new File(localPath);
                File parentDir = targetFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs(); // 递归创建目录
                }

                // 保存 bitmap 到文件（JPEG 格式，质量 90%，兼顾质量和体积）
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush(); // 强制写入
                }

                // 保存成功：主线程回调
                ((FragmentActivity) context).runOnUiThread(() -> {
                    callback.onSuccess(localPath);
                    //bitmap.recycle(); // 释放内存，避免 OOM
                });

            } catch (IOException e) {
                e.printStackTrace();
                // 保存失败：主线程回调
                ((FragmentActivity) context).runOnUiThread(() ->
                        callback.onFailure("保存失败：" + e.getMessage())
                );
            }
        });
    }

    /**
     * 辅助方法：从 ImageView 提取 bitmap（兼容所有 Drawable 类型）
     */
    private Bitmap extractBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) return null;

        // 情况1：Drawable 是 BitmapDrawable（直接提取 bitmap）
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // 情况2：其他 Drawable（如 Glide 加载的资源）→ 绘制到新 bitmap
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        if (width <= 0 || height <= 0) return null; // 避免宽高为 0

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas); // 将 Drawable 绘制到 bitmap
        return bitmap;
    }

    /**
     * 2. 获取内容图本地存储路径（唯一命名：post_+postId+.jpg）
     */
    public String getPostImageLocalPath(long postId) {
        File dir = new File(context.getExternalFilesDir(ROOT_DIR), POST_IMAGE_DIR);
        return new File(dir, "post_" + postId + ".jpg").getAbsolutePath();
    }

    /**
     * 3. 获取头像本地存储路径（唯一命名：avatar_+userId+.jpg）
     */
    public String getAvatarLocalPath(long userId) {
        File dir = new File(context.getExternalFilesDir(ROOT_DIR), AVATAR_DIR);
        return new File(dir, "avatar_" + userId + ".jpg").getAbsolutePath();
    }

    /**
     * 4. 判断图片是否已保存（避免重复存储）
     */
    public boolean isImageExists(String localPath) {
        File file = new File(localPath);
        return file.exists() && file.length() > 0; // 确保文件存在且不为空
    }

    /**
     * 保存结果回调接口（通知 UI 状态）
     */
    public interface SaveCallback {
        void onSuccess(String localPath); // 保存成功（返回本地路径）
        void onFailure(String errorMsg);  // 保存失败（返回错误信息）
    }

    /**
     * 释放资源（APP 退出时调用）
     */
    public void release() {
        SAVE_EXECUTOR.shutdown();
    }
}
