package com.example.somayahalharbi.momsplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.somayahalharbi.momsplanner.helpers.WidgetUpdateHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.somayahalharbi.momsplanner.helpers.ServiceSchedulingHelper.scheduleAppointmentReminderService;
import static com.example.somayahalharbi.momsplanner.helpers.ServiceSchedulingHelper.scheduleOverdueTaskNotificationService;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mFirebaseauth;
    @BindView(R.id.to_do_btn)
    Button toDoButton;
    @BindView(R.id.appointment_btn)
    Button appointmentButton;
    @BindView(R.id.contacts_btn)
    Button contactsButton;
    @BindView(R.id.family_members_btn)
    Button familyMembersButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        final Context context=this;

        mFirebaseauth=FirebaseAuth.getInstance();
        final FirebaseUser user =mFirebaseauth.getCurrentUser();
        authListener=new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

            }
        };
        contactsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent contactsIntent = new Intent(context, ImportantContactsActivity.class);
                startActivity(contactsIntent);
            }
        });
        familyMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent familyMembersIntent=new Intent(context, FamilyActivity.class);
                startActivity(familyMembersIntent);

            }
        });
        toDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toDoIntent=new Intent(context,ToDoActivity.class);
                startActivity(toDoIntent);

            }
        });
        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appointmentsIntent=new Intent(context,AppointmentsActivity.class);
                startActivity(appointmentsIntent);

            }
        });
        scheduleAppointmentReminderService(this);
       // scheduleOverdueTaskNotificationService(this);
        //TODO:  uncomment this
        WidgetUpdateHelper.updateWidgetData(MainActivity.this);
        //TODO: this doesn't work , Why??


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseauth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mFirebaseauth.removeAuthStateListener(authListener);
        }
    }
    public void signOut() {
        mFirebaseauth.signOut();
    }

}

//TODO: Firebase jobDispatcher not working
//TODO: save the app state when the device configuration changes
//TODO: add app widget to display the to do items
//TODO: unregister listeners.