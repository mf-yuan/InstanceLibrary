package com.constants;

/**
 * @author yuanmengfan
 * @date 2023/6/25 23:01
 * @description
 *  JPEG/JPG: 文件头标识字节为 FF D8 FF
 *
 *  PNG: 文件头标识字节为 89 50 4E 47 0D 0A 1A 0A
 *
 *  GIF: 文件头标识字节为 47 49 46 38
 *
 *  BMP: 文件头标识字节为 42 4D
 *
 *  TIFF: 文件头标识字节为 49 49 2A 00 或 4D 4D 00 2A
 */
public interface ImageConstants {
    String JPG_START_HEAD = "ffd8ff";
    String PNG_START_HEAD = "89504e470d0a1a0a";
    String GIF_START_HEAD = "47494638";
    String BMP_START_HEAD = "424d";
    String TIFF_START_HEAD_1 = "49492a00";
    String TIFF_START_HEAD_2 = "4d002a";
}
