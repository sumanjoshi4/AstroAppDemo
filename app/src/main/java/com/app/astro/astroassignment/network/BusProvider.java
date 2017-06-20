package com.app.astro.astroassignment.network;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by B0096643 on 6/16/2017.
 */
public class BusProvider {
    private static final Bus BUS = new MainThreadBus(new Bus());

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

    static class MainThreadBus extends Bus {
        private final Bus     mBus;
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        public MainThreadBus(final Bus bus) {
            if (bus == null) {
                throw new NullPointerException("bus must not be null");
            }
            mBus = bus;
        }

        @Override
        public void register(Object obj) {
            mBus.register(obj);
        }

        @Override
        public void unregister(Object obj) {
            mBus.unregister(obj);
        }

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mBus.post(event);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBus.post(event);
                    }
                });
            }
        }
    }
}
