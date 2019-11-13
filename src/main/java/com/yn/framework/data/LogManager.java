package com.yn.framework.data;


import com.yn.framework.file.FileUtil;
import com.yn.framework.system.SystemUtil;
import com.yn.framework.system.TimeUtil;
import com.yn.framework.system.Tool;
import com.yn.framework.thread.YNAsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by youjiannuo on 17/4/21.
 */

public class LogManager {
    //存储日子的文件夹
    private final static String LOG_PATH = SystemUtil.getSDCardPath() + "/1/log/";
    private final static String LOG_ZIP_PATH = LOG_PATH + "zip.zip";

    public static void addLog(String log) {
        new YNAsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... v) {
                if (v == null || v.length == 0) {
                    return null;
                }
                Date nowDate = new Date();
                Date date = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate());
                String path = LOG_PATH + date.getTime() + ".log";
                FileUtil.append("\n" + TimeUtil.getNowTime() + "\t:" + v[0] + "\n", path);
                return null;
            }
        }.executeOnExecutor(log);

    }

    public static boolean isHaveLogFile() {
        File[] files = FileUtil.getAllFileOrderByTime(LOG_PATH);
        return !(files == null || files.length == 0);
    }



    public static void getAllFile(final OnLogFileListener l) {
        new YNAsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... v) {
                //获取最近七天的日志
                if (!new File(LOG_ZIP_PATH).exists()) {
                    int num = 0;
                    File[] files = FileUtil.getAllFileOrderByTime(LOG_PATH);
                    if (files == null || files.length == 0) {
                        if (l != null) {
                            l.onLogFile("");
                        }
                        return "";
                    }
                    List<String> fileList = new ArrayList<>();
                    for (File file : files) {
                        fileList.add(file.getPath());
                        if ((++num) == 7) {
                            break;
                        }
                        SystemUtil.printlnInfo("time = " + file.lastModified());
                    }
                    String[] f = new String[fileList.size()];
                    fileList.toArray(f);
                    Tool.newZipFiles(LOG_ZIP_PATH, f);
                }
                return LOG_ZIP_PATH;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                if (l != null) {
                    l.onLogFile(aVoid);
                }
            }
        }.executeOnExecutor();
    }


    //删除压缩包
    public static void delZIP() {
        new File(LOG_ZIP_PATH).delete();
    }

    //删除所有文件
    public static void delAllFile() {
        FileUtil.deleteAllFile(LOG_PATH);
    }


    public interface OnLogFileListener {
        void onLogFile(String path);
    }

}
