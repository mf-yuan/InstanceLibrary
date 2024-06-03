package com.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class ImageRotate {


    /**
     * 纠正图片旋转
     *
     * @param srcImgPath
     */
    public static void correctImg(String srcImgPath) {
        FileOutputStream fos = null;
        try {
            // 原始图片
            File srcFile = new File(srcImgPath);
            // 获取偏转角度
            RectifyDirection rotateAngle = getRectifyDirection(srcFile);
            if(rotateAngle == null){
                return;
            }
            System.out.println(rotateAngle.angel+":"+rotateAngle.isMirror);

            // 原始图片缓存
            BufferedImage srcImg = ImageIO.read(srcFile);


            // 原始宽度
            int imgWidth = srcImg.getWidth();
            // 原始高度
            int imgHeight = srcImg.getHeight();

            // 宽高互换
            if (rotateAngle.angel != 180) {
                int temp = imgWidth;
                imgWidth = imgHeight;
                imgHeight = temp;
            }

            // 中心点位置
            double centerWidth = ((double) imgWidth) / 2;
            double centerHeight = ((double) imgHeight) / 2;

            // 图片缓存
            BufferedImage targetImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

            // 旋转对应角度
            Graphics2D g = targetImg.createGraphics();
            // 向左旋转
            g.rotate(Math.toRadians(rotateAngle.angel), centerWidth, centerHeight);
            System.out.println(srcImg.getWidth());
            System.out.println(srcImg.getHeight());

            int x = (imgWidth - srcImg.getWidth()) / 2;
            int y = (imgHeight - srcImg.getHeight()) / 2;
            g.drawImage(srcImg, x, y, null);
            g.dispose();
            if (rotateAngle.isMirror){
                BufferedImage tempImage = new BufferedImage(targetImg.getWidth(), targetImg.getHeight(), BufferedImage.TYPE_INT_RGB);
                g = tempImage.createGraphics();
                // 使用 AffineTransform 进行水平翻转
                AffineTransform transform = new AffineTransform();
                transform.translate(targetImg.getWidth() , 0);
                transform.scale(-1, 1);
                g.setTransform(transform);

                g.drawImage(targetImg,0, 0, null);
                g.dispose();
                targetImg = tempImage;
            }

            // 输出图片
            fos = new FileOutputStream(new File(srcFile.getAbsolutePath() + "." + srcImgPath.substring(srcImgPath.lastIndexOf(".") + 1)));
            ImageIO.write(targetImg, srcImgPath.substring(srcImgPath.lastIndexOf(".") + 1), fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取纠正方向
     * @param file
     * @return
     * @throws Exception
     */
    public static RectifyDirection getRectifyDirection(File file) throws Exception {
        // 1.获取图片的元数据
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        // 2.图片元数据处理那种方向
        int orientation = 0;

        for (Directory directory : metadata.getDirectories()) {
            // 3.只需要TAG_ORIENTATION的数据即可。
            if(directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)){
                for (Tag tag : directory.getTags()) {
                    System.out.println(tag);
                }
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        }
        // 4.根据不同的orientation创建不同的处理方式
        switch (orientation){
            case 2:
                return new RectifyDirection(0,true);
            case 3:
                return new RectifyDirection(180,false);
            case 4:
                return new RectifyDirection(180, true);
            case 5:
                return new RectifyDirection(90,true);
            case 6:
                return new RectifyDirection(90,false);
            case 7:
                return new RectifyDirection(270,true);
            case 8:
                return new RectifyDirection(270,false);
            default:
                return null;
        }
    }

    static class RectifyDirection{
        /**
         * 角度
         */
        public int angel;
        /**
         * 是否镜像
         */
        public boolean isMirror;

        public RectifyDirection(int angel, boolean isMirror) {
            this.angel = angel;
            this.isMirror = isMirror;
        }
    }

    public static void main(String[] args) {
        ImageRotate.correctImg("C:\\Users\\EDY\\Desktop\\微信图片_20230814120400.jpg");
    }
}
