package com.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.apache.poi.ss.formula.functions.T;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

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
            AngelMirror rotateAngle = getRotateAngle(srcFile);
            if(rotateAngle == null){
                return;
            }
            System.out.println(rotateAngle.angel+":"+rotateAngle.isMirror);

            // 原始图片缓存
            BufferedImage srcImg = ImageIO.read(srcFile);

            // 宽高互换
            // 原始宽度
            int imgWidth = srcImg.getHeight();
            // 原始高度
            int imgHeight = srcImg.getWidth();

            // if (rotateAngle.angel != 180) {
            //     int temp = imgWidth;
            //     imgWidth = imgHeight;
            //     imgHeight = temp;
            // }

            // 中心点位置
            double centerWidth = ((double) imgWidth) / 2;
            double centerHeight = ((double) imgHeight) / 2;

            // 图片缓存
            BufferedImage targetImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

            // 旋转对应角度
            Graphics2D g = targetImg.createGraphics();
            // 向左旋转
            g.rotate(Math.toRadians(rotateAngle.angel), centerWidth, centerHeight);
            g.drawImage(srcImg, (imgWidth - srcImg.getWidth()) / 2, (imgHeight - srcImg.getHeight()) / 2, null);
            g.rotate(Math.toRadians(-rotateAngle.angel), centerWidth, centerHeight);
            g.dispose();

            if (rotateAngle.isMirror){
                g = targetImg.createGraphics();
                // 使用 AffineTransform 进行水平翻转
                AffineTransform transform = new AffineTransform();
                System.out.println(imgWidth);
                System.out.println(imgHeight);
                System.out.println(centerWidth);
                System.out.println(centerHeight);
                transform.translate(targetImg.getWidth(), 0);
                transform.scale(-1, 1);
                g.drawImage(targetImg, transform, null);
                g.dispose();
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

    public static AngelMirror getRotateAngle(File file) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        int orientation = 0;

        for (Directory directory : metadata.getDirectories()) {
            System.out.println(directory);
            if (directory.getString(ExifIFD0Directory.TAG_ORIENTATION) != null) {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        }
        System.out.println(orientation);
        switch (orientation){
            case 2:
                return new AngelMirror(0,true);
            case 3:
                return new AngelMirror(180,false);
            case 4:
                return new AngelMirror(180, true);
            case 5:
                return new AngelMirror(90,true);
            case 6:
                return new AngelMirror(90,false);
            case 7:
                return new AngelMirror(270,true);
            case 8:
                return new AngelMirror(270,false);
            default:
                return null;
        }
    }

    static class AngelMirror{
        public int angel;
        public boolean isMirror;

        public AngelMirror(int angel, boolean isMirror) {
            this.angel = angel;
            this.isMirror = isMirror;
        }
    }




    public static void main(String[] args) {
        ImageRotate.correctImg("C:\\Users\\EDY\\Desktop\\微信图片_20230814102842.jpg");
    }
}
