package com.yn.framework.animation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.yn.framework.R;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.file.FileUtil;

/**
 * Created by youjiannuo on 2018/10/25.
 * Email by 382034324@qq.com
 */

public class CropImageActivity extends YNCommonActivity {

    private CropImageView mCropImageView;
    private String mFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_crop_image);
    }

    @Override
    protected void initData() {
        super.initData();
        mFileName = getIntentString("file");
    }

    @Override
    protected void initView() {
        super.initView();
        mCropImageView = findView(R.id.cropImageView);
    }

    @Override
    protected void setViewData() {
        super.setViewData();
        mCropImageView.setGuidelines(CropImageView.DEFAULT_GUIDELINES);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setImageBitmap(BitmapFactory.decodeFile(mFileName));
    }

    public void onCropClick(View v) {
        Bitmap bitmap = mCropImageView.getCroppedImage();
        FileUtil.saveBitmapInFile(bitmap , mFileName);
        setResult(RESULT_OK , getIntent());
        finish();
    }

    public static void open(YNCommonActivity activity, String fileName, int requestCode) {
        activity.openNewActivityForResult(CropImageActivity.class, requestCode, "file", fileName);
    }

}
