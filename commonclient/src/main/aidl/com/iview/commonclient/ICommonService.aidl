// ICommonAidl.aidl
package com.iview.commonclient;

// Declare any non-default types here with import statements

interface ICommonService {
    int controlHorizontalMotor(int steps, int dir, int delay, boolean bCheckLimitSwitch);

    int controlVerticalMotor(int steps, int dir, int delay, boolean bCheckLimitSwitch);

    int stopMotorRunning(int motorId);

    void addClient(IBinder client);

    void removeClient(IBinder client);
}
