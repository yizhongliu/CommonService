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

    public static final int HEncoder = 1;
    public static final int VEncoder = 0;

    public static final float HEncoderPerPlus = 0.37f;
    public static final float VEncoderPerPlus = 0.46f;

    public final static Float H_DEGREE_P_STEP = 0.001232f;
    public final static Float V_DEGREE_P_STEP = 0.001330f;

    public static final int FOCUS_NEAR = 1;
    public static final int FOCUS_FAR = 0;

    public static final int PROJECTOR_POWER_ON = 1;
    public static final int PROJECTOR_POWER_OFF = 0;

    public static final int PI_STATE_TRIGER = 1;
    public static final int PI_STATE_UNTRIGER = 0;

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

    public int controlMotor(int motorId, float degree, int dir, int delay, boolean bCheckLimitSwitch) {
        int ret = -1;
        int steps = 0;

        try {
            if (commonService != null) {
                if (motorId == HMotor) {
                    steps = (int) (degree / H_DEGREE_P_STEP);
                    ret = commonService.controlHorizontalMotor(steps, dir, delay, bCheckLimitSwitch);
                } else if (motorId == VMotor) {
                    steps = (int) (degree / V_DEGREE_P_STEP);
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

    public int controlMotorAsync(int motorId, int steps, int dir, int delay, boolean bCheckLimitSwitch) {
        int ret = -1;

        try {
            if (commonService != null) {
                if (motorId == HMotor) {
                    ret = commonService.controlHorizontalMotorAsync(steps, dir, delay, bCheckLimitSwitch);
                } else if (motorId == VMotor) {
                    ret = commonService.controlVerticalMotorAsync(steps, dir, delay, bCheckLimitSwitch);
                }
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public int controlMotorAsync(int motorId, float degree, int dir, int delay, boolean bCheckLimitSwitch) {
        int ret = -1;
        int steps = 0;

        try {
            if (commonService != null) {
                if (motorId == HMotor) {
                    steps = (int) (degree / H_DEGREE_P_STEP);
                    ret = commonService.controlHorizontalMotorAsync(steps, dir, delay, bCheckLimitSwitch);
                } else if (motorId == VMotor) {
                    steps = (int) (degree / V_DEGREE_P_STEP);
                    ret = commonService.controlVerticalMotorAsync(steps, dir, delay, bCheckLimitSwitch);
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

    /**
     * 获取编码器的值
     * @param encoderId 编码器id
    {
    @link HEncoder 水平方向编码器
    @link VEncoder 垂直方向编码器
    }
     * @return int 编码器脉冲数
     */
    public int getEncoder(int encoderId) {
        int ret = 0;
        try {
            if (commonService != null) {
                ret = commonService.getEncoder(encoderId);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public float getEncoderDegree(int encoderId) {
        int pluses = getEncoder(encoderId);
        float degree = 0.0f;

        switch(encoderId) {
            case HEncoder:
                degree = pluses * HEncoderPerPlus;
                break;
            case VEncoder:
                degree = pluses * VEncoderPerPlus;
                break;
        }

        return degree;
    }

    /**
     * 设置编码器的值
     * @param encoderId 编码器id
    {
    @link HEncoder 水平方向编码器
    @link VEncoder 垂直方向编码器
    }
     * @param value 编码器脉冲数
     */
    public void setEncoder(int encoderId, int value) {
        try {
            if (commonService != null) {
                commonService.setEncoder(encoderId, value);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void controFocusMotor(int steps, int dir) {
        Log.e(TAG, "controFocusMotor");
        if (dir == FOCUS_NEAR || dir == FOCUS_FAR) {
            try {
                if (commonService != null) {

                    commonService.controlFocusMotor(steps, dir);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "controlFoucsMotor error dir:" + dir);
        }
    }

    public void setKeystone(int angle) {
        if (angle > 40 || angle < -40) {
            Log.e(TAG, "setKeystone angle out of range");
            return;
        }

        try {
            if (commonService != null) {
                commonService.setKeystone(angle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void switchProjector(int enable) {
        if (enable != 0 && enable != 1) {
            Log.e(TAG, "switchProjector error");
            return;
        }

        try {
            if (commonService != null) {
                commonService.switchProjector(enable);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getMotorSteps(int motorId) {
        int steps = 0;
        try {
            if (commonService != null) {
                steps = commonService.getMotorSteps(motorId);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return steps;
    }

    /*
     * 以下几个接口有关联
     * 先设置速度和方向, 再调用startMotorRunning 启动马达运动,如果不调用stopMotorRunning则一直运行
     */
    public void setMotorSpeed(int motorId, int delay) {
        try {
            if (commonService != null) {
                commonService.setMotorSpeed(motorId, delay);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void setMotorDirection(int motorId, int direction) {
        try {
            if (commonService != null) {
                commonService.setMotorDirection(motorId, direction);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int startMotorRunning(int motorId, boolean bCheckLimitSwitch) {
        Log.e(TAG, "startMotorRunning: motorId:" + motorId + ", bCheckLimitSwitch:" + bCheckLimitSwitch);
        int ret = -1;

        try {
            if (commonService != null) {
                if (motorId == HMotor) {
                    Log.e(TAG, "startMotorRunning: startHorizontalMotorRunning");
                    ret = commonService.startHorizontalMotorRunning(bCheckLimitSwitch);
                } else if (motorId == VMotor) {
                    Log.e(TAG, "startMotorRunning: startVerticalMotorRunning");
                    ret = commonService.startVerticalMotorRunning(bCheckLimitSwitch);
                }
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void setProjectorMode(int mode) {
        try {
            if (commonService != null) {
                commonService.setProjectionMode(mode);
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setProjectorOnOff(int enable) {
        try {
            if (commonService != null) {
                commonService.setProjectorOnOff(enable);
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setMotorSteps(int motorId, int motorSteps) {
        try {
            if (commonService != null) {
                commonService.setMotorSteps(motorId, motorSteps);
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getPiState(int motorId, int direction) {
        int ret = PI_STATE_UNTRIGER;
        try {
            if (commonService != null) {
                ret = commonService.getPiState(motorId, direction);
            } else {
                Log.e(TAG, "commonService is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return ret;

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

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
