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
import java.math.BigDecimal;

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

    private static void compress(String imagePath, String toCompressImagePath, ImageType toImageType, Integer width, Integer height, Float quality) {
        BufferedImage bufferedImage = getBufferedImage(imagePath);
        if (bufferedImage == null) {
            throw new RuntimeException("Not Load " + imagePath + " Image");
        }
        if (toImageType == null) {
            toImageType = getImageType(imagePath);
        }

        BufferedImage drawImageDoneBufferedImage = getDrawImageDoneBufferedImage(bufferedImage, toImageType, width, height);

        write(drawImageDoneBufferedImage, toCompressImagePath, toImageType, quality);
    }


    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType, Float quality) {
        compress(imagePath, toCompressImagePath, toImageType, null, null, quality);
    }

    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType, Integer width, Integer height) {
        compress(imagePath, toCompressImagePath, toImageType, width, height, null);
    }

    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType) {
        compress(imagePath, toCompressImagePath, toImageType, null, null, null);
    }

    public static void compressImage(String imagePath, String toCompressImagePath, Integer width, Integer height) {
        compress(imagePath, toCompressImagePath, null, width, height, null);
    }

    public static void compressImage(String imagePath, String toCompressImagePath, Float quality) {
        compress(imagePath, toCompressImagePath, null, null, null, quality);
    }

    public static void compressImage(String imagePath, String toCompressImagePath, Long specifySize) throws IOException {
        File file = new File(imagePath);
        BufferedImage bufferedImage = getBufferedImage(file);
        if (bufferedImage == null) {
            throw new RuntimeException("Not Load " + imagePath + " Image");
        }
        ImageType toImageType = getImageType(imagePath);

        BufferedImage saveImage = getDrawImageDoneBufferedImage(bufferedImage, toImageType);



        BigDecimal quality = new BigDecimal("1.0");
        long size = file.length();
        System.out.println("压缩前大小" + size);
        while (size > specifySize && quality.floatValue() > 0) {
            ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(toImageType.getType()).next();
            ImageWriteParam param = imageWriter.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality.floatValue());

            try (FileImageOutputStream output = new FileImageOutputStream(new File(toCompressImagePath + quality +"." + toImageType.getType()))) {
                imageWriter.setOutput(output);
                imageWriter.write(null, new javax.imageio.IIOImage(saveImage, null, null), param);
                size = output.length();
                System.out.println("压缩后大小" + size);
                System.out.println("压缩质量" + quality);
                bufferedImage.flush();
                saveImage.flush();
                output.flush();
                System.out.println(output.isCached());
                quality = quality.add(new BigDecimal("-0.1"));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                imageWriter.dispose();
            }
            saveImage.flush();
        }
//        BufferedImage saveImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
//                BufferedImage.TYPE_INT_ARGB);
//        float quality = 1.0f;
//        long compressedSize = Long.MAX_VALUE;
//        while (compressedSize > specifySize && quality > 0) {
//            // Perform image compression
//            Graphics2D g2d = saveImage.createGraphics();
//            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
//                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            // Set the image compression quality
//            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
//            ImageWriter writer = writers.next();
//            ImageWriteParam writeParam = writer.getDefaultWriteParam();
//            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//            writeParam.setCompressionQuality(quality);
//
//            // Perform image compression
//            ImageOutputStream outputStream = ImageIO.createImageOutputStream(new File(toCompressImagePath + "." + toImageType.getType()));
//            writer.setOutput(outputStream);
//            writer.write(null, new IIOImage(saveImage, null, null), writeParam);
//            outputStream.close();
//
//            // Calculate the compressed image file size
//            compressedSize = new File(toCompressImagePath + "." + toImageType.getType()).length();
//
//            System.out.println(compressedSize);
//            // Adjust the image quality
//            quality -= 0.1f;
//        }
//        System.out.println("Image compression successful!");
    }

    public static BufferedImage getBufferedImage(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage getBufferedImage(String imagePath) {
        return getBufferedImage(new File(imagePath));
    }

    public static ImageType getImageType(File imageFile) {
        if (!imageFile.exists()) {
            throw new RuntimeException("PathFile --> " + imageFile + "Cannot Exists");
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
            throw new RuntimeException("Image Path Cannot Empty");
        }
        return getImageType(new File(imagePath));
    }

    private static void writeUnChangeQuality(BufferedImage image, String saveImagePath, ImageType toImageType) {
        String type = toImageType.getType();
        try {
            ImageIO.write(image, type, new File(saveImagePath + "." + type));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeChangeQuality(BufferedImage image, String saveImagePath, ImageType toImageType, Float quality) {
        String type = toImageType.getType();
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(type).next();
        ImageWriteParam param = imageWriter.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);


        try (FileImageOutputStream output = new FileImageOutputStream(new File(saveImagePath + "." + type))) {
            imageWriter.setOutput(output);
            imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), param);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            imageWriter.dispose();
        }
    }

    private static void write(BufferedImage image, String saveImagePath, ImageType toImageType, Float quality) {
        if (quality == null) {
            writeUnChangeQuality(image, saveImagePath, toImageType);
        } else {
            writeChangeQuality(image, saveImagePath, toImageType, quality);
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
        if (targetWidth == null) {
            targetWidth = targetImage.getWidth();
        }
        if (targetHeight == null) {
            targetHeight = targetImage.getHeight();
        }

        int colorType = imageType == ImageType.PNG ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage result = new BufferedImage(targetWidth, targetHeight, colorType);

        Graphics2D graphics = (Graphics2D) result.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
