package com.nivalsoul.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: ImageUtil </p>
 * <p>Description: 使用JDK原生态类生成图片缩略图和裁剪图片 </p>
 */
public class ImageUtil {
	
	private static Logger log = LoggerFactory.getLogger(ImageUtil.class);
    
    private static String DEFAULT_PREVFIX = "thumb_";
    private static Boolean DEFAULT_FORCE = false;
    
    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 根据图片路径生成缩略图 </p>
     * @param imgFile      原图片
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param prevfix      生成缩略图的前缀
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     * @param thumbFile    缩略图， 为null则将图片保存在原目录并加上前缀
     */
    public static void thumbnailImage(File imgFile, int w, int h, String prevfix, boolean force, File thumbFile){
        if(imgFile.exists()){
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames());
                String suffix = null;
                // 获取图片后缀
                if(imgFile.getName().indexOf(".") > -1) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return ;
                }
                log.debug("target image's size, width:{}, height:{}.",w,h);
                Image img = ImageIO.read(imgFile);
                if(!force){
                    // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if((width*1.0)/w < (height*1.0)/h){
                        if(width > w){
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w/(width*1.0)));
                            log.debug("change image's height, width:{}, height:{}.",w,h);
                        }
                    } else {
                        if(height > h){
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h/(height*1.0)));
                            log.debug("change image's width, width:{}, height:{}.",w,h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                String p = imgFile.getPath();
                // 默认将图片保存在原目录并加上前缀
                if(thumbFile == null)
                	thumbFile = new File(p.substring(0,p.lastIndexOf(File.separator)) 
                    		+ File.separator + prevfix +imgFile.getName());
                ImageIO.write(bi, suffix, thumbFile);
            } catch (IOException e) {
               log.error("generate thumbnail image failed.",e);
            }
        }else{
            log.warn("the image is not exist.");
        }
    }
    
    public static void thumbnailImage(String imagePath, int w, int h, 
    		String prevfix, boolean force, String thumbFilePath){
        File imgFile = new File(imagePath);
        File thumbFile = null;
        if(thumbFilePath != null)
        	thumbFile = new File(thumbFilePath);
        thumbnailImage(imgFile, w, h, prevfix, force, thumbFile);
    }
    
    public static void thumbnailImage(String imagePath, int w, int h, boolean force, String thumbFilePath){
        thumbnailImage(imagePath, w, h, DEFAULT_PREVFIX, force, thumbFilePath);
    }
    
    public static void thumbnailImage(String imagePath, int w, int h, String thumbFilePath){
        thumbnailImage(imagePath, w, h, DEFAULT_FORCE, thumbFilePath);
    }
    
    public static void main(String[] args) {
		ImageUtil.thumbnailImage("imgs/Tulips.jpg", 100, 150, null);
    }

}