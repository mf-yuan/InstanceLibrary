package com.util;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author yuanmengfan
 * @date 2023/3/30 23:34
 * @description
 */
public class FileUtils {

    /**
     * 将文件转换为字节数组
     *
     * @param file 文件
     * @return 字节数组
     */
    public static byte[] fileToBytes(File file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            return streamToBytes(inputStream);
        } finally {
            inputStream.close();
        }
    }


    /**
     * 从文件中读取内容并返回字符串
     *
     * @param file 文件
     * @return 文件内容字符串
     */
    public static String readFromFile(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            return readerToString(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * 向文件中写入内容
     *
     * @param file    文件
     * @param content 内容
     */
    public static void writeFile(File file, String content) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(content);
        } finally {
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    /**
     * 向文件中写入字节数组
     *
     * @param file  文件
     * @param bytes 内容
     */
    public static void writeFile(File file, byte[] bytes) throws IOException {
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(bytes);
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }


    /**
     * 字节输入流转为字节数组
     *
     * @param inputStream 字节输入流
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] streamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputStream.available());
        byte[] buffer = new byte[2048];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    /**
     * 字符输入流转为字符串
     *
     * @param reader 字符输入流
     * @return
     * @throws IOException
     */
    public static String readerToString(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = new char[1024];
        int len;
        while ((len = reader.read(chars)) != -1) {
            stringBuilder.append(chars, 0, len);
        }
        return stringBuilder.toString();
    }

    /**
     * 复制文件或目录
     *
     * @param file        源文件或目录
     * @param target      目标文件或目录
     * @param isOverwrite 针对文件夹处理
     *                    如果要覆盖的话则文件夹中的所有文件替换为file中的文件
     *                    否则会保留原有文件夹中的文件
     * @throws IOException 复制异常
     */
    public static void copy(File file, File target, boolean isOverwrite) throws IOException {
        if (Objects.isNull(file) || Objects.isNull(target)) {
            return;
        }
        if (!file.exists()) {
            return;
        }
        if (isOverwrite) {
            delete(target);
        }
        if (file.isDirectory()) {
            createFolder(target);
            File[] files = file.listFiles();
            for (File srcFile : files) {
                File destFile = new File(target, srcFile.getName());
                copy(srcFile, destFile);
            }
        } else {
            writeFile(target, fileToBytes(file));
        }
    }

    /**
     * 复制文件或目录 默认不做覆盖处理
     *
     * @param file   源文件或目录
     * @param target 目标文件或目录
     * @throws IOException 复制异常
     */
    public static void copy(File file, File target) throws IOException {
        copy(file, target, false);
    }

    /**
     * 移动文件或目录
     *
     * @param file        源文件或目录
     * @param target      目标文件或目录
     * @param isOverwrite 针对文件夹处理
     *                    如果要覆盖的话则文件夹中的所有文件替换为file中的文件
     *                    否则会保留原有文件夹中的文件
     * @throws IOException 复制异常
     */
    public static void move(File file, File target, boolean isOverwrite) throws IOException {
        copy(file, target, isOverwrite);
        delete(file);
    }

    /**
     * 移动文件或目录 默认不做覆盖处理
     *
     * @param file   源文件或目录
     * @param target 目标文件或目录
     * @throws IOException 复制异常
     */
    public static void move(File file, File target) throws IOException {
        move(file, target, false);
    }


    /**
     * 删除文件或目录
     *
     * @param target 目标文件或目录
     */
    public static void delete(File target) {
        if (Objects.isNull(target)) {
            return;
        }
        if (!target.exists()) {
            return;
        }
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            for (File file : files) {
                delete(file);
            }
        }
        target.delete();
    }

    /**
     * 列出指定目录下的所有文件和子目录。返回一个包含所有文件和子目录的 List
     *
     * @param directory  源文件夹
     * @param fileFilter 过滤器
     * @return
     */
    public static List<File> listFiles(File directory, FileFilter fileFilter) {
        List<File> result = new ArrayList<>();
        result.add(directory);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles(fileFilter);
            for (File file : files) {
                result.addAll(listFiles(file));
            }
        }
        return result;
    }


    /**
     * 无过滤
     * 列出指定目录下的所有文件和子目录。返回一个包含所有文件和子目录的 List
     *
     * @param directory 源文件夹
     * @return
     */
    public static List<File> listFiles(File directory) {
        return listFiles(directory, null);
    }


    /**
     * 过滤Mac系统自带的隐藏文件 .DS_Store
     * 列出指定目录下的所有文件和子目录。返回一个包含所有文件和子目录的 List
     *
     * @param directory 源文件夹
     * @return
     */
    public static List<File> listFilesFilterDsStore(File directory) {
        return listFiles(directory, (file) -> {
            if (!".DS_Store".equals(file.getName())) {
                return true;
            }
            return false;
        });
    }


    /**
     * 获取文件后缀
     *
     * @param file 源文件
     * @return
     */
    public static String getFileSuffix(File file) {
        if (Objects.isNull(file)) {
            return null;
        }
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1);
    }


    /**
     * 创建目录
     *
     * @param file 目录
     */
    public static void createFolder(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 判断文件路径是否为本地文件
     *
     * @param filePath 文件路径
     * @return 是否为本地文件
     */
    public static boolean isLocalFile(String filePath) {
        try {
            URI uri = new URI(filePath);
            String scheme = uri.getScheme();
            return "file".equals(scheme);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    /**
     * 获取文件或文件夹的大小 默认单位为字节
     *
     * @param target 源文件或文件夹
     * @return
     */
    public static long size(File target) {
        long result = 0;
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            for (File file : files) {
                result += size(file);
            }
        }
        result += target.length();
        return result;
    }

    /**
     * 获取文件或文件夹的大小
     *
     * @param target       源文件或文件夹
     * @param capacityUnit 单位
     * @return
     */
    public static double size(File target, CapacityUnit capacityUnit) {
        return (double) size(target) / (1 << (capacityUnit.getPow() * 10));
    }


    /**
     * 设置文件或文件夹为隐藏
     *
     * @param target 源文件或文件夹
     * @throws IOException
     */
    public static void hide(File target) throws IOException {
        // 已经是隐藏文件了
        if (target.getName().indexOf(".") == 0) {
            return;
        }
        move(target, new File(target.getParent(), "." + target.getName()));
    }

    /**
     * 设置文件或文件夹为可见
     *
     * @param target 源文件或文件夹
     * @throws IOException
     */
    public static void show(File target) throws IOException {
        // 不是隐藏的文件
        if (target.getName().indexOf(".") != 0) {
            return;
        }
        move(target, new File(target.getParent(), target.getName().substring(1)));
    }

    /**
     * 计算机容量
     */
    enum CapacityUnit {
        /**
         * 字节
         */
        BIT(0),
        /**
         * 千字节 1024 字节
         */
        KB(1),
        /**
         * 兆字节 1024 * 1024
         */
        MB(2),
        /**
         * 京字节 1024 * 1024 * 1024
         */
        GB(3),
        /**
         * 1024 * 1024 * 1024 * 1024
         */
        TB(4);

        /**
         * 1024 的 平方
         */
        private Integer pow;

        CapacityUnit(Integer pow) {
            this.pow = pow;
        }

        public Integer getPow() {
            return pow;
        }
    }
}
