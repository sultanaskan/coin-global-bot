package com.askan.coinglobalbot.data;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;

public class CapReqInstance {
    int requestCode;
    int resultCode;
    Intent data;
    private static CapReqInstance instance;
    private CapReqInstance(){}


    public int getResultCode() {
        return resultCode;
    }

    public static CapReqInstance getInstance(){
        if(instance == null){
            instance = new CapReqInstance();
        }
        return instance;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public String toString() {
        return "ScreenCapture{" +
                "requestCode=" + requestCode +
                ", resultCode=" + resultCode +
                ", data=" + data +
                '}';
    }




}
