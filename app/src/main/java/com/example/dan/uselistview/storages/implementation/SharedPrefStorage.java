package com.example.dan.uselistview.storages.implementation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.dan.uselistview.core.Person;
import com.example.dan.uselistview.storages.core.BaseStorageManager;
import java.util.Map;

public class SharedPrefStorage extends BaseStorageManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void addPersonInternal(Person addedPerson) {
        editor.putString(addedPerson.getKey(), addedPerson.toString());
        editor.apply();
    }

    @Override
    protected void deletePersonInternal(Person person) {
        editor.remove(person.getKey());
        editor.apply();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void updatePersonToStorage(String key, String name, String phone, Uri uri) {
        Person person = getPerson(key);
        editor.putString(key, person.toString());
        editor.apply();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initializeInternal(Context context,@NonNull InitializeCallback callback) {

        this.prefs = context.getSharedPreferences("contacts", Context.MODE_PRIVATE);
        editor = prefs.edit(); // Returns a new instance of the {@link Editor} interface, allowing you to modify the values in this SharedPreferences object.

        Map<String, String> personMap = (Map<String, String>) prefs.getAll();
        for (Map.Entry<String, String> personEntry: personMap.entrySet()) {
            Person tempPerson = Person.Builder.newPerson(personEntry.getKey(), personEntry.getValue());
            personsList.add(tempPerson);
        }
        callback.onFinished();
    }
}
