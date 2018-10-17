package com.example.dan.uselistview.storages.implementation;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.dan.uselistview.core.Person;
import com.example.dan.uselistview.storages.core.BaseStorageManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class JSONStorage extends BaseStorageManager {

    private File contactsFile;
    private JSONArray jsonArray;

    @Override
    protected void addPersonInternal(Person addedPerson) {
        JSONObject jsonPerson = addedPerson.toJSONObject();
        jsonArray.put(jsonPerson);
        writeJsonArrayToFile();
    }

    private void writeJsonArrayToFile(){
        try {
            FileOutputStream stream = new FileOutputStream(contactsFile);
            Writer outputStreamWriter = new BufferedWriter(new OutputStreamWriter(stream));
            outputStreamWriter.write(jsonArray.toString());
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void deletePersonInternal(Person person) {
        int position = personsList.indexOf(person);
        jsonArray.remove(position);
        writeJsonArrayToFile();
    }

    @Override
    protected void updatePersonToStorage(String key, String name, String phone, Uri uri) {
        Person person = getPerson(key);
        int index = personsList.indexOf(person);

        try {
            @SuppressWarnings("ConstantConditions")
            JSONObject personForEdit = person.toJSONObject();
            jsonArray.put(index, personForEdit);
            writeJsonArrayToFile();
        } catch (Exception e ) {
            e.printStackTrace();
        }

    }

    @Override
    public void initializeInternal(Context context, @NonNull InitializeCallback callback) {

        try {
            String jsonFileContent = getStringFromFile(context);
            if(jsonFileContent != null && !jsonFileContent.isEmpty()){
                jsonArray = new JSONArray(jsonFileContent);
                for (int i = 0; i < jsonArray.length(); i++) {
                    personsList.add(Person.Builder.newPerson(jsonArray.getJSONObject(i)));
                }
                callback.onFinished();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(jsonArray == null)
            jsonArray = new JSONArray();
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String getStringFromFile ( Context context) throws Exception {
        contactsFile = new File(context.getFilesDir(), "contactsList");
        if(contactsFile.exists()){
            FileInputStream fin = new FileInputStream(contactsFile);
            String convertStreamToString = convertStreamToString(fin);
            fin.close();
            return convertStreamToString;
        }
        else{
            contactsFile.createNewFile();
            return null;
        }
    }
}