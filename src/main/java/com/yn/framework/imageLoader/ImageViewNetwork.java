package com.yn.framework.imageLoader;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yn.framework.thread.YNAsyncTask;

import java.util.Date;


public class ImageViewNetwork extends ImageView {


    //当前任务执行的时间
    private long mLastRunTime = 0;

    private NetworkPhotoTask mTask = null;

    //缓存图片机制
    private BitmapCacheManager mBitmapCacheManager = BitmapCacheManager.BITMAP_CACHE_MANAGER;
    //当前正在执行的任务
    private Asy mAsy = null;
    //任务队列
    private TaskQueue mTaskQueue = TaskQueue.TASK_QUEUE;

    private BitmapCacheManager getBitmapCacheManager() {
        return BitmapCacheManager.BITMAP_CACHE_MANAGER;
    }

    //处理图片
    private BitmapDealManager mBitmapDealManager = new BitmapDealManager();

    public ImageViewNetwork(Context context) {
        super(context);
    }

    public ImageViewNetwork(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkPhotoTask getNetworkPhotoTask(String url, int width, int height) {
        if (mTask == null) {
            mTask = new NetworkPhotoTask();
        } else {
            mTask.clear();
        }
        mTask.height = height;
        mTask.width = width;
        mTask.url = url;
        return mTask;
    }

    public void refresh() {
        if (mTask != null) {
            setImageParams(mTask);
        }
    }

    //开始加载照片
    public void setImageParams(NetworkPhotoTask task) {

        if (!check()) return;
        //判断当然任务是否是一样的
        if (mLastRunTime != 0
                && mTask != null
                && mTask.equals(task)
                && new Date().getTime() - mLastRunTime < 100) {
            Util.println("任务相同提前结束");
            return;
        }
        mLastRunTime = new Date().getTime();
        mTask = task;
        task.v = this;
        //停止任务
        stopTask();
        //是否缓存里面有图片
        if (mTask.isGetImageFromCache) {
            Bitmap bitmap = mBitmapCacheManager.getCacheBitmap(task.getPhotoKey());
            if (bitmap != null) {
                Util.println("获取缓存图片:" + mTask.url + "    " + task.getPhotoKey());
                setImageBitmap(bitmap);
                return;
            }
        }
        if (mTask.imageLoaderOperationListener != null) {
            //目前图片处在ListView里面,正在高速滑动，不需要加载图片
            if (mTask.imageLoaderOperationListener.getStatue() == ImageLoaderScrollerObservation.TYPE_STOP) {
                Util.println("ListView正在滑动无法加载图片");
                setDefaultResource();
                return;
            }
        }

        //添加到服务器里面去
        mTaskQueue.addTask(this);
        startTask();
    }

    public void startTask() {
        //准备开启线程
        //是否有本地是否存在照片
        if (getBitmapCacheManager().isFileExit(mTask)) {
            //本地有照片
            startNativeTask();
        } else {
            startNetworkTask();
        }
    }


    private boolean check() {
        //检测手机存储是否存在
        if (Util.checkSDCardAvailable()) {
            return true;
        }
        return false;
    }

    public void stopTask() {
        //停止
        if (mAsy != null) {
            mAsy.onCancelled();
            mAsy.cancel(false);
            mAsy = null;
        }
    }

    private void startNativeTask() {
        mAsy = new NativeAsy();
        try {
            mAsy.executeOnExecutor(mTask);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startNetworkTask() {
        mAsy = new NetworkAsy();
        try {
            mAsy.execute1(mTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NetworkPhotoTask getTask() {
        return mTask;
    }

    class NetworkAsy extends Asy {

        @Override
        public String getBitmapFileName(NetworkPhotoTask task) {
            if (isCancel()) {
                return null;
            }
            return (mAccessNetwork = new AccessNetwork()).getBitmapFromNetwork(task.getPhotoName(), task.url, this, 0);
        }
    }

    class NativeAsy extends Asy {
        @Override
        public String getBitmapFileName(NetworkPhotoTask task) {
            return task.getPhotoName();
        }

        @Override
        public void setErrorBitmap() {
            super.setErrorBitmap();
            //停止
            if (mAsy != null) {
                mAsy.onCancelled();
                mAsy = null;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    startNetworkTask();
                }
            });

        }

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null && bm.isRecycled()) {
            return;
        }
        super.setImageBitmap(bm);

    }

    private void setDefaultResource() {
        if (mTask.startDrawId != -1) {
            if (mTask.isSetRounded) {
                Bitmap bitmap = mBitmapCacheManager.getCacheBitmap(mTask.startDrawId + "");
                if (bitmap == null) {
                    //设置圆形
                    bitmap = BitmapFactory.decodeResource(getResources(), mTask.startDrawId);
                    bitmap = ImageUtil.getRoundedBitmap(bitmap);
                    mBitmapCacheManager.addCacheBitmap(mTask.startDrawId + "", bitmap);
                }
                setImageBitmap(bitmap);
            } else {
                setImageResource(mTask.startDrawId);
            }
        }
    }

    abstract class Asy extends YNAsyncTask<NetworkPhotoTask, Integer, Bitmap> implements AsyPublish {


        ViewGroup.LayoutParams lp;
        boolean isStop = false;
        public NetworkPhotoTask task = null;
        protected AccessNetwork mAccessNetwork = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setDefaultResource();
        }

        @Override
        protected Bitmap doInBackground(NetworkPhotoTask... params) {
            // TODO Auto-generated method stub

            Bitmap bitmap = null;
            if (params.length == 0) return null;
            NetworkPhotoTask task = this.task = params[0];
            if (task == null) return null;
            if (task.isGetImageFromSdCard) {
                if (task.fileName == null || task.fileName.trim().length() == 0) return null;
            } else if (task.url == null || task.url.trim().length() == 0) {
                return null;
            }

            if (!isCancel()) {
                String fileName = getBitmapFileName(task);
                if (fileName == null) {
                    //任务停止
                    return null;
                }

                //回调方法，自定义Bitmap的属性
                if (task.onLoaderImageCallback != null) {
                    bitmap = task.onLoaderImageCallback.onCallback(fileName);
                }
                if (bitmap == null) {
                    bitmap = mBitmapCacheManager.getBitmapFromCacheAndNative(task);
                    bitmap = mBitmapDealManager.dealBitmap(bitmap, task);

                    if (bitmap == null) {
                        setErrorBitmap();
                    }
                }

                if (bitmap != null) {
                    //是否需要把图片移到正确位置
                    if (task.isRightDirection) {
                        bitmap = BitmapDealManager.getRightDirectionBitmap(fileName, bitmap);
                    }
                    //是否需要添加缓存到内存里面
                    if (task.isAddToCache) {
                        //add bitmap to cache
                        mBitmapCacheManager.addCacheBitmap(task.getPhotoKey(), bitmap);
                        Util.println("添加图片到缓存里面去:" + task.getPhotoKey());
                    }
                }
            }

            if (bitmap != null) {
                lp = calc(bitmap, task.v);
            }
            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            //任务没有取消
            if (!isCancel()) {
                if (result != null) {
                    setImageBitmap(result);
                } else {
                    if (mTask.errorDrawId != -1) {
                        setImageResource(mTask.errorDrawId);
                    }
                }
                //移除任务
                mTaskQueue.removeTask(ImageViewNetwork.this);
            }

        }


        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub

            if (mAccessNetwork != null) {
                Util.println("取消的任务:" + task.url);
                mAccessNetwork.close();
                mAccessNetwork = null;
            }
            isStop = true;
        }


        @Override
        public void publishProgressDevelopment(Integer... progress) {
            // TODO Auto-generated method stub
            publishProgress(progress);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //图片下载进度显示
            if (mTask.onLoadImageProgress != null) {
                mTask.onLoadImageProgress.onProgress(values[0], mTask.getPhotoName());
            }
        }

        @Override
        public boolean isCancel() {
            // TODO Auto-generated method stub
            return isStop;
        }

        //文件破损了
        public void setErrorBitmap() {

        }

        public abstract String getBitmapFileName(NetworkPhotoTask task);
    }


    @SuppressLint("NewApi")
    private ViewGroup.LayoutParams calc(Bitmap bitmap, View v) {
        if (!(v instanceof ImageView)) return null;
        ImageView imageView = (ImageView) v;
        int width = imageView.getMaxWidth();
        int height = imageView.getMaxHeight();

        if (height == Integer.MAX_VALUE || width == Integer.MAX_VALUE) {
            return imageView.getLayoutParams();
        }

        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

        Log.i("ImageViewNetwork", "view.width = " + width + " view.height = " + height);
        Log.i("ImageViewNetwork", "bitmap.width = " + bWidth + " bitmap.height = " + bHeight);

        final int size = width < height ? height : width;
        final int bsize = bWidth < bHeight ? bHeight : bWidth;
        float scaling = (float) (bsize / (size * 1.0));
        Log.i("ImageViewNetwork", "scaling" + scaling);
        scaling = scaling < 1 ? 1 : scaling;
        Log.i("ImageViewNetwork", "alter scaling" + scaling);
        bWidth /= scaling;
        bHeight /= scaling;

        Log.i("ImageViewNetwork", " bitmap scaling  bitmap.width = " + bWidth + " bitmap.height = " + bHeight);

        boolean iswidth = width > bWidth;
        boolean isheight = height > bHeight;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();


        int w = width;
        int h = height;

        if (iswidth && !isheight) {
            w = bWidth;
        } else if (!iswidth && isheight) {
            h = bHeight;
        } else if (iswidth && isheight) {
            w = bWidth;
            h = bHeight;
        }
        Log.i("ImageViewNetwork", " set View   width = " + w + " bWidth = " + h);
        params.width = w;
        params.height = h;
        return params;
    }

}
