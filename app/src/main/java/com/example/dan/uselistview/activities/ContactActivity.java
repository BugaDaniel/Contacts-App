package com.example.dan.uselistview.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dan.uselistview.ContactsApplication;
import com.example.dan.uselistview.R;
import com.example.dan.uselistview.core.GlideApp;
import com.example.dan.uselistview.core.Person;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class ContactActivity extends AppCompatActivity {
    EditText phoneText;
    EditText nameText;
    Button callButton;
    Button editButton;
    ImageView contactImage;
    ImageView addContactIcon;
    Toolbar toolbar;

    public String key;
    public boolean isInViewMode;
    public Person selectedPerson;
    public Uri imageUriFromFilePicker = null;

    public static final int PICK_IMAGE = 77;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact_info);

        final Intent intent = getIntent();
        key = intent.getStringExtra("key");
        isInViewMode = intent.getBooleanExtra("isAddButtonPressed", false);

        nameText = findViewById(R.id.person_name);
        phoneText = findViewById(R.id.person_phone);
        callButton = findViewById(R.id.callButton);
        editButton = findViewById(R.id.editButton);
        contactImage = findViewById(R.id.contact_image_in_display_activity);
        addContactIcon = findViewById(R.id.plus);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addContactIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editMode();
            }
        });


        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent callIntent;
                if (ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    callIntent = new Intent(Intent.ACTION_DIAL);
                }else {
                    callIntent = new Intent(Intent.ACTION_CALL);
                }

                String phoneForIntent = "tel:" + selectedPerson.getPhone();
                callIntent.setData(Uri.parse(phoneForIntent));
                startActivity(callIntent);
            }
        });

        if(!isInViewMode){
            editMode();
            GlideApp.with(getApplicationContext()).load(Person.getUriFromDefaultDrawableId()).skipMemoryCache(true).into(contactImage);
        }
        else{
            viewMode();
            if(key != null){
                selectedPerson = ContactsApplication.getStorageManager().getPerson(key);
                final String phoneNr = selectedPerson.getPhone();
                phoneText.setText(phoneNr);
                final String personName = selectedPerson.getFullName();
                nameText.setText(personName);
                Uri uri = selectedPerson.getUri();
                GlideApp
                        .with(getApplicationContext())
                        .load(uri)
                        .override(contactImage.getWidth(), contactImage.getHeight())
                        .into(contactImage);
            }
        }
    }

    private void viewMode(){
        isInViewMode = true;
        nameText.setEnabled(false);
        phoneText.setEnabled(false);
        callButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        addContactIcon.setVisibility(View.GONE);
    }

    private void editMode(){
        isInViewMode = false;
        nameText.setEnabled(true);
        phoneText.setEnabled(true);
        callButton.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        addContactIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(isInViewMode || key == null) {
            super.onBackPressed();
        }else {
            viewMode();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.buttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.save_button:
                String name = nameText.getText().toString();
                String phone = phoneText.getText().toString();

                if(selectedPerson !=null){
                    // imageUriFromFilePicker can be null if image not picked, null check is in Person .toString / .getUri
                    ContactsApplication.getStorageManager().updatePerson(selectedPerson.getKey(),name,phone, imageUriFromFilePicker);

                    Toast.makeText(getApplicationContext(), "Saved successful",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    Person newPerson = Person.Builder.newPerson(null,name, phone, imageUriFromFilePicker);
                    if(newPerson == null){
                        Toast.makeText(getApplicationContext(), "INVALID CONTACT",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }else {
                        selectedPerson = newPerson;
                        ContactsApplication.getStorageManager().addPerson(selectedPerson);
                    }
                }

                ContactsApplication.customAdapterNotifiy();
                viewMode();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION); intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUriFromFilePicker = data.getData();
            if(imageUriFromFilePicker != null){
                GlideApp
                        .with(getApplicationContext())
                        .load(imageUriFromFilePicker)
                        .override(contactImage.getWidth(), contactImage.getHeight())
                        .skipMemoryCache(true)
                        .into(contactImage);
            }
            else{
                GlideApp
                        .with(getApplicationContext())
                        .load(Person.getUriFromDefaultDrawableId())
                        .skipMemoryCache(true)
                        .into(contactImage);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
