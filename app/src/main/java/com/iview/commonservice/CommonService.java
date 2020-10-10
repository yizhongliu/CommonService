package com.iview.commonservice;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import com.iview.commonclient.ICommonService;
import com.iview.motor.MotorControl;

public class CommonService extends Service {
    private static final String TAG = "CommonService";

    private boolean bHMotorRunning = false;
    private boolean bVMotorRunning = false;

    private CommonClientCallback commonClientCallback;
    private KeyeventReceiver keyeventReceiver;

    public CommonService() {
        commonClientCallback = CommonClientCallback.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");

        keyeventReceiver = new KeyeventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.iview.commonservice.KeyEvent");
        registerReceiver(keyeventReceiver, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                MotorControl.controlFocusMotor(1000, 0);
            }
        }).start();

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iCommonAidl;
    }

    private ICommonService.Stub iCommonAidl = new ICommonService.Stub() {

        @Override
        public int controlHorizontalMotor(final int steps, final int dir, final int delay, final boolean bCheckLimitSwitch) {
            Log.d(TAG, "controlHorizontalMotor");

            if (bHMotorRunning) {
                Log.e(TAG, "Horizontal motor is running");
                return -1;
            }

            bHMotorRunning = true;

            Log.e(TAG, "before handleLeftButton step:" + steps + ", delay:" + delay);
            MotorControl.controlMotor(MotorControl.HMotor, steps, dir, delay, bCheckLimitSwitch);
            Log.e(TAG, "after handleLeftButton step:" + steps + ", delay:" + delay + ",runningtime:" + steps * delay * 2);

            bHMotorRunning = false;

        //    commonClientCallback.onMotorStop(MotorControl.HMotor);

            return 0;
        }

        @Override
        public int controlVerticalMotor(final int steps,final int dir, final int delay, final boolean bCheckLimitSwitch) {
            Log.d(TAG, "controlVerticalMotor");

            if (bVMotorRunning) {
                Log.e(TAG, "Vertical motor is running");
                return -1;
            }

            bVMotorRunning = true;

            Log.e(TAG, "before handleLeftButton step:" + steps + ", delay:" + delay);
            MotorControl.controlMotor(MotorControl.VMotor, steps, dir, delay, bCheckLimitSwitch);
            Log.e(TAG, "after handleLeftButton step:" + steps + ", delay:" + delay + ",runningtime:" + steps * delay * 2);

            bVMotorRunning = false;

            //commonClientCallback.onMotorStop(MotorControl.VMotor);

            return 0;
        }

        @Override
        public int controlHorizontalMotorAsync(final int steps, final int dir, final int delay, final boolean bCheckLimitSwitch) {
            Log.d(TAG, "controlHorizontalMotor");

            if (bHMotorRunning) {
                Log.e(TAG, "Horizontal motor is running");
                return -1;
            }

            bHMotorRunning = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "before handleLeftButton step:" + steps + ", delay:" + delay);
                    MotorControl.controlMotor(MotorControl.HMotor, steps, dir, delay, bCheckLimitSwitch);
                    Log.e(TAG, "after handleLeftButton step:" + steps + ", delay:" + delay + ",runningtime:" + steps * delay * 2);

                    bHMotorRunning = false;

                    commonClientCallback.onMotorStop(MotorControl.HMotor);
                }
            }).start();
            return 0;
        }

        @Override
        public int controlVerticalMotorAsync(final int steps,final int dir, final int delay, final boolean bCheckLimitSwitch) {
            Log.d(TAG, "controlVerticalMotor");

            if (bVMotorRunning) {
                Log.e(TAG, "Vertical motor is running");
                return -1;
            }

            bVMotorRunning = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "before handleLeftButton step:" + steps + ", delay:" + delay);
                    MotorControl.controlMotor(MotorControl.VMotor, steps, dir, delay, bCheckLimitSwitch);
                    Log.e(TAG, "after handleLeftButton step:" + steps + ", delay:" + delay + ",runningtime:" + steps * delay * 2);

                    bVMotorRunning = false;

                    commonClientCallback.onMotorStop(MotorControl.VMotor);
                }
            }).start();
            return 0;
        }

        @Override
        public int stopMotorRunning(int motorId) {
            return MotorControl.stopMotorRunning(motorId);
        }


        @Override
        public void addClient(IBinder client) throws RemoteException {
            commonClientCallback.setClient(client);
        }

        @Override
        public void removeClient(IBinder client) throws RemoteException {
            commonClientCallback.releaseClient(client);
        }

        @Override
        public void switchProjector(int enable) throws RemoteException {
            MotorControl.swtichProjector(enable);
        }

        @Override
        public void setKeystone(int angle) throws RemoteException {
            MotorControl.setKeyStone(angle);
        }

        @Override
        public void controlFocusMotor(int steps, int dir) throws RemoteException {
            MotorControl.controlFocusMotor(steps, dir);
        }

        @Override
        public int getMotorSteps(int motorId) throws RemoteException {
            return MotorControl.getMotorSteps(motorId);
        }


        @Override
        public int getEncoder(int encoderId) {
            return MotorControl.getEncoderCount(encoderId);
        }

        @Override
        public void setEncoder(int encoderId, int value) {
            MotorControl.setEncoderCount(encoderId, value);
        }

        @Override
        public int setMotorSpeed(int motorId, int delay) {
            return MotorControl.setMotorSpeed(motorId, delay);
        }

        @Override
        public int setMotorDirection(int motorId, int direction) {
            return MotorControl.setMotorDirection(motorId, direction);
        }

        @Override
        public int startHorizontalMotorRunning(final boolean bCheckLimitSwitch) {
            Log.d(TAG, "startHorizontalMotorRunning");

            if (bHMotorRunning) {
                Log.e(TAG, "Horizontal motor is running");
                return -1;
            }

            bHMotorRunning = true;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    MotorControl.startMotorRunning(MotorControl.HMotor, bCheckLimitSwitch);

                    bHMotorRunning = false;

                    commonClientCallback.onMotorStop(MotorControl.HMotor);
                }
            }).start();
            return 0;
        }

        @Override
        public int startVerticalMotorRunning(final boolean bCheckLimitSwitch) {
            Log.d(TAG, "controlVerticalMotor");

            if (bVMotorRunning) {
                Log.e(TAG, "Vertical motor is running");
                return -1;
            }

            bVMotorRunning = true;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    MotorControl.startMotorRunning(MotorControl.VMotor, bCheckLimitSwitch);

                    bVMotorRunning = false;

                    commonClientCallback.onMotorStop(MotorControl.VMotor);
                }
            }).start();
            return 0;
        }

        @Override
        public void setProjectionMode(int mode) {
            if (mode >=0 && mode < 4) {
                MotorControl.setProjectionMode(mode);
            }
        }

        @Override
        public void setProjectorOnOff(int enable) {
            if (enable == MotorControl.POWER_OFF || enable == MotorControl.POWER_ON) {
                MotorControl.setProjectorPower(enable);
            }

        }

        @Override
        public void setMotorSteps(int motorId, int steps) {
            MotorControl.setMotorSteps(motorId, steps);
        }

        @Override
        public int getPiState(int motorId, int direction) {
            return MotorControl.getPiState(motorId, direction);
        }

    };
}
