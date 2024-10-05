package com.askan.coinglobalbot;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.askan.coinglobalbot.data.CapReqInstance;

public class BotService extends AccessibilityService {
    private String appPackageName = "com.coinglobal.bdt";  // target app package name.bdt";  // target app package name
    private String targetText = "Naeem Ikbal";  // Example: order price to match
    private boolean itemFound = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servise created", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "Service connected", Toast.LENGTH_SHORT).show();
        // Initial setup can go here
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // appPackageName = intent.getStringExtra("appPackageName");
      //  targetText = intent.getStringExtra("targetText");
        if(intent != null){
            openTargetApp();
        }
        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Check for current window event to find and interact with UI elements
        if (event.getSource() != null) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                searchForText(source);
            }
        }
    }

    @Override
    public void onInterrupt() {
        // Handle service interruption
    }

    // Method to open the target app
    private void openTargetApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appPackageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
            Toast.makeText(this, "Target App Opened", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Package Not Found", Toast.LENGTH_SHORT).show();
        }
    }


    // Recursively search for the target text (order price) in scrollable items
    private void searchForText(AccessibilityNodeInfo nodeInfo) {
        String text=  getTextFromListItems(nodeInfo);
        System.out.println("CAPTURE INFO for from diffrent class: "+ text);
    }





    //get text from list items in scrollable view
    int i = 0;

    public String getTextFromListItems(AccessibilityNodeInfo nodeInfo) {
        StringBuilder text = new StringBuilder();

        if (nodeInfo == null) {
            System.out.println("Node not found");
            return null;
        }

        // Increment the counter for traversed nodes
        i++;
        if(nodeInfo.isClickable()) {
            // Capture the current node's details (node class, id, text)
            text.append(" | Node Count: ").append(nodeInfo.getChildCount())
                    .append(" | Depth Level: ").append(i)
                    .append(" | Node Info: ").append(nodeInfo.toString());
        }

        // If the node has text, append it to the result
        CharSequence nodeText = nodeInfo.getText();
        if (nodeText != null && nodeText.length() > 0) {
            text.append(" | Node Text: ").append(nodeText);
        }

        // Recursively traverse each child node
        for (int j = 0; j < nodeInfo.getChildCount(); j++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(j);
            if (childNode != null) {
                text.append("\n")
                        .append("  ".repeat(i)) // Add indentation for visual representation of depth
                        .append(getTextFromListItems(childNode)); // Recursive call to traverse deeper
            }
        }

        // Return the accumulated text from all nodes
        return text.toString();
    }





    // Perform action on sibling button of the target text node
    private void performActionOnSiblingButton(AccessibilityNodeInfo targetNode) {
        AccessibilityNodeInfo parent = targetNode.getParent();
        if (parent != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                AccessibilityNodeInfo sibling = parent.getChild(i);
                if (sibling != null && sibling.isClickable()) {
                    sibling.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Toast.makeText(this, "Clicked Button", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // find scrollable node in the tree
    // Method to find the first scrollable node in the node tree
    private AccessibilityNodeInfo findScrollableNode(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return null;  // Base case: node is null
        }
        // Check if the current node is scrollable
        if (nodeInfo.isScrollable()) {
            return nodeInfo;  // Return the node if it is scrollable
        }
        // Recursively traverse the child nodes to find a scrollable node
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            AccessibilityNodeInfo scrollableNode = findScrollableNode(child);
            if (scrollableNode != null) {
                return scrollableNode;  // Return the first scrollable node found
            }
        }
        return null;  // Return null if no scrollable node is found
    }





    // Fill the input field and click confirm button
    private void fillInputAndConfirm() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                if (rootNode == null) return;

                // Find input field and fill the value
                AccessibilityNodeInfo inputField = findNodeByClassName(rootNode, "android.widget.EditText");
                if (inputField != null) {
                    inputField.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    inputField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, createTextInput("Your Text Here"));
                    Toast.makeText(BotService.this, "Input filled", Toast.LENGTH_SHORT).show();
                }

                // Find confirm button and click
                AccessibilityNodeInfo confirmButton = findNodeByText(rootNode, "Confirm");
                if (confirmButton != null) {
                    confirmButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Toast.makeText(BotService.this, "Clicked Confirm", Toast.LENGTH_SHORT).show();
                }

                // Close any dialog and go back
                performGlobalAction(GLOBAL_ACTION_BACK);
                Toast.makeText(BotService.this, "Dialog Closed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    // Helper method to find node by class name
    private AccessibilityNodeInfo findNodeByClassName(AccessibilityNodeInfo root, String className) {
        if (root == null) return null;

        if (className.equals(root.getClassName())) {
            return root;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByClassName(root.getChild(i), className);
            if (result != null) return result;
        }

        return null;
    }

    // Helper method to find node by text
    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null) return null;
        if (root.getText() != null && root.getText().toString().equals(text)) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByText(root.getChild(i), text);
            if (result != null) return result;
        }
        return null;
    }



    // Helper method to create text input
    private Bundle createTextInput(String input) {
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, input);
        return args;
    }
}
