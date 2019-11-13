package com.yn.framework.system;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by youjiannuo on 18/4/25.
 * Email by 382034324@qq.com
 */

public class MediaPlayerController {

    private Context mContext;

    public MediaPlayerController(Context context) {
        mContext = context;
    }


    public void play(String fileName, final OnPlayStatusListener l) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        AssetManager assetManager = mContext.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor()
                    , fileDescriptor.getStartOffset()
                    , fileDescriptor.getStartOffset());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (l != null) {
                        l.playOver(mediaPlayer);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public interface OnPlayStatusListener {
        //播放结束
        void playOver(MediaPlayer mediaPlayer);
    }


    class Task {
        Object data;
    }

}
