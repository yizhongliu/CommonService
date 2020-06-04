// ICommonAidl.aidl
package com.iview.commonclient;

// Declare any non-default types here with import statements

interface ICommonService {
    int controlHorizontalMotor(int steps, int dir, int delay, boolean bCheckLimitSwitch);

    int controlVerticalMotor(int steps, int dir, int delay, boolean bCheckLimitSwitch);

    int controlHorizontalMotorAsync(int steps, int dir, int delay, boolean bCheckLimitSwitch);

    int controlVerticalMotorAsync(int steps, int dir, int delay, boolean bCheckLimitSwitch);

    int stopMotorRunning(int motorId);

    void setEncoder(int encoderId, int value);

    int getEncoder(int encoderId);

    void addClient(IBinder client);

    void removeClient(IBinder client);

    void switchProjector(int enable);

    void setKeystone(int angle);

    void controlFocusMotor(int steps, int dir);

    int getMotorSteps(int motorId);

    //以下几个接口有关联
    //先设置速度和方向, 再调用startMotorRunning 启动马达运动,如果不调用stopMotorRunning则一直运行
    int setMotorSpeed(int motorId, int delay);

    int setMotorDirection(int motorId, int direction);

    int startHorizontalMotorRunning(boolean bCheckLimitSwitch);

    int startVerticalMotorRunning(boolean bCheckLimitSwitch);
    ///////////////////////////////////////////////////////////////////////
}
