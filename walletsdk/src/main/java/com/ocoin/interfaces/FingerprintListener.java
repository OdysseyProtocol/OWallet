package com.ocoin.interfaces;

import android.hardware.fingerprint.FingerprintManager;

public interface FingerprintListener {
    public void authenticationFailed(String error);

    public void authenticationSucceeded(FingerprintManager.AuthenticationResult result);
}