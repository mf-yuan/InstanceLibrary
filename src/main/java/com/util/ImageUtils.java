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
        /**
         * 无透明色的图片 RGB
         */
        JPEG("jpg"),
        /**
         * 有透明色图片 ARGB
         */
        PNG("png"),
        GIF("gif"),
        BMP("bmp"),
        TIFF("tiff");

        private final String name;

        ImageType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * @param imagePath 待处理图片文件路径
     * @param savePath  处理后保存文件的地址 不带后缀
     * @param imageType 保存的文件格式 如果为空则使用 imagePath的文件格式保存
     */
    private static void processImage(String imagePath, String savePath, ImageType imageType, Integer width, Integer height, Float scale, Float quality) {
        BufferedImage bufferedImage = null;
        ImageType targetImageType = imageType;
        try {
            bufferedImage = getBufferedImage(imagePath);
        } catch (IOException e) {
            log.error("Not Load " + imagePath + " Image");
            return;
        }

        if (StrUtil.isBlank(savePath)) {
            log.error("SavePath Cannot Empty");
            return;
        }

        if (targetImageType == null) {
            // 尝试获取原图片的图片类型 没有获取到则返回
            targetImageType = getImageType(imagePath);
            if (targetImageType == null) {
                log.error("Unable to obtain the type of saved image");
                return;
            }
        }

        if(scale != null && scale <= 0){
            log.error("Scaling value needs to be greater than 0 ");
            return;
        }

        if(quality != null && quality <= 0){
            log.error("Quality value needs to be greater than 0 ");
            return;
        }

        BufferedImage targetImage = getDrawImageDoneBufferedImage(bufferedImage, imageType, width, height, scale);

        boolean isSuccess = write(targetImage, savePath, imageType, quality);
        if (!isSuccess) {
            log.error("Image write fail");
        }
    }

    /**
     * 根据传入的图片，绘制会一个新的图片
     *
     * @param image     源图片
     * @param imageType 图片类型
     * @param width     宽度
     * @param height    高度
     * @return
     */
    private static BufferedImage getDrawImageDoneBufferedImage(Image image, ImageType imageType, Integer width, Integer height, Float scale) {
        // 当传入的宽度或者高度为空时，或者源图片的值，进行替换
        Integer targetWidth = width;
        Integer targetHeight = height;
        if (targetWidth == null) {
            targetWidth = image.getWidth(null);
        }
        if (targetHeight == null) {
            targetHeight = image.getHeight(null);
        }
        if (scale != null) {
            targetWidth = (int) (targetWidth * scale);
            targetHeight = (int) (targetHeight * scale);
        }
        // 因为 PNG的颜色类型是ARGB的，而JPG的颜色是RGB的，
        int colorType = imageType == ImageType.PNG ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage result = new BufferedImage(targetWidth, targetHeight, colorType);

        // 设置处理图片的细节
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 最后绘制图片
        graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        return result;
    }

    private static BufferedImage getDrawImageDoneBufferedImage(Image targetImage, ImageType imageType) {
        return getDrawImageDoneBufferedImage(targetImage, imageType, null, null,null);
    }

    /**
     * 根据是否传入quality选择不同的写入图片的方式
     *
     * @param image         源图片
     * @param saveImagePath 保存图片的路径
     * @param toImageType   保存图片的类型
     * @param quality       图片质量
     * @return
     */
    private static boolean write(BufferedImage image, String saveImagePath, ImageType toImageType, Float quality) {
        return quality == null ? writeNoChangeQuality(image, saveImagePath, toImageType) :
                writeChangeQuality(image, saveImagePath, toImageType, quality);
    }

    /**
     * 图片无质量变化的时候的写入方法
     *
     * @param image         源文件
     * @param saveImagePath 保存路径
     * @param toImageType   保存图片格式
     * @return
     */
    private static boolean writeNoChangeQuality(BufferedImage image, String saveImagePath, ImageType toImageType) {
        String type = toImageType.getName();
        try {
            return ImageIO.write(image, type, new File(saveImagePath + "." + type));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片有质量变化的时候的写入方法
     * 因为有质量变化的时候需要使用 ImageWriteParam
     *
     * @param image         源文件
     * @param saveImagePath 保存路径
     * @param toImageType   保存图片格式
     * @return
     */
    private static boolean writeChangeQuality(BufferedImage image, String saveImagePath, ImageType toImageType, Float quality) {
        String type = toImageType.getName();
        try (FileOutputStream output = new FileOutputStream(saveImagePath + "." + type)) {
            writeOutput(image, type, quality, output);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将image文件写入文件写入流中
     *
     * @param image   源文件
     * @param type    保存图片格式
     * @param quality 质量
     * @param output  输出流
     * @throws IOException
     */
    private static void writeOutput(BufferedImage image, String type, Float quality, OutputStream output) throws IOException {
        ImageWriter imageWriter = null;
        try {
            imageWriter = ImageIO.getImageWritersByFormatName(type).next();
            ImageWriteParam param = imageWriter.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            imageWriter.setOutput(ImageIO.createImageOutputStream(output));
            imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), param);
        } catch (IOException e) {
            throw e;
        } finally {
            imageWriter.dispose();
        }
    }

    public static void processImage(String imagePath, String savePath, ImageType imageType, Float scale, Float quality) {
        processImage(imagePath, savePath, imageType,null,null, scale, quality);
    }

    public static void processImage(String imagePath, String savePath, ImageType imageType,Integer width,Integer height, Float quality) {
        processImage(imagePath, savePath, imageType,width,height, null, quality);
    }

    public static void processImage(String imagePath, String savePath, ImageType imageType) {
        processImage(imagePath, savePath, imageType,null,null, null, null);
    }

    public static void processImage(String imagePath, String savePath) {
        processImage(imagePath, savePath, null,null,null, null, null);
    }

    public static void processImageQuality(String imagePath, String savePath, ImageType imageType , Float quality) {
        processImage(imagePath, savePath, imageType,null,null, null, quality);
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
            output.reset();
            writeOutput(saveImage, toImageType.getName(), quality.floatValue(), output);
            size = output.size();
            log.info("压缩后大小:{}。压缩质量：{}", size, quality);
            quality = quality.add(new BigDecimal("-0.1"));
        } while (size > maxLength && quality.floatValue() > 0);
        if (size > maxLength) {
            log.warn("压缩指定大小失败！");
        }
        FileUtils.writeFile(new File(toCompressImagePath + "." + toImageType.getName()), output.toByteArray());
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

        writeNoChangeQuality(drawImageDoneBufferedImage, toCompressImagePath, toImageType);
    }

    /**
     * 文件转成 BufferedImage
     *
     * @param imageFile 图片文件 不存在返回为空
     * @return
     */
    public static BufferedImage getBufferedImage(File imageFile) throws IOException {
        if (!imageFile.exists()) {
            throw new FileNotFoundException("PathFile --> " + imageFile + "Cannot Exists");
        }
        return ImageIO.read(imageFile);
    }

    public static BufferedImage getBufferedImage(String imagePath) throws IOException {
        if (StrUtil.isBlank(imagePath)) {
            throw new IllegalArgumentException("ImagePath Cannot Empty");
        }
        return getBufferedImage(new File(imagePath));
    }

    /**
     * 获取图片的类型读取先前8个bit位 也就是64位数据
     * 计算机最后的存储单元的是bit，而实际底层是用0 1这样的数据来存储的
     * 1bit是8位 二进制为 1111 1111
     * 又因为十六进制数最大的数是F -> 1111
     * 所以1个bit位是可以存储两个16进制数的，我们根据文件头来判断当前类型是什么类型的
     * 参考：https://www.ngui.cc/el/2638435.html?action=onClick
     * 为什么采取读文件的形式，是因为如果只是单纯改了后缀图片底层的颜色还是没变的。所以后缀可能不准
     *
     * @param imageFile
     * @return
     */
    public static ImageType getImageType(File imageFile) {
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("PathFile --> " + imageFile + "Cannot Exists");
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

    /**
     * 根据图片路径判断图片类型
     *
     * @param imagePath
     * @return
     */
    public static ImageType getImageType(String imagePath) {
        if (StrUtil.isBlank(imagePath)) {
            throw new IllegalArgumentException("ImagePath Cannot Empty");
        }
        return getImageType(new File(imagePath));
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
