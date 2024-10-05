package com.askan.coinglobalbot;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.askan.coinglobalbot.data.CapReqInstance;

public class MainActivity extends AppCompatActivity {

    private EditText appPackageNameEditText;
    private EditText targetTextEditText;
    private Button startBotButton, goToScreenCaptureControllButton;
    // Declare this globally
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1000;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference UI elements
        appPackageNameEditText = findViewById(R.id.appPackageName);
        targetTextEditText = findViewById(R.id.targetText);
        startBotButton = findViewById(R.id.startBotButton);
        goToScreenCaptureControllButton = findViewById(R.id.goToScreenCaptureControllButton);





        // Set click listener for the Start button
        startBotButton.setOnClickListener(view -> {
            String appPackageName = appPackageNameEditText.getText().toString();
            String targetText = targetTextEditText.getText().toString();

            // Start the bot service with the package name and target text
            startBotService(appPackageName, targetText);
        });


        //set click listener for the screen capture control button
        goToScreenCaptureControllButton.setOnClickListener(view -> {
            // Start the screen capture control activity
            Intent intent = new Intent(this, ScreenRecordActivity.class);
            startActivity(intent);
        });

        // Initialize the MediaProjectionManager
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        // Request screen capture permission
        Intent screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
       // startActivityForResult(screenCaptureIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }


    // Handle the result when user grants permission
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == RESULT_OK) {
            if(resultCode == -1 && data != null) {
                new MethodsImp(this).startScreenCapture(resultCode, data);
                CapReqInstance capReqInstance =  CapReqInstance.getInstance();
                capReqInstance.setRequestCode(requestCode);
                capReqInstance.setResultCode(resultCode);
                capReqInstance.setData(data);
                String instance = capReqInstance.toString();
                System.out.println("Capture Instance from main activity:  " + instance);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }






    // Function to start bot service
    private void startBotService(String appPackageName, String targetText) {
        Intent intent = new Intent(this, BotService.class);
        intent.putExtra("appPackageName", appPackageName);
        intent.putExtra("targetText", targetText);
        startService(intent);
    }







}
