package com.iview.commonservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iview.commonclient.ICommonService;
import com.iview.motor.MotorControl;

public class CommonService extends Service {
    private static final String TAG = "CommonService";

    private boolean bHMotorRunning = false;
    private boolean bVMotorRunning = false;

    private CommonClientCallback commonClientCallback;

    public CommonService() {
        commonClientCallback = CommonClientCallback.getInstance();
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
        public int controlVerticalMotor(final int steps,final int dir, final int delay, final boolean bCheckLimitSwitch) {
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
    };
}
