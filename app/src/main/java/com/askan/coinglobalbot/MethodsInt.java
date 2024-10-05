package com.askan.coinglobalbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;

public interface MethodsInt {
    void startScreenCapture(  int resultCode, Intent data);
    public Bitmap imageToBitmap(Image image);
    //void stopScreenCapture();


}
