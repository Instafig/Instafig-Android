package com.apptao.i.instafig;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.Date;

/**
 * Created by miss_jie on 2016/3/9.
 */
public class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    /**
     * How long to wait before checking onStart()/onStop() count to determine if the app has been
     * backgrounded.
     */
    public static final long BACKGROUND_CHECK_DELAY_MS = 500;

    private static ActivityLifecycleCallbacks sInstance;

    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private boolean mIsForeground = false;
    private int mCount = 1;

    public static void init(final Application application) {
        if (sInstance == null) {
            sInstance = new ActivityLifecycleCallbacks();
            application.registerActivityLifecycleCallbacks(sInstance);
        }
    }

    public static ActivityLifecycleCallbacks getInstance() {
        return sInstance;
    }

    public boolean isForeground() {
        return mIsForeground;
    }

    public boolean isBackground() {
        return !mIsForeground;
    }

    @Override
    public void onActivityStarted(final Activity activity) {
        mCount++;
        mMainThreadHandler.removeCallbacksAndMessages(null);
        //if (!mIsForeground) {
        //mIsForeground = true;
        //}

        long currentDate = new Date().getTime();
        long lastLoadDate =
                (long) SPUtils.get(activity, "lastLoadDate", currentDate);
        if ((currentDate - lastLoadDate) > 1000 * 60 * 60 * 24) {
            Instafig.instafig = null;
            Instafig.getInstance();
        }
    }

    public void onActivityStopped(final Activity activity) {
        mCount--;
        mMainThreadHandler.removeCallbacksAndMessages(null);
        mMainThreadHandler.postDelayed(new Runnable() {
            public void run() {
                if (mCount == 0) {
                    mIsForeground = false;
                }
            }
        }, BACKGROUND_CHECK_DELAY_MS);
    }

    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
    }

    public void onActivityResumed(final Activity activity) {
    }

    public void onActivityPaused(final Activity activity) {

    }

    public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(final Activity activity) {
    }
}
