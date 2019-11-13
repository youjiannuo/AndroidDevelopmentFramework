package com.yn.framework.imageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.yn.framework.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by youjiannuo on 15/9/2.
 */
public class BitmapDealManager {

    /**
     * 对图片进行一些处理，比如添加圆角，圆形等
     *
     * @param bitmap
     * @param task
     * @return
     */
    public Bitmap dealBitmap(Bitmap bitmap, NetworkPhotoTask task) {
        if (bitmap == null) return null;

        //The image is set to the circle
        if (task.isSetRounded) {
            bitmap = ImageUtil.getRoundedBitmap(bitmap);
        } else if (task.roundedCornersSize > 0) {
            // The image is set to the rounded Corner
            bitmap = ImageUtil.toRoundCorner(bitmap, task.roundedCornersSize);
        }


        return bitmap;
    }

    public static void saveImageToFileCacheAndDeleteFromFile(String fromFileName, String toUrl) {
        NetworkPhotoTask task = NetworkPhotoTask.build();
        task.url = toUrl;
        FileUtil.copyAndDeleteFromFile(fromFileName, task.getPhotoName());
    }


    public static void saveImageToFileCache(String fromFileName, String toUrl) {
        NetworkPhotoTask task = NetworkPhotoTask.build();
        task.url = toUrl;
        FileUtil.copy(fromFileName, task.getPhotoName());
    }

    public static void saveImageToFileCache(InputStream inputStream, String url) {
        NetworkPhotoTask task = NetworkPhotoTask.build();
        task.url = url;
        try {
            FileUtil.saveInputStreamBitmap(inputStream, task.getPhotoName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getRightDirectionBitmap(String filename, Bitmap bitmap) {
        if (!new File(filename).exists()) {
            return null;
        }

        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filename);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息

            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(filename);
        }
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }
        return bitmap;
    }



    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }



}
