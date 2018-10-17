package com.example.dan.uselistview.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.dan.uselistview.ContactsApplication;
import com.example.dan.uselistview.R;
import com.example.dan.uselistview.core.CustomAdapter;
import com.example.dan.uselistview.core.Person;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static List<Person> persons = ContactsApplication.getStorageManager().getPersonList();
    private ListView simpleList;
    private Button addButton;
    CustomAdapter customAdapter;
    private boolean isSnackbar = true;
    boolean detailActivityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                1);

        simpleList = findViewById(R.id.list_view);
        customAdapter = new CustomAdapter(getApplicationContext(), persons);
        ContactsApplication.setAdapterWeakReference(customAdapter);

        simpleList.setAdapter(customAdapter);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!detailActivityStarted){
                    detailActivityStarted = true;
                    Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                    intent.putExtra("key", persons.get(position).getKey());
                    intent.putExtra("isAddButtonPressed", true);
                    startActivity(intent);
                }
            }
        });

        simpleList.setLongClickable(true);
        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> a, View v, final int position, long id) {
                if(isSnackbar){
                    deleteWithSnackbar(position);
                }
                else{
                    deleteWithAlertDialog(position);
                }
                customAdapter.notifyDataSetChanged();
                return true;
            }
        });

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               Intent addContactIntent = new Intent(MainActivity.this, ContactActivity.class);
               addContactIntent.putExtra("key", -1);
               startActivity(addContactIntent);
            }
        });
    }

    public void deleteWithSnackbar(final int position) {

        // Create snackbar
        String message = "Contact deleted";
        int duration = Snackbar.LENGTH_LONG;
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), message, duration);
        final Person person = ContactsApplication.getStorageManager().getPersonList().get(position);
        ContactsApplication.getStorageManager().deletePerson(person);
        // Set an action on it, and a handler
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                ContactsApplication.getStorageManager().addPerson(person);
                customAdapter.notifyDataSetChanged();
                Snackbar.make(getWindow().getDecorView(),"Contact restored", Snackbar.LENGTH_SHORT).show();
            }
        }).show();
    }

    public void deleteWithAlertDialog(final int position){
        AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
        adb.setTitle("Delete?");
        adb.setMessage("Are you sure you want to delete contact?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Person person = ContactsApplication.getStorageManager().getPersonList().get(position);
                ContactsApplication.getStorageManager().deletePerson(person);
            }});
        adb.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        detailActivityStarted = false;
    }
}
