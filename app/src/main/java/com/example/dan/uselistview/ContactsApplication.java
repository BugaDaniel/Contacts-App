package com.example.dan.uselistview;

import android.app.Application;
import android.util.Log;

import com.example.dan.uselistview.core.CustomAdapter;
import com.example.dan.uselistview.storages.StorageManagerFactory;
import com.example.dan.uselistview.storages.core.StorageManager;

import java.lang.ref.WeakReference;

public class ContactsApplication extends Application {

    public static WeakReference<CustomAdapter> adapterWeakReference;
    static StorageManager storageManager;

    public static String TAG = ContactsApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            storageManager = StorageManagerFactory.create(StorageManagerFactory.StorageManagerType.SHARED_PREF);
            storageManager.initialize(this, new StorageManager.InitializeCallback() {

                @Override
                public void onFinished() {
                    customAdapterNotifiy();
                }
            });
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    public static void setAdapterWeakReference(CustomAdapter adapter){
        adapterWeakReference = new WeakReference<>(adapter);
    }

    public static void customAdapterNotifiy() {
        CustomAdapter adapter = adapterWeakReference != null ? adapterWeakReference.get() : null;
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    public static StorageManager getStorageManager(){
        return storageManager;
    }
}
