package com.it.tinkercustom_exercise.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static final String dexDirName = "odexDir";

    /**
     * 将文件从assets目录拷贝至私有目录下
     * @param context
     * @param fileName
     * @return
     */
    public static File copyAssetsAndWrite(Context context, String fileName) {
        try {
            //            File cacheDir = context.getCacheDir();
            //data/user/0/包名/app_odexDir
            File cacheDir = context.getDir(dexDirName, Context.MODE_PRIVATE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File outFile = new File(cacheDir, fileName);

            //为了测试，方便测试过程中修改classes2.dex 每次都能刷新私有目录下的dex文件
            if (outFile.exists()) {
                outFile.delete();
            }

            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (res) {
                    InputStream is = context.getAssets().open(fileName);
                    FileOutputStream fos = new FileOutputStream(outFile);
                    byte[] buffer = new byte[is.available()];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                    Log.d("TAG:", "文件拷贝到私有目录成功" + outFile.getAbsolutePath());
                    return outFile;
                }
            } else {
                Log.d("TAG:", "私有目录下文件已经存在" + outFile.getAbsolutePath());
                return outFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @throws IOException IO异常
     */
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        // 关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }
}
