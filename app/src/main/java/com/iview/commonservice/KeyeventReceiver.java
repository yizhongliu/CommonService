package com.iview.commonservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.iview.motor.MotorControl;

public class KeyeventReceiver extends BroadcastReceiver {

    private static final String TAG = "KeyeventReceiver";

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private static final int H_STEPS = 300;
    private static final int H_DELAY = 70;
    private static final int V_STEPS = 300;
    private static final int V_DELAY = 70;

    int projectionMode = 0;

    public KeyeventReceiver() {
        initHandleThread();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int keycore = (int)intent.getExtras().get("keyCode");
        int keyaction = (int)intent.getExtras().get("keyAction");
        Log.e(TAG, "keycode:" + keycore);

        mHandler.removeMessages(keycore);
        Message moveMessage = new Message();
        moveMessage.what = keycore;
        Bundle bundle = new Bundle();
        bundle.putInt("keyAction" , keyaction);
        moveMessage.setData(bundle);
        mHandler.sendMessage(moveMessage);

    }

    private void initHandleThread() {
        mHandlerThread = new HandlerThread("PathControlHandler");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.e(TAG, "handle keycode:" + msg.what);
                switch (msg.what) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        handleDpadLeft(msg);
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        handleDpadRight(msg);
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        handleDpadUp(msg);
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        handleDpadDown(msg);
                        break;
                    case KeyEvent.KEYCODE_UNKNOWN:
                        handleUnknown(msg);
                        break;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        handleVolumeUp(msg);
                        break;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        handleVolumeDown(msg);
                        break;
                    case KeyEvent.KEYCODE_POWER:
                        handlePower(msg);
                        break;
                }
            }
        };

    }

    private synchronized void handleDpadLeft(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            MotorControl.controlMotor(MotorControl.HMotor, H_STEPS, MotorControl.HMotorLeftDirection, H_DELAY, true);
        }
    }

    private synchronized void handleDpadRight(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            MotorControl.controlMotor(MotorControl.HMotor, H_STEPS, MotorControl.HMotorRightDirection, H_DELAY, true);
        }
    }

    private synchronized void handleUnknown(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            projectionMode = (++projectionMode) % 4;
            MotorControl.setProjectionMode(projectionMode);
        }
    }

    private synchronized void handleDpadUp(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            MotorControl.controlMotor(MotorControl.VMotor, V_STEPS, MotorControl.VMotorUpDirection, V_DELAY, true);
        }
    }

    private synchronized void handleDpadDown(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            MotorControl.controlMotor(MotorControl.VMotor, V_STEPS, MotorControl.HMotorRightDirection, V_DELAY, true);
        }
    }

    private synchronized void handleVolumeUp(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            MotorControl.controlFocusMotor(10, 1);
        }
    }

    private synchronized void handleVolumeDown(Message msg) {
        int keyaction =  msg.getData().getInt("keyAction");
        if (keyaction == KeyEvent.ACTION_DOWN) {
            MotorControl.controlFocusMotor(10, 0);
        }
    }

    private synchronized void handlePower(Message msg) {
        int keyaction = msg.getData().getInt("keyAction");
        if (keyaction == 0) {
            MotorControl.setProjectorPower(MotorControl.POWER_OFF);
        } else if (keyaction == 1) {
            MotorControl.setProjectorPower(MotorControl.POWER_ON);
        }
    }
}
