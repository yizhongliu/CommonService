package com.iview.commonservice;

import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.iview.commonclient.ICommonClient;

import java.util.HashMap;

public class CommonClientCallback implements CommonEventListener{

    private static final String TAG = "CommonClientCallback";

    private static CommonClientCallback mCommonClientCallback;

    private RemoteCallbackList<ICommonClient> mCallbackList;

    private HashMap<IBinder, ICommonClient> mMap;

    private CommonClientCallback() {
        mCallbackList = new RemoteCallbackList<ICommonClient>() {
            @Override
            public void onCallbackDied(ICommonClient client) {
                mMap.remove(client.asBinder());
            }
        };

        mMap = new HashMap<IBinder, ICommonClient>();
    }

    public static CommonClientCallback getInstance() {
        if (mCommonClientCallback == null) {
            synchronized (CommonClientCallback.class) {
                if (mCommonClientCallback == null) {
                    mCommonClientCallback = new CommonClientCallback();
                }
            }
        }

        return mCommonClientCallback;
    }

    public void setClient(IBinder client) {
        if (client != null) {
            ICommonClient commonClient = ICommonClient.Stub.asInterface(client);
            mCallbackList.register(commonClient);
            mMap.put(client, commonClient);
        }
    }

    public void releaseClient(IBinder client) {
        if (client != null) {
            ICommonClient commonClient = mMap.get(client);
            mCallbackList.unregister(commonClient);
            mMap.remove(client);
        }
    }

    @Override
    public void onMotorStop(int motorId) {
        if (mCallbackList == null) {
            return;
        }

        int count = mCallbackList.beginBroadcast();
        while (count > 0) {
            count--;
            try {
                mCallbackList.getBroadcastItem(count).onMotorStop(motorId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        mCallbackList.finishBroadcast();
    }
}
