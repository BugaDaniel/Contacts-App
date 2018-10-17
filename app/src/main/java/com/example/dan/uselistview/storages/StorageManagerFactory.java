package com.example.dan.uselistview.storages;

import com.example.dan.uselistview.storages.core.StorageManager;
import com.example.dan.uselistview.storages.implementation.JSONStorage;
import com.example.dan.uselistview.storages.implementation.SQLiteStorage;
import com.example.dan.uselistview.storages.implementation.SharedPrefStorage;

import java.lang.reflect.Constructor;

public class StorageManagerFactory {

    public enum StorageManagerType {

        SHARED_PREF(SharedPrefStorage.class.getSimpleName()),
        SQL_LITE(SQLiteStorage.class.getSimpleName()),
        JSON(JSONStorage.class.getSimpleName());

        String className;

        StorageManagerType(String className){
            this.className = className;
        }

        @Override
        public String toString() {
            return className;
        }
    }

    public static StorageManager create(StorageManagerType type) throws Exception{

        String sClassName = "com.example.dan.uselistview.storages.implementation." + type.toString();
        try {
            Class<?> storageManagerClass = Class.forName(sClassName);
            Constructor constructor = storageManagerClass.getConstructor();
            Object object = constructor.newInstance();

           return (StorageManager)object;

        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException ("Class not found");
        }
    }
}
