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
 * @version 1.0
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


    private static boolean processImage(String imagePath, OutputStream output, ImageType imageType, Integer width, Integer height, Float scale, Float quality) {
        BufferedImage bufferedImage = null;
        ImageType targetImageType = imageType;
        try {
            bufferedImage = getBufferedImage(imagePath);
        } catch (IOException e) {
            log.error("Not Load " + imagePath + " Image");
            return false;
        }

        if (output == null) {
            log.error("Output Cannot NULL");
            return false;
        }

        if (targetImageType == null) {
            // 尝试获取原图片的图片类型 没有获取到则返回
            targetImageType = getImageType(imagePath);
            if (targetImageType == null) {
                log.error("Unable to obtain the type of saved image");
                return false;
            }
        }

        if (scale != null && scale <= 0) {
            log.error("Scaling value needs to be greater than 0 ");
            return false;
        }

        if (quality != null && quality <= 0) {
            log.error("Quality value needs to be greater than 0 ");
            return false;
        }

        BufferedImage targetImage = getDrawImageDoneBufferedImage(bufferedImage, targetImageType, width, height, scale);

        return write(targetImage, output, targetImageType, quality);
    }

    /**
     * @param imagePath 待处理图片文件路径
     * @param savePath  处理后保存文件的地址 不带后缀
     * @param imageType 保存的文件格式 如果为空则使用 imagePath的文件格式保存
     * @return
     */
    private static boolean processImage(String imagePath, String savePath, ImageType imageType, Integer width, Integer height, Float scale, Float quality) {
        if (StrUtil.isBlank(savePath)) {
            log.error("SavePath Cannot Empty");
            return false;
        }
        try (FileOutputStream output = new FileOutputStream(savePath + "." + imageType.getName())) {
            return processImage(imagePath, output, imageType, width, height, scale, quality);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean processImage(String imagePath, String savePath, ImageType imageType, Float scale, Float quality) {
        return processImage(imagePath, savePath, imageType, null, null, scale, quality);
    }

    public static boolean processImage(String imagePath, String savePath, ImageType imageType, Integer width, Integer height, Float quality) {
        return processImage(imagePath, savePath, imageType, width, height, null, quality);
    }

    public static boolean processImage(String imagePath, String savePath, ImageType imageType) {
        return processImage(imagePath, savePath, imageType, null, null, null, null);
    }

    public static boolean processImage(String imagePath, String savePath) {
        return processImage(imagePath, savePath, null, null, null, null, null);
    }

    public static boolean processImageQuality(String imagePath, String savePath, ImageType imageType, Float quality) {
        return processImage(imagePath, savePath, imageType, null, null, null, quality);
    }

    public static boolean processImageQuality(String imagePath, OutputStream output, ImageType imageType, Float quality) {
        return processImage(imagePath, output, imageType, null, null, null, quality);
    }

    public static boolean processImageScaled(String imagePath, String savePath, ImageType imageType, Float scale) {
        return processImage(imagePath, savePath, imageType, null, null, scale, null);
    }

    public static boolean processImageScaled(String imagePath, OutputStream output, ImageType imageType, Float scale) {
        return processImage(imagePath, output, imageType, null, null, scale, null);
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
        return getDrawImageDoneBufferedImage(targetImage, imageType, null, null, null);
    }

    /**
     * 将图片写入输出流程
     * 如果不改变质量则直接使用 ImageIO.write 写入图片
     * 反之使用 ImageWriter
     *
     * @param image     源图片
     * @param output    输出流
     * @param imageType 保存图片的类型
     * @param quality   图片质量
     * @return
     */
    private static boolean write(BufferedImage image, OutputStream output, ImageType imageType, Float quality) {
        String type = imageType.getName();
        try {
            if (quality == null) {
                return ImageIO.write(image, type, ImageIO.createImageOutputStream(output));
            }
            ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(type).next();
            ImageWriteParam param = imageWriter.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            imageWriter.setOutput(ImageIO.createImageOutputStream(output));
            imageWriter.write(null, new javax.imageio.IIOImage(image, null, null), param);
            imageWriter.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将源文件数据通过FileOutputStream保存到saveImagePath的文件中
     *
     * @param image         源图片
     * @param saveImagePath 保存图片的路径
     * @param imageType     保存图片的类型
     * @param quality       图片质量
     * @return
     */
    private static boolean write(BufferedImage image, String saveImagePath, ImageType imageType, Float quality) {
        try (FileOutputStream output = new FileOutputStream(saveImagePath + "." + imageType.getName())) {
            return write(image, output, imageType, quality);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 压缩方式
     */
    enum CompressType {
        // 质量压缩
        QualityCompress,
        // 缩放压缩
        ScaledCompress
    }

    /**
     * 将图片压缩指指定文件大小 最多尝试10次 最后还未压缩至指定大小则返回false
     * 压缩成功后将文件保存至 toCompressImagePath中
     *
     * @param imagePath           图片文件地址
     * @param toCompressImagePath 保存文件的地址
     * @param imageType           图片格式
     * @param specifySize         指定的大小
     * @param specifyType         指定的单位
     * @param compressType        压缩方式
     * @return
     * @throws IOException
     */
    public static boolean compressImage(String imagePath, String toCompressImagePath, ImageType imageType
            , Long specifySize, FileUtils.CapacityUnit specifyType, CompressType compressType) throws IOException {
        if (compressType == null) {
            log.error("CompressType Cannot NULL");
            return false;
        }
        File file = new File(imagePath);

        BigDecimal targetValue = new BigDecimal("1.0");
        long size = file.length();
        log.info("压缩前大小" + size);

        long maxLength = specifySize * specifyType.getByteSize();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        boolean flag = false;
        do {
            output.reset();
            if (compressType == CompressType.QualityCompress) {
                flag = processImageQuality(imagePath, output, imageType, targetValue.floatValue());
            } else if (compressType == CompressType.ScaledCompress) {
                flag = processImageScaled(imagePath, output, imageType, targetValue.floatValue());
            }
            if (!flag) {
                log.error("压缩失败！");
                return false;
            }
            size = output.size();
            log.info("压缩后大小:{}、{}Value：{}", size, compressType, targetValue);
            targetValue = targetValue.subtract(new BigDecimal("0.1"));
        } while (size > maxLength && targetValue.floatValue() > 0);
        if (size > maxLength) {
            log.error("压缩指定大小失败！");
            return false;
        }
        FileUtils.writeFile(new File(toCompressImagePath + "." + imageType.getName()), output.toByteArray());
        return true;
    }

    public static boolean compressImageQuality(String imagePath, String toCompressImagePath, ImageType imageType
            , Long specifySize, FileUtils.CapacityUnit specifyType) throws IOException {
        return compressImage(imagePath, toCompressImagePath, imageType, specifySize, specifyType, CompressType.QualityCompress);
    }

    public static boolean compressImageScaled(String imagePath, String toCompressImagePath, ImageType imageType
            , Long specifySize, FileUtils.CapacityUnit specifyType) throws IOException {
        return compressImage(imagePath, toCompressImagePath, imageType, specifySize, specifyType, CompressType.ScaledCompress);
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
