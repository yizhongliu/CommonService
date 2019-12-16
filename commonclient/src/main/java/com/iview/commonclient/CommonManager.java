package com.iview.commonclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iview.commonclient.ICommonService;
import com.iview.commonclient.ICommonClient;

import java.util.ArrayList;

public class CommonManager {
    private static String TAG = "CommonManager";

    public static final int HMotor = 1;
    public static final int VMotor = 2;

    public static final int HMotorLeftDirection = 0;
    public static final int HMotorRightDirection = 1;

    public static final int VMotorUpDirection = 0;
    public static final int VMotorDownDirection = 1;

    public static final int PROJECTOR_ON = 1;
    public static final int PROJECTOR_OFF = 0;

    private ICommonService commonService;

    private static CommonManager commonManager;

    private Context context;

    private Client client;

    private ArrayList<OnCommonListener> listeners = new ArrayList<OnCommonListener>();

    public static CommonManager getInstance(Context context) {
        if (commonManager == null) {
            synchronized (CommonManager.class) {
                if (commonManager == null) {
                    commonManager = new CommonManager(context);
                }
            }
        }
        return commonManager;
    }

    private CommonManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void connect() {
        if (commonService == null) {
            Intent it = new Intent();
            it.setAction("android.intent.action.CommonService");
            it.setPackage("com.iview.commonservice");
            context.bindService(it, sc, Context.BIND_AUTO_CREATE);
            Log.e(TAG, "bind service");
        } else {
            for (OnCommonListener l : listeners) {
                l.onServiceConnect();
            }
        }
    }

    public void disconnect() {
        if (commonService != null) {
            try {
                commonService.removeClient(client);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            context.unbindService(sc);
            commonService = null;
        }
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "onServiceConnected");
            commonService = ICommonService.Stub.asInterface(iBinder);

            client = new Client();

            try {
                commonService.addClient(client);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            for (OnCommonListener l : listeners) {
                l.onServiceConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            commonService = null;
        }
    };

    public int controlMotor(int motorId, int steps, int dir, int delay, boolean bCheckLimitSwitch) {
        int ret = -1;

        try {
            if (commonService != null) {
                if (motorId == HMotor) {
                    ret = commonService.controlHorizontalMotor(steps, dir, delay, bCheckLimitSwitch);
                } else if (motorId == VMotor) {
                    ret = commonService.controlVerticalMotor(steps, dir, delay, bCheckLimitSwitch);
                }
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public int stopMotorRunning(int motorId) {
        int ret = -1;
        try {
            if (commonService != null) {
               commonService.stopMotorRunning(motorId);
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private class Client extends ICommonClient.Stub {

        @Override
        public void onMotorStop(int motorId) throws RemoteException {
            for (OnCommonListener l : listeners) {
                l.onMotorStop(motorId);
            }
        }
    }

    public void regisiterOnCommonListener(OnCommonListener listener) {
        listeners.add(listener);
    }

    public void unregisiterOnCommonListener(OnCommonListener listener) {
        listeners.remove(listener);
    }
}
