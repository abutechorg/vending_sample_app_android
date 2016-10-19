package com.mastercard.labs.unattended.vending.demo;

import android.os.Message;

/**
 * @author Muhammad Azeem (muhammad.azeem@mastercard.com)
 */
public interface MainThread {
    void execute(Runnable runnable);

    Message obtainMessage(int messageId);
}
