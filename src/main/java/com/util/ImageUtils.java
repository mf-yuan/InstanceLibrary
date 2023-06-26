package com.util;

import cn.hutool.core.util.StrUtil;
import com.constants.ImageConstants;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author yuanmengfan
 * @date 2023/6/25 22:31
 * @description
 */
public class ImageUtils {


    /**
     * 图片类型
     */
    enum ImageType {
        JPEG("jpg"),
        PNG("png"),
        GIF("gif"),
        BMP("bmp"),
        TIFF("tiff");

        private final String type;

        ImageType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType,float quality) {
        BufferedImage bufferedImage = getBufferedImage(imagePath);
        if (bufferedImage == null) {
            return;
        }

        String type = toImageType.getType();
        BufferedImage toCompressBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics graphics = toCompressBufferedImage.getGraphics();
        graphics.drawImage(bufferedImage,0,0,null);
        graphics.dispose();

        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(type).next();
        ImageWriteParam param = imageWriter.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(param.getCompressionTypes()[0]);
        param.setCompressionQuality(quality);


        try (FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(toCompressImagePath + "." + type))) {
            imageWriter.setOutput(fileImageOutputStream);
            imageWriter.write(null, new javax.imageio.IIOImage(toCompressBufferedImage, null, null), param);
            imageWriter.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void convertImage(String imagePath, String toCompressImagePath, ImageType toImageType) {
        BufferedImage bufferedImage = getBufferedImage(imagePath);
        if (bufferedImage == null) {
            return;
        }

        String type = toImageType.getType();
        BufferedImage toCompressBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics graphics = toCompressBufferedImage.getGraphics();
        graphics.drawImage(bufferedImage,0,0,null);
        graphics.dispose();

        try {
            ImageIO.write(toCompressBufferedImage,type,new File(toCompressImagePath+"."+type));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getBufferedImage(String imagePath) {
        File imageFile = new File(imagePath);
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getImageType(File imageFile) {
        if (!imageFile.exists()) {
            return null;
        }
        try (FileInputStream io = new FileInputStream(imageFile)) {
            byte[] bytes = new byte[8];
            io.read(bytes);
            String hexString = bytesToHexString(bytes);
            System.out.println(hexString);
            if (isJpg(hexString)) {
                return ImageType.JPEG.getType();
            } else if (isPng(hexString)) {
                return ImageType.PNG.getType();
            } else if (isGif(hexString)) {
                return ImageType.GIF.getType();
            } else if (isBmp(hexString)) {
                return ImageType.BMP.getType();
            } else if (isTiff(hexString)) {
                return ImageType.TIFF.getType();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImageType(String imagePath) {
        if (StrUtil.isBlank(imagePath)) {
            return null;
        }
        return getImageType(new File(imagePath));
    }


    private static String bytesToHexString(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

    private static boolean isJpg(String hexStr) {
        return hexStr.startsWith(ImageConstants.JPG_START_HEAD);
    }

    private static boolean isPng(String hexStr) {
        return hexStr.startsWith(ImageConstants.PNG_START_HEAD);
    }

    private static boolean isGif(String hexStr) {
        return hexStr.startsWith(ImageConstants.GIF_START_HEAD);
    }

    private static boolean isBmp(String hexStr) {
        return hexStr.startsWith(ImageConstants.BMP_START_HEAD);
    }

    private static boolean isTiff(String hexStr) {
        return hexStr.startsWith(ImageConstants.TIFF_START_HEAD_1) || hexStr.startsWith(ImageConstants.TIFF_START_HEAD_2);
    }
}
