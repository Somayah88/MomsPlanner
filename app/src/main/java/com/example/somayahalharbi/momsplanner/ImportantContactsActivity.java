package com.example.somayahalharbi.momsplanner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private DatabaseReference contactsRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    private ContactsAdapter contactsAdapter;
    private ArrayList<Contacts> contactsList = new ArrayList<>();
    //------------save UI state-------------
    private static final String CONTACTS_LIST="contact_list";

    //------------- Save Dialog state------------
    private static final String TITLE_TEXT="title_text";
    private static final String ADDRESS_TEXT="address_text";
    private static final String CITY_TEXT="city_text";
    private static final String STATE_TEXT="state_text";
    private static final String UNIT_TEXT="unit_text";
    private static final String ZIPCODE_TEXT="zip_text";
    private static final String PHONE_TEXT="phone_text";
    private static final String EMAIL_TEXT="email_text";
    private static final String DIALOG_STATUS="dialog_status";
    private boolean dialogShown;
    //------------ Dialog Views-----------
     AlertDialog dialog;
     Button addButton ;
     Button cancelButton ;
     EditText title ;
    EditText address ;
    EditText city ;
     EditText state ;
    EditText unit ;
     EditText zipCode ;
     EditText phone ;
     EditText email;
     //------------------------------------------

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
        contactsRef = database.getReference("users").child(user.getUid()).child("contacts");
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContacts();
            }
        });
        LinearLayoutManager contactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);
        contactsAdapter = new ContactsAdapter();
        contactsRecyclerView.setAdapter(contactsAdapter);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    contactsAdapter.removeContact(position);


                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(contactsRecyclerView);
        if(savedInstanceState!=null && savedInstanceState.containsKey(CONTACTS_LIST))
        {
            contactsList=savedInstanceState.getParcelableArrayList(CONTACTS_LIST);
            contactsAdapter.setData(contactsList);

        }
        else
            getContacts();

    }


    private void getContacts(){

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
                Log.w("ContactsActivity", "loadContacts:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();

            }
        };
        contactsRef.addValueEventListener(contactsListener);
    }

    private void addContacts() {
        //TODO: validate data and put default for nt available data
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_contact, null);
        dialogBuilder.setView(dialogView);
        addButton = dialogView.findViewById(R.id.add_contact);
       cancelButton = dialogView.findViewById(R.id.cancel_btn);
         title = dialogView.findViewById(R.id.contact_title);
         address = dialogView.findViewById(R.id.address1);
         city = dialogView.findViewById(R.id.city);
         state = dialogView.findViewById(R.id.state);
         unit = dialogView.findViewById(R.id.unit);
       zipCode = dialogView.findViewById(R.id.zip_code);
         phone = dialogView.findViewById(R.id.phone);
         email = dialogView.findViewById(R.id.email);
       dialog = dialogBuilder.create();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.contact_title_empty), Toast.LENGTH_SHORT).show();

                }
                if (address.getText().toString().isEmpty() && phone.getText().toString().isEmpty() && email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.contact_empty), Toast.LENGTH_SHORT).show();

                } else {


                    Contacts contacts = new Contacts();
                    contacts.setTitle(title.getText().toString());
                    contacts.setStreetAddress(address.getText().toString());
                    contacts.setUnit(unit.getText().toString());
                    contacts.setZipCode(zipCode.getText().toString());
                    contacts.setState(state.getText().toString());
                    contacts.setPhone(phone.getText().toString());
                    contacts.setEmailAddress(email.getText().toString());
                    contacts.setCity(city.getText().toString());
                    String key = contactsRef.push().getKey();
                    contacts.setId(key);
                    contactsRef.child(key).setValue(contacts);
                    getContacts();
                    dialog.dismiss();
                }

            }

        });
        dialog.show();
    }

    @Override
    public void onPause(){
        super.onPause();
        dialogShown = dialog != null && dialog.isShowing();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(CONTACTS_LIST,contactsList);
        outState.putBoolean(DIALOG_STATUS, dialogShown);
        if(dialogShown){
            //Save the dialog views state
            if(!title.getText().toString().isEmpty())
                outState.putString(TITLE_TEXT,title.getText().toString());
            if(!address.getText().toString().isEmpty())
                outState.putString(ADDRESS_TEXT, address.getText().toString());
            if(!city.getText().toString().isEmpty())
                outState.putString(CITY_TEXT,city.getText().toString());

            if(!state.getText().toString().isEmpty())
                outState.putString(STATE_TEXT,state.getText().toString());

            if(!unit.getText().toString().isEmpty())
                outState.putString(UNIT_TEXT,unit.getText().toString());

            if(!zipCode.getText().toString().isEmpty())
                outState.putString(ZIPCODE_TEXT,zipCode.getText().toString());

            if(!phone.getText().toString().isEmpty())
                outState.putString(PHONE_TEXT,phone.getText().toString());

            if(!email.getText().toString().isEmpty())
                outState.putString(EMAIL_TEXT,email.getText().toString());


        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(DIALOG_STATUS)){
            addContacts();
            if(savedInstanceState.containsKey(TITLE_TEXT)){
                String titleText=savedInstanceState.getString(TITLE_TEXT);
                title.setText(titleText);
            }
            if(savedInstanceState.containsKey(ADDRESS_TEXT)){
                String addressText=savedInstanceState.getString(ADDRESS_TEXT);
               address.setText(addressText);
            }
            if(savedInstanceState.containsKey(CITY_TEXT)){
                String cityText=savedInstanceState.getString(CITY_TEXT);
                city.setText(cityText);

            }
            if(savedInstanceState.containsKey(UNIT_TEXT)){
                String unitText=savedInstanceState.getString(UNIT_TEXT);
                unit.setText(unitText);

            }
            if(savedInstanceState.containsKey(STATE_TEXT)){

                String stateText=savedInstanceState.getString(STATE_TEXT);
                state.setText(stateText);
            }
            if(savedInstanceState.containsKey(ZIPCODE_TEXT)){
                String zipCodeText=savedInstanceState.getString(ZIPCODE_TEXT);
                zipCode.setText(zipCodeText);

            }
            if(savedInstanceState.containsKey(PHONE_TEXT)){
                String phoneText=savedInstanceState.getString(PHONE_TEXT);
                phone.setText(phoneText);

            }
            if(savedInstanceState.containsKey(EMAIL_TEXT)){
                String emailText=savedInstanceState.getString(EMAIL_TEXT);
                email.setText(emailText);

            }

        }
    }
}
//TODO: display error messages as needed
//TODO: fix bug when the device rotates recyclerView becomes empty
