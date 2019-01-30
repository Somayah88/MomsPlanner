package com.example.somayahalharbi.momsplanner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.models.Contacts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsAdapterViewHolder> {
    private static FirebaseDatabase database;
    private ArrayList<Contacts> contacts = new ArrayList<>();

    @NonNull
    @Override
    public ContactsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutFromListItem = R.layout.contact_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutFromListItem, viewGroup, false);

        return new ContactsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapterViewHolder viewHolder, int position) {
        String title = contacts.get(position).getTitle();
        String email = contacts.get(position).getEmailAddress();
        String phone = contacts.get(position).getPhone();
        String contactAddress = contacts.get(position).getStreetAddress();
        String contactCity = contacts.get(position).getCity();
        String contactState = contacts.get(position).getState();
        String contactZipCode = contacts.get(position).getZipCode();
        String contactsUnit = contacts.get(position).getUnit();

        String fullAddress;
        if (!contactsUnit.equals("null")) {

            fullAddress = contactAddress + " " + contactsUnit + ", " + contactCity + ", " + contactState + " " + contactZipCode;

        } else {


            fullAddress = contactAddress + ", " + contactCity + ", " + contactState + " " + contactZipCode;

        }
        viewHolder.contactTitle.setText(title);
        viewHolder.contactEmail.setText(email);
        viewHolder.contactPhoneNo.setText(phone);
        viewHolder.address.setText(fullAddress);


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setData(ArrayList<Contacts> contactList) {
        clear();
        contacts = contactList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = contacts.size();
        contacts.clear();
        notifyItemRangeChanged(0, size);
    }

    public void removeContact(int position) {
        DatabaseReference contactsRef;
        FirebaseUser user;
        FirebaseAuth mFirebaseAuth;
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();

        }
        Contacts contact = contacts.get(position);
        contactsRef = database.getReference("users").child(user.getUid()).child("contacts").child(contact.getId());
        contactsRef.removeValue();
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class ContactsAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView contactTitle;
        @BindView(R.id.contact_address)
        TextView address;
        @BindView(R.id.email)
        TextView contactEmail;
        @BindView(R.id.phone_no)
        TextView contactPhoneNo;


        public ContactsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
