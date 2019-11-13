package com.yn.framework.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by youjiannuo on 2018/8/6.
 * Email by 382034324@qq.com
 */
public class Tool {

    public static void newZipFiles(String outputZipFile, String[] files) {
        BufferedInputStream br = null;
        try {
            FileOutputStream fos = new FileOutputStream(outputZipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.setMethod(ZipOutputStream.DEFLATED);
            byte[] data = new byte[1024];
            for (int i = 0; i < files.length; i++) {
                // System.out.println("Adding: " + files[i]);
                FileInputStream fi = new FileInputStream(files[i]);
                br = new BufferedInputStream(fi, 1024);
                ZipEntry entry = new ZipEntry(files[i].substring(files[i]
                        .lastIndexOf(File.separator)));
                zos.putNextEntry(entry);
                int count;
                while ((count = br.read(data, 0, 1024)) != -1) {
                    zos.write(data, 0, count);
                }
                zos.closeEntry();
                br.close();
                fi.close();
                fi = null;
                br = null;
            }
            zos.close();
            zos = null;
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br = null;
            }
        }
    }

}
