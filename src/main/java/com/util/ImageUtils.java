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

    private static void machineImage(String imagePath, String toCompressImagePath, ImageType toImageType, Integer width, Integer height, Float quality) {
        BufferedImage bufferedImage = getBufferedImage(imagePath);
        if (bufferedImage == null) {
            throw new RuntimeException("Not Load "+imagePath+" Image");
        }
        if(toImageType == null){
            toImageType  = getImageType(imagePath);
        }

        BufferedImage drawImageDoneBufferedImage = getDrawImageDoneBufferedImage(bufferedImage,toImageType,width,height);
        write(drawImageDoneBufferedImage,toCompressImagePath,toImageType,quality);
    }

    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType, Float quality) {
        machineImage(imagePath, toCompressImagePath, toImageType,null,null, quality);
    }
    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType, Integer width, Integer height) {
        machineImage(imagePath, toCompressImagePath, toImageType,width,height, null);
    }
    public static void convertImage(String imagePath, String toCompressImagePath, ImageType toImageType) {
        machineImage(imagePath, toCompressImagePath, toImageType,null,null, null);
    }

    public static void convertImage(String imagePath, String toCompressImagePath, Integer width, Integer height) {
        machineImage(imagePath, toCompressImagePath, null,width,height, null);
    }

    public static void convertImage(String imagePath, String toCompressImagePath, Float quality) {
        machineImage(imagePath, toCompressImagePath, null,null,null, quality);
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

    public static ImageType getImageType(File imageFile) {
        if (!imageFile.exists()) {
            throw new RuntimeException("PathFile --> " + imageFile + "Not Exists");
        }
        try (FileInputStream io = new FileInputStream(imageFile)) {
            byte[] bytes = new byte[8];
            io.read(bytes);
            String hexString = bytesToHexString(bytes);
            if (isJpg(hexString)) {
                return ImageType.JPEG;
            } else if (isPng(hexString)) {
                return ImageType.PNG;
            } else if (isGif(hexString)) {
                return ImageType.GIF;
            } else if (isBmp(hexString)) {
                return ImageType.BMP;
            } else if (isTiff(hexString)) {
                return ImageType.TIFF;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageType getImageType(String imagePath) {
        if (StrUtil.isBlank(imagePath)) {
            throw new RuntimeException("Image Path Not Empty");
        }
        return getImageType(new File(imagePath));
    }

    private static void write(BufferedImage image, String saveImagePath, ImageType toImageType) {
        write(image,  saveImagePath,  toImageType, null);
    }

    private static void write(BufferedImage image, String saveImagePath, ImageType toImageType,Float quality) {
        String type = toImageType.getType();
        if (quality != null) {
            ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(type).next();
            ImageWriteParam param = imageWriter.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(param.getCompressionTypes()[0]);
            param.setCompressionQuality(quality);

            try (FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(saveImagePath + "." + type))) {
                imageWriter.setOutput(fileImageOutputStream);
                imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), param);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                imageWriter.dispose();
            }
        }else{
            try {
                ImageIO.write(image, type, new File(saveImagePath + "." + type));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private static BufferedImage getDrawImageDoneBufferedImage(BufferedImage targetImage, ImageType imageType, Integer width, Integer height) {
        Integer targetWidth = width;
        Integer targetHeight = height;
        if(targetWidth == null){
            targetWidth = targetImage.getWidth();
        }
        if(targetHeight == null){
            targetHeight = targetImage.getWidth();
        }
        BufferedImage result = new BufferedImage(targetWidth, targetHeight, imageType == ImageType.PNG ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(targetImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return result;
    }

    private static BufferedImage getDrawImageDoneBufferedImage(BufferedImage targetImage, ImageType imageType) {
        return getDrawImageDoneBufferedImage(targetImage, imageType, null, null);
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
