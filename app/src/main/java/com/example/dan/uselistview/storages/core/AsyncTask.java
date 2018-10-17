package com.example.dan.uselistview.storages.core;

import android.os.Handler;
import android.os.Looper;

public abstract class AsyncTask<Params, Progress, Result>  {

    private Result publicResult;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public abstract Result doInBackground(Params... params);

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {
    }

    protected final void publishProgress(final Progress... values) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onProgressUpdate(values);
            }
        });
    }

    public final AsyncTask<Params, Progress, Result> execute(final Params... params) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onPreExecute(); // has to be executed before doInBackground && on UI thread

                Thread mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        publicResult = doInBackground(params);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                onPostExecute(publicResult);
                            }
                        });
                    }
                });
                mThread.start();
            }
        });
        return this;
    }
}
