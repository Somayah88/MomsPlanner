package com.example.somayahalharbi.momsplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.somayahalharbi.momsplanner.adapters.AppointmentsAdapter;
import com.example.somayahalharbi.momsplanner.models.Appointment;
import com.example.somayahalharbi.momsplanner.models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentsActivity extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton addAppointmentFab;
    @BindView(R.id.appointment_recyclerView)
    RecyclerView apptRecyclerView;
    @BindView(R.id.member_spinner)
    Spinner memberSpinner;
    private AppointmentsAdapter appointmentAdapter;
    private ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();
    public static final String APPOINTMENT_PATH = "appointment";
    private static FirebaseDatabase database;
    DatabaseReference apptRef;
    DatabaseReference ownersRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    String ownerId = "0";
    private ArrayList<Member> members = new ArrayList<Member>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        ButterKnife.bind(this);
        //setDummyData();// TODO: Remove this


        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }
        apptRef = database.getReference("users").child(user.getUid()).child(APPOINTMENT_PATH);
        apptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList = new ArrayList<>();
                for (DataSnapshot apptsnapshot : dataSnapshot.getChildren()) {
                    Appointment appt = apptsnapshot.getValue(Appointment.class);
                    appointmentList.add(appt);


                }
                appointmentAdapter.setData(appointmentList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AppointmentActivity", "loadAppointments:onCancelled", databaseError.toException());


            }
        });

        ownersRef = database.getReference("users").child(user.getUid()).child("member");
        addAppointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAppointment();
            }
        });
        apptRecyclerView.setHasFixedSize(true);
        LinearLayoutManager appointmentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        apptRecyclerView.setLayoutManager(appointmentLayoutManager);
        appointmentAdapter = new AppointmentsAdapter();
        apptRecyclerView.setAdapter(appointmentAdapter);
        appointmentAdapter.setData(appointmentList);
        // CAN BE SEPERATE METHID

        String[] owners = {"Faisal", "Somayah", "Sarah"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getMembers());
        memberSpinner.setAdapter(adapter);
        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: implement this

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    //TODO: Delete this and get real data
    private void setDummyData() {
        Appointment appointment = new Appointment();
        appointment.setApptDate("11/12/2018");
        appointment.setApptLocation("South Slope Pediatrics");
        appointment.setApptOwner("Sarah");
        appointment.setApptTime("9:30 AM");
        appointment.setApptTitle("Sarah 15 months checkup");
        appointmentList.add(appointment);
        appointment = new Appointment();
        appointment.setApptDate("11/1/2019");
        appointment.setApptLocation("Happy Teeth Dentist");
        appointment.setApptOwner("Faisal");
        appointment.setApptTime("11:30 AM");
        appointment.setApptTitle("Dentist cleaning appointment");
        appointmentList.add(appointment);
        appointment = new Appointment();
        appointment.setApptDate("8/11/2018");
        appointment.setApptLocation("Faisal's school");
        appointment.setApptOwner("Faisal");
        appointment.setApptTime("3:00 PM");
        appointment.setApptTitle("Parent teachers conferences");
        appointmentList.add(appointment);


    }

    private ArrayList<String> getMembers() {

        final ArrayList<String> owners = new ArrayList<>();
        final ArrayList<Member> members = new ArrayList<>();

        ownersRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ownersSnapshot : dataSnapshot.getChildren()) {
                    Member member = ownersSnapshot.getValue(Member.class);
                    members.add(member);
                    String owner = member.getName();
                    owners.add(owner);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AppointmentActivity", "loadAppointments:onCancelled", databaseError.toException());


            }
        });
        return owners;


    }

    private void addAppointment() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_appointment, null);
        dialogBuilder.setView(dialogView);
        // ButterKnife.bind(this, dialogView);
        //TODO: replace this with Butterknife
        final EditText apptTitle = dialogView.findViewById(R.id.appt_title);
        final Button addButton = dialogView.findViewById(R.id.add_appt_button);
        final Button cancelButton = dialogView.findViewById(R.id.cancel_appt_button);
        final EditText apptLocation = dialogView.findViewById(R.id.appt_location);
        final EditText apptDate = dialogView.findViewById(R.id.appt_date);
        final EditText apptTime = dialogView.findViewById(R.id.appt_time);


        // Spinner
        //   String[] owners = {"Faisal", "Somayah", "Sarah"};


        final Spinner ownersSpinner = dialogView.findViewById(R.id.owner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getMembers());
        ownersSpinner.setAdapter(adapter);
        ownersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: implement this
                ownerId = members.get(i).getId();
                Toast.makeText(getApplicationContext(), members.get(i).getName(), Toast.LENGTH_LONG).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ownerId = "0";


            }
        });
        //TODO: extract this part to reuse it between all activities.
        //Date Picker Dialog
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                apptDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        apptDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(dialogView.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Time picker dialog
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                myCalendar.set(Calendar.HOUR, hour);
                myCalendar.set(Calendar.MINUTE, minutes);
                //TODO: show AM or PM
               /*
               String myFormat = "hh:mm a"; // your own format
               SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
               String  formated_time = sdf.format(myCalendar.getTime());
               apptTime.setText(formated_time);
               */
                apptTime.setText(hour + ":" + minutes);


            }
        };
        apptTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(dialogView.getContext(), time,
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        true).show();
            }
        });


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

                Appointment appt = new Appointment();
                appt.setApptTitle(apptTitle.getText().toString());
                appt.setApptLocation(apptLocation.getText().toString());
                appt.setApptTime(apptTime.getText().toString());
                appt.setApptDate(apptDate.getText().toString());
                //  if(!ownerId.equals("0"))
                //apptRef.child(ownerId).push().setValue(appt);
                apptRef.push().setValue(appt);

                // appt.setApptOwner(ownersSpinner.getSelectedItem().toString());

                dialog.dismiss();

            }

        });
        dialog.show();
    }



}
