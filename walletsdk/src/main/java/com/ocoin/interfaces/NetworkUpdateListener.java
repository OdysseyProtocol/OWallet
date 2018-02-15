package com.ocoin.interfaces;


import okhttp3.Response;

public interface NetworkUpdateListener {

    public void onUpdate(Response s);
}
