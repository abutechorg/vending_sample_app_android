package com.mastercard.labs.unattended.vending.demo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com)
 */
public class MainThreadImpl implements MainThread {
    private Handler mainHandler;

    public MainThreadImpl(Looper mainLooper) {
        mainHandler = new Handler(mainLooper);
    }

    @Override
    public void execute(Runnable runnable) {
        mainHandler.post(runnable);
    }

    @Override
    public Message obtainMessage(int messageId) {
        return Message.obtain(mainHandler, messageId);
    }
}
