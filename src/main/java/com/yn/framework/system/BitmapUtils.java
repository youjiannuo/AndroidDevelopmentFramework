package com.yn.framework.system;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Base64;

import com.yn.framework.file.FileUtil;

import static com.yn.framework.system.ContextManager.getContext;

/**
 * Created by youjiannuo on 18/2/2.
 * Email by 382034324@qq.com
 */

public class BitmapUtils {

    public static String getSendBitmapString(String content) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        Bitmap bitmap1 = BitmapFactory.decodeFile(content, options);
        float size = bitmap1.getHeight() / 100.0f;
        int width = (int) (bitmap1.getWidth() / size);
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmap1, width, 100, true);
        FileUtil.saveBitmapInFile(bitmap, SystemUtil.getSDCardPath() + "/1/1.jpg");
        return "";
    }

    public static Bitmap decodeByteFromHttp(String content) {
        byte bs[] = Base64.decode(content, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bs, 0, bs.length);
    }


    public static Bitmap getRightDrawable(int drawId) {
        try {
            return BitmapFactory.decodeResource(getContext().getResources(), drawId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


//    BitmapFactory.Options options = new BitmapFactory.Options();
//    options.inSampleSize = 10;
//    Bitmap bitmap1 = BitmapFactory.decodeFile(content, options);
//        SystemUtil.printlnInfo("bitmap width = " + bitmap1.getWidth() + "   height = " + bitmap1.getHeight());
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        try {
//        baos.close();
//    } catch (IOException e) {
//        e.printStackTrace();
//        return "";
//    }
//    byte[] buffer = baos.toByteArray();
//        SystemUtil.printlnInfo("图片大小:" + buffer.length);
//    String photo = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);
//        SystemUtil.printlnInfo("photo length = " + photo.length());
//        FileUtil.saveBitmapInFile(decodeByteFromHttp(photo), SystemUtil.getSDCardPath() + "/1/1.png");

    public static int LR = 1;

    public static int TB = 2;

    public static int TRBL = 3;

    public static Bitmap drawableLinearGradient(int width, int height, int[] colors, float[] positions, int dir) {
        return drawableLinearGradient(width, height, colors, positions, 0F, 0F, 0F, 0F, dir);
    }

    public static Bitmap drawableLinearGradient(int width, int height, int colors[], float positions[], float leftTop, float rightTop, float rightBottom, float leftBottom, int dir) {
        int x1 = width;
        int y1 = height;
        if (dir == LR) {
            y1 = 0;
        } else if (dir == TB) {
            x1 = 0;
        }
        return drawableLinearGradient(width, height, colors, positions, new int[]{0, 0, x1, y1}, leftTop, rightTop, rightBottom, leftBottom, dir);
    }

    public static Bitmap drawableLinearGradient(int width, int height, int colors[], float positions[], int[] size, float leftTop, float rightTop, float rightBottom, float leftBottom, int dir) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0x00000000);

        LinearGradient gradient = new LinearGradient(size[0], size[1], size[2], size[3], colors, positions, Shader.TileMode.CLAMP);
        paint.setShader(gradient);

        if (!(leftBottom == 0 && rightBottom == 0 && leftTop == 0 && rightTop == 0)) {
            Path path = new Path();
            float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
            radiusArray[0] = leftTop;
            radiusArray[1] = leftTop;
            radiusArray[2] = rightTop;
            radiusArray[3] = rightTop;
            radiusArray[4] = rightBottom;
            radiusArray[5] = rightBottom;
            radiusArray[6] = leftBottom;
            radiusArray[7] = leftBottom;
            path.addRoundRect(new RectF(0, 0, width, height), radiusArray, Path.Direction.CW);
            canvas.clipPath(path);
        }
        canvas.drawPaint(paint);
        return bitmap;
    }

}
