package com.example.dan.uselistview.storages.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dan.uselistview.core.Person;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStorageManager implements StorageManager {
    protected List<Person> personsList = new ArrayList<>();
    private Context context;

    @Override
    public final void addPerson(Person addedPerson) {
        if(addedPerson.getUri() != Person.getUriFromDefaultDrawableId()){
            Uri localStorageImageUri = saveImageToInternalStorage(addedPerson.getUri(), addedPerson.getKey());
            addedPerson.setUri(localStorageImageUri);
        }
        personsList.add(addedPerson);
        addPersonInternal(addedPerson);
    }

    protected abstract void addPersonInternal(Person addedPerson);

    @Override
    public void deletePerson(Person person) {
        deleteImageFile(person.getUri());
        deletePersonInternal(person);
        personsList.remove(person);
    }

    protected abstract void deletePersonInternal(Person person);

    @Override
    public final void updatePerson(String key, String name, String phone, Uri uri) {
        if(Person.checkValidPerson(name,phone)){
            for (Person person: personsList) {
                if(person.getKey().equals(key)){
                    person.setFullName(name);
                    person.setPhone(phone);
                    Uri localStorageImageUri = saveImageToInternalStorage(uri, key);
                    deleteImageFile(person.getUri());
                    person.setUri(localStorageImageUri);
                    updatePersonToStorage(key,name,phone,localStorageImageUri);
                    break;
                }
            }
        }
    }

    protected abstract void updatePersonToStorage(String key, String name, String phone, Uri uri);

    @Override
    public void initialize(Context context, @NonNull InitializeCallback callback) {
        this.context = context;
        initializeInternal(context, callback);
    }

    protected abstract void initializeInternal(Context context, @NonNull InitializeCallback callback);

    @Override
    public final List<Person> getPersonList() {
        return personsList;
    }

    @Override
    public final Person getPerson(String key) {
        for (Person person: personsList) {
            if(person.getKey().equals(key)){
                return person;
            }
        }
        return null;
    }

    private void deleteImageFile(Uri personUri){
        if(personUri != null){
            File fileToDelete = new File(personUri.getPath());
            if(fileToDelete.exists()){
                boolean deleteFile = fileToDelete.delete();
                Log.e("del" , "deleteFile = " + deleteFile);
            }
        }
    }

    private Uri saveImageToInternalStorage(Uri uri, String imageName){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myImagePath=new File(directory,imageName + ".jpg");
        FileOutputStream fos ;
        try {
            fos = new FileOutputStream(myImagePath);
            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.parse("file://" + myImagePath.getAbsolutePath());
    }
}
