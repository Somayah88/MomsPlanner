package com.example.somayahalharbi.momsplanner;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.somayahalharbi.momsplanner.adapters.ContactsAdapter;
import com.example.somayahalharbi.momsplanner.models.Contacts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImportantContactsActivity extends AppCompatActivity {

    private static FirebaseDatabase database;
    @BindView(R.id.fab)
    FloatingActionButton addContact;
    @BindView(R.id.contacts_recyclerView)
    RecyclerView contactsRecyclerView;
    DatabaseReference myRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    private ContactsAdapter contactsAdapter;
    private ArrayList<Contacts> contactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_contacts);
        ButterKnife.bind(this);
        final Context context = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();

        }
        myRef = database.getReference("users").child(user.getUid()).child("contacts");
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContacts();
            }
        });
        contactsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager contactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);
        contactsAdapter = new ContactsAdapter();
        contactsRecyclerView.setAdapter(contactsAdapter);
        contactsAdapter.setData(contactsList);

        ValueEventListener contactsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactsList = new ArrayList<>();
                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    Contacts contact = contactSnapshot.getValue(Contacts.class);
                    contactsList.add(contact);
                }
                contactsAdapter.setData(contactsList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ContactsActivity", "loadMember:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(contactsListener);


    }


    private void addContacts() {
        //TODO: validate data and put default for nt available data
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_contact, null);
        dialogBuilder.setView(dialogView);
        final Button addButton = dialogView.findViewById(R.id.add_contact);
        final Button cancelButton = dialogView.findViewById(R.id.cancel_btn);
        final EditText title = dialogView.findViewById(R.id.contact_title);
        final EditText address = dialogView.findViewById(R.id.address1);
        final EditText city = dialogView.findViewById(R.id.city);
        final EditText state = dialogView.findViewById(R.id.state);
        final EditText unit = dialogView.findViewById(R.id.unit);
        final EditText zipCode = dialogView.findViewById(R.id.zip_code);
        final EditText phone = dialogView.findViewById(R.id.phone);
        final EditText email = dialogView.findViewById(R.id.email);
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
                Contacts contacts = new Contacts();
                contacts.setTitle(title.getText().toString());
                contacts.setStreetAddress(address.getText().toString());
                contacts.setUnit(unit.getText().toString());
                contacts.setZipCode(zipCode.getText().toString());
                contacts.setState(state.getText().toString());
                contacts.setPhone(phone.getText().toString());
                contacts.setEmailAddress(email.getText().toString());
                contacts.setCity(city.getText().toString());
                myRef.push().setValue(contacts);
                dialog.dismiss();


            }

        });
        dialog.show();
    }

}
