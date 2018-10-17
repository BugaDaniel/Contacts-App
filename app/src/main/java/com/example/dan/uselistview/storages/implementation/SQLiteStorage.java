package com.example.dan.uselistview.storages.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.dan.uselistview.core.Person;
import com.example.dan.uselistview.storages.core.AsyncTask;
import com.example.dan.uselistview.storages.core.BaseStorageManager;

public class SQLiteStorage extends BaseStorageManager {

    private SQLiteStorageHelper sqlHelper;
    private SQLiteDatabase db;
    private InitializeCallback callback;

    @Override
    protected void addPersonInternal(Person addedPerson) {
        sqlHelper.insertContact(addedPerson.getKey(), addedPerson.getFullName(), addedPerson.getPhone(), addedPerson.getUri());
    }

    @Override
    protected void deletePersonInternal(Person person) {
        sqlHelper.deleteContact(person.getKey());
    }

    @Override
    protected void updatePersonToStorage(String key, String name, String phone, Uri uri) {
        sqlHelper.updateContact(key,name,phone,uri);
    }

    @Override
    public void initializeInternal(Context context ,@NonNull InitializeCallback callback) {
        this.callback = callback;
        DataBaseTask dataBaseTask = new DataBaseTask(context);
        dataBaseTask.execute();
    }

    private class DataBaseTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public DataBaseTask(Context context){
            this.context = context;
        }

        @Override
        public Void doInBackground(Void... voids) {
            sqlHelper = new SQLiteStorageHelper(context);
            db = sqlHelper.getWritableDatabase();
            sqlHelper.addContactsFromDataBaseToList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callback.onFinished();
        }
    }

    private class SQLiteStorageHelper extends SQLiteOpenHelper {

        static final String DATABASE_NAME = "contactsDataBase.db";
        static final String CONTACTS_TABLE_NAME = "contacts";
        static final String CONTACTS_COLUMN_KEY = "personKey";
        static final String CONTACTS_COLUMN_NAME = "name";
        static final String CONTACTS_COLUMN_PHONE = "phone";
        static final String CONTACTS_COLUMN_URI = "uri";
        static final String TEXT = "text";

        private SQLiteStorageHelper(Context context) {
            super(context, DATABASE_NAME , null, 2);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(
                    "create table " + CONTACTS_TABLE_NAME  + " (" +
                            CONTACTS_COLUMN_KEY + " " + TEXT + "," +
                            CONTACTS_COLUMN_NAME + " " + TEXT + "," +
                            CONTACTS_COLUMN_PHONE + " " + TEXT + "," +
                            CONTACTS_COLUMN_URI + " " + TEXT + ")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion < 2){
                addColumnInTable(CONTACTS_COLUMN_URI, db);
            }
            // if I add new column :
//            if (oldVersion < 3) {
//                db.execSQL();
//            }
        }

        void addColumnInTable(String newColumnName, SQLiteDatabase db){
            db.execSQL("ALTER TABLE "
                    + CONTACTS_TABLE_NAME + " ADD COLUMN " + newColumnName + " TEXT;");
        }

        void insertContact(String key, String name, String phone, Uri uri) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACTS_COLUMN_KEY, key);
            contentValues.put(CONTACTS_COLUMN_NAME, name);
            contentValues.put(CONTACTS_COLUMN_PHONE, phone);
            if(uri != null){
                contentValues.put(CONTACTS_COLUMN_URI, uri.toString());
            }
            db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        }

        void deleteContact (String key) {
            String[] whereArgs = new String[] { key };
            db.delete(CONTACTS_TABLE_NAME, CONTACTS_COLUMN_KEY + " = ?", whereArgs);
        }

        void updateContact (String key, String name, String phone, Uri uri) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CONTACTS_COLUMN_NAME, name);
            contentValues.put(CONTACTS_COLUMN_PHONE, phone);
            if(uri != null){
                contentValues.put(CONTACTS_COLUMN_URI, uri.toString());
            }
            db.update(CONTACTS_TABLE_NAME, contentValues, CONTACTS_COLUMN_KEY + " = ?", new String[] { key } );
        }

        void addContactsFromDataBaseToList() {

            Cursor cursor =  db.rawQuery( "select * from " + CONTACTS_TABLE_NAME, null );
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                String key = cursor.getString( cursor.getColumnIndex(CONTACTS_COLUMN_KEY ) );
                String name = cursor.getString( cursor.getColumnIndex(CONTACTS_COLUMN_NAME) );
                String phone = cursor.getString( cursor.getColumnIndex(CONTACTS_COLUMN_PHONE ) );

                Uri uri = null;
                if(!cursor.isNull(cursor.getColumnIndex(CONTACTS_COLUMN_URI))){
                    uri = Uri.parse(cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_URI)));
                }

                Person tempPerson;
                tempPerson = Person.Builder.newPerson(key, name, phone, uri);
                personsList.add(tempPerson);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
