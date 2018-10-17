package com.example.dan.uselistview.core;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;

public class Person {
    private String fullName;
    private String phone;
    private String key;
    private Uri personUri;
    private static Uri defaultUri;

    static final String KEY_KEY = "key";
    static final String NAME_KEY = "name";
    static final String PHONE_KEY = "phone";
    static final String URI_KEY = "uri";

    Person(String key, String fullName, String phone, Uri uri){
        if(key != null) {
            this.key = key;
        }
        else {
            this.key = UUID.randomUUID().toString();
        }
        this.fullName = fullName;
        this.phone = phone;
        this.personUri = uri;
    }

    Person(String key, String serializedPerson){

        String[] personDetails = serializedPerson.split(";");
        this.key = key;
        this.fullName = personDetails[0];
        this.phone = personDetails[1];
        if(personDetails.length > 2){
            this.personUri = Uri.parse(personDetails[2]);
        }
    }

    public static class Builder{
        public static Person newPerson(String key, String fullName,String phone,Uri uri){
            if(checkValidPerson(fullName, phone)) {
                return new Person(key, fullName, phone, uri);
            }
            return null;
        }
        public static Person newPerson(String key, String serializedPerson){
            return new Person(key, serializedPerson);
        }
        public static Person newPerson(JSONObject personJson){
            try{
                String key = personJson.getString(KEY_KEY);
                String name = personJson.getString(NAME_KEY);
                String phone = personJson.getString(PHONE_KEY);

                Uri uri = null;
                if(personJson.has(URI_KEY)){
                    uri = Uri.parse(personJson.getString(URI_KEY));
                }
                return new Person(key, name, phone, uri);
            }catch (Exception e){
                return  null;
            }
        }
    }

    public static boolean checkValidPerson(String fullName, String phone){
       return !fullName.isEmpty() && !phone.isEmpty();
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setFullName(String fullName){
        this.fullName=fullName;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }


    @Override
    public String toString(){
        if(personUri != null){
            return fullName + ";" + phone + ";" + personUri.toString();
        }
        else{
            return fullName + ";" + phone;
        }
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put(KEY_KEY, getKey());
            jsonObject.put(NAME_KEY, getFullName());
            jsonObject.put(PHONE_KEY, getPhone());
            if(personUri != null){
                jsonObject.put(URI_KEY, personUri);
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getKey() {
        return key;
    }

    public void setUri(Uri uri){
        personUri = uri;
    }

    public Uri getUri() {
        if(personUri != null)
            return personUri;
        else {
            return getUriFromDefaultDrawableId();
        }
    }

    public static Uri getUriFromDefaultDrawableId() {
        if(defaultUri == null){
            defaultUri = Uri.parse("android.resource://com.example.dan.uselistview/drawable/contacts_icon");
        }
       return defaultUri;
    }
}
