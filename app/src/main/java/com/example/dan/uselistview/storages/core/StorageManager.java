package com.example.dan.uselistview.storages.core;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.dan.uselistview.core.Person;

import java.util.List;

public interface StorageManager {
    void addPerson(Person addedPerson);
    Person getPerson(String key);
    void deletePerson(Person person);

    void updatePerson(String key, String name, String phone, Uri uri);

    void initialize(Context context, @NonNull InitializeCallback callback);

    List<Person> getPersonList();

    interface InitializeCallback{

        void onFinished();
    }

}
