package com.askan.coinglobalbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.nio.ByteBuffer;
public class MethodsImp {
        private Context context;

        // Constructor that takes a context
        public MethodsImp(Context context) {
            this.context = context;
        }


        //start screen capture method
        public void startScreenCapture(int resultCode, Intent data) {
            System.out.println("startScreenCapture method called: " + resultCode + " " + data);
            if(resultCode == Activity.RESULT_OK && data != null){
               try {
                   MediaProjectionManager mpm  = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                   new Thread(() -> {
                       System.out.println("Background thread started");
                       MediaProjection mediaProjection = mpm.getMediaProjection(Activity.RESULT_OK, data);
                       if(mediaProjection != null ){
                           System.out.println("MediaProjection is not null: "+ mediaProjection);
                           DisplayMetrics metrics = new DisplayMetrics();
                           WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                           windowManager.getDefaultDisplay().getMetrics(metrics);
                           // Set up the ImageReader to capture the screenshot
                           int screenWidth = metrics.widthPixels;
                           int screenHeight = metrics.heightPixels;
                           int screenDensity = metrics.densityDpi;

                           // Set up the ImageReader with the screen's dimensions and pixel format
                           ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
                           System.out.println("ImageReader created");
                           // Create the Virtual Display using MediaProjection
                           VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay(
                                   "ScreenCapture",
                                   screenWidth,
                                   screenHeight,
                                   screenDensity,
                                   DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                                   imageReader.getSurface(),
                                   null,
                                   null
                           );

                           System.out.println("Virtual Display created");

                           // Set a listener to capture the image when available
                           imageReader.setOnImageAvailableListener(reader -> {
                               System.out.println("Image available");
                               Image image = reader.acquireLatestImage();
                               if (image != null) {
                                   System.out.println("Image captured");

                                   Bitmap bitmap = imageToBitmap(image);
                                   image.close();

                                   if (bitmap != null) {
                                       System.out.println("Bitmap created from the captured image");
                                   } else {
                                       System.out.println("Bitmap creation failed");
                                   }
                               } else {
                                   System.out.println("Image not captured");
                               }
                           }, null);
                       }else{
                           System.out.println("MediaProjection is null");
                       }
                   }).start();
               }catch (Exception e){
                   System.out.println("startScreenCapture method failed: " + e.getMessage());
               }


            }else{
                System.out.println("startScreenCapture method failed: " + resultCode + " " + data);
            }

        }








        // Method for converting Image to Bitmap
        public Bitmap imageToBitmap(Image image) {
            if (image == null) {
                return null;
            }
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * image.getWidth();

            Bitmap bitmap = Bitmap.createBitmap(
                    image.getWidth() + rowPadding / pixelStride,
                    image.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
            bitmap.copyPixelsFromBuffer(buffer);
            return bitmap;
        }
    }

