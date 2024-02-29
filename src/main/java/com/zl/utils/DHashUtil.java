package com.zl.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 差异哈希算法
 */
public class DHashUtil {


    /**
     * 计算dHash方法
     *
     * @param file 文件
     * @return hash
     */
    private static String getDHash(File file) {
        //读取文件
        BufferedImage srcImage;
        try {
            srcImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //文件转成9*8像素，为算法比较通用的长宽
        BufferedImage buffImg = new BufferedImage(9, 8, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(srcImage.getScaledInstance(9, 8, Image.SCALE_SMOOTH), 0, 0, null);

        int width = buffImg.getWidth();
        int height = buffImg.getHeight();
        int[][] grayPix = new int[width][height];
        StringBuffer figure = new StringBuffer();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //图片灰度化
                int rgb = buffImg.getRGB(x, y);
                int r = rgb >> 16 & 0xff;
                int g = rgb >> 8 & 0xff;
                int b = rgb & 0xff;
                int gray = (r * 30 + g * 59 + b * 11) / 100;
                grayPix[x][y] = gray;

                //开始计算dHash 总共有9*8像素 每行相对有8个差异值 总共有 8*8=64 个
                if (x != 0) {
                    long bit = grayPix[x - 1][y] > grayPix[x][y] ? 1 : 0;
                    figure.append(bit);
                }
            }
        }

        return figure.toString();
    }

    /**
     * 计算海明距离
     * <p>
     * 原本用于编码的检错和纠错的一个算法
     * 现在拿来计算相似度，如果差异值小于一定阈值则相似，一般经验值小于5为同一张图片
     *
     * @param str1
     * @param str2
     * @return 距离
     */
    private static long getHammingDistance(String str1, String str2) {
        int distance;
        if (str1 == null || str2 == null || str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    //DHashUtil 参数值为待处理文件夹
    public static int calrLength() throws IOException {
        File partImage = new File("E:\\partImage.png");
        File backImage = new File("E:\\backgroungImage.jpg");

        BufferedImage read = ImageIO.read(partImage);

        int offX = 0;
        int offY = 0;

        int endX = 0;
        int endY = 0;

        boolean isGetPoint = false;
        for (int i = 0; i < read.getWidth(); i++) {
            for (int j = 0; j < read.getHeight(); j++) {
                if (!isGetPoint && read.getRGB(i, j) != 0) {
                    offX = i + 2;
                    offY = j + 2;
                    isGetPoint = true;
                }
            }
        }
        for (int i = 0; i < read.getHeight(); i++) {
            if (read.getRGB(offX, i) != 0) {
                endY = i - 2;
            }
        }

        for (int i = 0; i < read.getWidth(); i++) {
            if (read.getRGB(i, offY) != 0) {
                endX = i - 2;
            }
        }

        BufferedImage subimage = read.getSubimage(offX, offY, endX - offX, endY - offY);
        File partOutput = new File("E:/tempPart.png");
        ImageIO.write(subimage, "png", partOutput);


        BufferedImage readBg = ImageIO.read(backImage);
        int theX = 0;
        long minHash = 9999999999L;
        for (int i = 0; i < readBg.getWidth() - endX + offX - 1; i++) {
            BufferedImage subBgImage = readBg.getSubimage(i, offY, endX - offX, endY - offY);
            File bgOutput = new File("E:/tempBgPart.jpg");
            ImageIO.write(subBgImage, "jpg", bgOutput);
//            System.out.println("图片1hash值：" + getDHash(partOutput));
//            System.out.println("图片2hash值：" + getDHash(bgOutput));
            long hammingDistance = getHammingDistance(getDHash(partOutput), getDHash(bgOutput));
//            System.out.println("海明距离为：" + hammingDistance);
            if (minHash >= hammingDistance) {
                theX = i;
                minHash = hammingDistance;
            }
        }

        System.out.println(theX);
//        System.out.println(minHash);
        return theX;
    }
}
