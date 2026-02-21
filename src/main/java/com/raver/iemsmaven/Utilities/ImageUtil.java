/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Utilities;

import com.digitalpersona.uareu.Fid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author admin
 */
public class ImageUtil {
    private static Image defaultImage = new Image("/Images/default_user_img.jpg");
    
    public static Image byteArrayToImage(byte[] byteArray) {
        if(byteArray == null){
            System.out.println("ImageUtil: byteArray from database is NULL, replacing with default image");
            return defaultImage;
        }
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        return new Image(inputStream);
    }
    
    /**
     * Converts an image file to a byte array
     * @param file the image file to convert
     * @return byte array of the image data
     * @throws IOException if there's an error reading the file
     */
    public static byte[] imageFileToByteArray(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            
            return byteArrayOutputStream.toByteArray();
        }
    }
    
    /**
     * Alternative method using ImageIO for image processing
     * @param file the image file to convert
     * @return byte array of the image data
     * @throws IOException if there's an error reading the file
     */
    public static byte[] imageFileToByteArrayWithImageIO(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        if (bufferedImage == null) {
            throw new IOException("Unsupported image format or corrupted file");
        }
        
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // Determine the file format
            String formatName = getImageFormat(file.getName());
            ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
    
    /**
     * Helper method to determine image format from file extension
     * @param fileName the name of the file
     * @return the image format (jpg, png, etc.)
     */
    private static String getImageFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "jpg";
            case "png":
                return "png";
            case "gif":
                return "gif";
            case "bmp":
                return "bmp";
            default:
                return "jpg"; // default to jpg
        }
    }
}