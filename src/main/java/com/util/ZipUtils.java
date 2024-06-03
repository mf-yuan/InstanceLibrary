package com.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author yuanmengfan
 * @date 2024/1/23 21:59
 * @description
 */
public class ZipUtils {


    public static void zip(){
        try {
            ZipInputStream zipInputStream = new ZipInputStream(
                    new FileInputStream(new File("C:\\Users\\EDY\\Desktop\\个人学习.zip")),
                    Charset.forName("GBK")
            );
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("222.zip"));
            ZipEntry entry = null;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                System.out.println(name);

                zipOutputStream.putNextEntry(entry);

                byte[] bytes = zipInputStream.readAllBytes();
                zipOutputStream.write(bytes);
                // FileOutputStream fileOutputStream = new FileOutputStream(new File(entry.getName()));
                // fileOutputStream.write(bytes);
                zipInputStream.closeEntry();

                // zipOutputStream.w
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ZipUtils.zip();
    }
}
