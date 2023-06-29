package com.util;

import cn.hutool.core.util.StrUtil;
import com.constants.ImageConstants;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * @author yuanmengfan
 * @date 2023/6/25 22:31
 * @description
 */
@Slf4j
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

    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType, Long specifySize, FileUtils.CapacityUnit specifyType) throws IOException {
        File file = new File(imagePath);
        BufferedImage bufferedImage = getBufferedImage(file);
        if (bufferedImage == null) {
            throw new RuntimeException("Not Load " + imagePath + " Image");
        }
        if (toImageType == null) {
            toImageType = getImageType(imagePath);
        }
        BufferedImage saveImage = getDrawImageDoneBufferedImage(bufferedImage, toImageType);

        BigDecimal quality = new BigDecimal("1.0");
        long size = file.length();
        log.info("压缩前大小" + size);

        long maxLength = specifySize * specifyType.getByteSize();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        do {
            ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(toImageType.getType()).next();
            ImageWriteParam param = imageWriter.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality.floatValue());
            try {
                output.reset();
                writeOutput(saveImage, imageWriter, param, output);
                size = output.size();
                log.info("压缩后大小:{}。压缩质量：{}", size, quality);
                quality = quality.add(new BigDecimal("-0.1"));
            } finally {
                imageWriter.dispose();
            }
        } while (size > maxLength && quality.floatValue() > 0);

        if (size > maxLength) {
            log.warn("压缩指定大小失败！");
        }

        FileUtils.writeFile(new File(toCompressImagePath + "." + toImageType.getType()), output.toByteArray());
    }

    public static void compressImage(String imagePath, String toCompressImagePath, ImageType toImageType, Float scale, int i) throws IOException {
        BufferedImage bufferedImage = getBufferedImage(imagePath);
        if (bufferedImage == null) {
            throw new RuntimeException("Not Load " + imagePath + " Image");
        }
        if (toImageType == null) {
            toImageType = getImageType(imagePath);
        }
        int width = (int) (bufferedImage.getWidth() * scale);
        int height = (int) (bufferedImage.getHeight() * scale);

        Image scaledInstance = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);

        BufferedImage drawImageDoneBufferedImage = getDrawImageDoneBufferedImage(scaledInstance, toImageType);

        writeUnChangeQuality(drawImageDoneBufferedImage, toCompressImagePath, toImageType);
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
        try (FileOutputStream output = new FileOutputStream(saveImagePath + "." + type)) {
            writeOutput(image, imageWriter, param, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeOutput(BufferedImage image, ImageWriter imageWriter, ImageWriteParam param, OutputStream output) throws IOException {
        try {
            imageWriter.setOutput(ImageIO.createImageOutputStream(output));
            imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), param);
        } catch (IOException e) {
            throw e;
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

    private static BufferedImage getDrawImageDoneBufferedImage(Image targetImage, ImageType imageType, Integer width, Integer height) {
        Integer targetWidth = width;
        Integer targetHeight = height;
        if (targetWidth == null) {
            targetWidth = targetImage.getWidth(null);
        }
        if (targetHeight == null) {
            targetHeight = targetImage.getHeight(null);
        }

        int colorType = imageType == ImageType.PNG ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage result = new BufferedImage(targetWidth, targetHeight, colorType);

        Graphics2D graphics = (Graphics2D) result.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



        graphics.drawImage(targetImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return result;
    }

    private static BufferedImage getDrawImageDoneBufferedImage(Image targetImage, ImageType imageType) {
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
}
