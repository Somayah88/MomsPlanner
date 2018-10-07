package com.example.somayahalharbi.momsplanner;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImportantContacts extends AppCompatActivity {
    @BindView(R.id.show_map)
    Button showMap;
    @BindView(R.id.fab)
    FloatingActionButton addContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_contacts);
        ButterKnife.bind(this);
        final Context context=this;

        showMap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //TODO: Create map intent to display address

            }
        });
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContacts();
            }
        });
    }

    private void addContacts() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_contact, null);
        dialogBuilder.setView(dialogView);
        final Button addButton = dialogView.findViewById(R.id.add_contact);
        final Button cancelButton = dialogView.findViewById(R.id.cancel_btn);


        final AlertDialog dialog = dialogBuilder.create();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add contact functionality goes here
            }

        });
        dialog.show();
    }

}
