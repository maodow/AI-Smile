package com.example.ai_smile.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Description:
 * Copyright  : Copyright (c) 2018
 * Company    : mixiong
 * Author     : zhanglei
 * Date       : 2018/5/11 15:22
 */

public class MyBus extends Bus {

    //通过单例模式返回唯一的bus对象,而且重写父类的post方法,通过handler实现任意线程可以调用
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static Bus bus = new MyBus();

    private MyBus() {
    }

    public static Bus getInstance() {
        return bus;
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyBus.super.post(event);
                }
            });
        }
    }
}
