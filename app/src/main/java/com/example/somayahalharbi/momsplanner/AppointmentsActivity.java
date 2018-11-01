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
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.example.somayahalharbi.momsplanner.adapters.AppointmentsAdapter;
import com.example.somayahalharbi.momsplanner.models.Appointment;
import com.example.somayahalharbi.momsplanner.models.Member;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentsActivity extends AppCompatActivity {
    public static final String APPOINTMENT_PATH = "appointment";

    private static FirebaseDatabase database;
    @BindView(R.id.fab)
    FloatingActionButton addAppointmentFab;
    @BindView(R.id.appointment_recyclerView)
    RecyclerView apptRecyclerView;
    @BindView(R.id.appt_member_spinner)
    Spinner memberSpinner;
    //DatabaseReference rootRef;
    DatabaseReference apptRef;
    DatabaseReference ownersRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    String ownerId = "0";
    ArrayList<Member> members;
    ArrayList<String> owners;
    private AppointmentsAdapter appointmentAdapter;
    private ArrayList<Appointment> appointmentList;
    private int mPosition;
    private ArrayList<String> spinnerMembers;
    private int filterSelection=-1;
    //------------------ Dialog save status-----------
    private boolean dialogShown;
    private static final String DIALOG_STATUES="dialog_status";
    private static final String APPOINTMENT_TITLE_TEXT="appointment_text";
    private static final String LOCATION_TEXT="location_text";
    private static final String APPT_DATE_TEX="appt_date_text";
    private static final String APPT_TIME_TEXT="appt_time_text";
    //---------------- UI save status-----------------
    private static final String FILTER_SELECTION="filter_selection";
    private static final String APPOINTMENTS_LIST="appt_list";
    private static final String MEMBERS_LIST="members_list";
    private static final String OWNERS_LIST="owners_list";
    //-------------- Dialog Views -----------
    private AlertDialog dialog;
    private EditText apptTitle;
    private Button addButton ;
    private Button cancelButton ;
    private EditText apptLocation ;
    private EditText apptDate ;
    private EditText apptTime ;
    private Spinner ownersSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        ButterKnife.bind(this);
        Log.v("Appointment Activity", "On  create called");
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        apptRef = database.getReference("users").child(user.getUid()).child(APPOINTMENT_PATH);


        addAppointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAppointment();
            }
        });


        LinearLayoutManager appointmentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        apptRecyclerView.setLayoutManager(appointmentLayoutManager);
        appointmentAdapter = new AppointmentsAdapter();
        apptRecyclerView.setAdapter(appointmentAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    appointmentAdapter.remove(position);

                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(apptRecyclerView);



        if(savedInstanceState!=null && savedInstanceState.containsKey(APPOINTMENTS_LIST))
        {
                filterSelection=savedInstanceState.getInt(FILTER_SELECTION);
                appointmentList=savedInstanceState.getParcelableArrayList(APPOINTMENTS_LIST);
                members=savedInstanceState.getParcelableArrayList(MEMBERS_LIST);
                owners=savedInstanceState.getStringArrayList(OWNERS_LIST);
               appointmentAdapter.setData(appointmentList);
               createFilterSpinner();

        }
        else {

            getMembers();
            getAllData();

        }



    }


    private void getAllData() {

        apptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList = new ArrayList<>();
                for (DataSnapshot apptSnapshot : dataSnapshot.getChildren()) {
                    Appointment appt = apptSnapshot.getValue(Appointment.class);
                    appointmentList.add(appt);


                }
                Log.w("GetAllData", "Get All data was just executed and selected position is " + filterSelection);

                appointmentAdapter.setData(appointmentList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AppointmentActivity", "loadAppointments:onCancelled", databaseError.toException());


            }
        });
    }

    private void getData(int position) {
        if (position < owners.size() - 1)
            getFilteredData(position);

        else
            getAllData();
    }

    private void createFilterSpinner() {
        spinnerMembers = new ArrayList<>();
        spinnerMembers.addAll(owners);
        spinnerMembers.remove(owners.size() - 1);
        spinnerMembers.add("All");


        ArrayAdapter<String> membersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerMembers);
        membersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // final Spinner memberSpinner=findViewById(R.id.appt_member_spinner);
        Log.w("create list", "Members list has " + members.size());
        Log.w("create list", "Owners list has " + owners.size());
        memberSpinner.setAdapter(membersAdapter);
        if(filterSelection==-1)
        memberSpinner.setSelection(spinnerMembers.size() - 1);
        else
            memberSpinner.setSelection(filterSelection);



        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.w("Filter Selected", "position is " + i);
                filterSelection = i;
                getData(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        membersAdapter.notifyDataSetChanged();


    }

    private void resetMemberSpinner() {
        memberSpinner.setSelection(spinnerMembers.size() - 1);
        filterSelection = spinnerMembers.size() - 1;

    }

    private void getFilteredData(int position) {

        Log.w("getFilkteredData", "Members list has " + members.size());
        Log.w("getFilteredDatas", "Owners list has " + owners.size());

        Query queryRef = apptRef.orderByChild("ownerId").equalTo(members.get(position).getId());
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList = new ArrayList<>();
                for (DataSnapshot ownersSnapshot : dataSnapshot.getChildren()) {
                    Appointment appt = ownersSnapshot.getValue(Appointment.class);
                    appointmentList.add(appt);

                }

                appointmentAdapter.setData(appointmentList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.w("GetFilteredData", "Get Filtered data was just executed");




    }


    private void getMembers() {
        ownersRef = database.getReference("users").child(user.getUid()).child("member");


        owners = new ArrayList<>();
        members = new ArrayList<>();

        ownersRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ownersSnapshot : dataSnapshot.getChildren()) {
                    Member member = ownersSnapshot.getValue(Member.class);
                    members.add(member);
                    String owner = member.getName();
                    owners.add(owner);
                }
                Log.w("getMembers", "Members list has " + members.size());
                Log.w("getMembers", "Owners list has " + owners.size());
                owners.add("No Owner");
                createFilterSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AppointmentActivity", "loadAppointments:onCancelled", databaseError.toException());


            }
        });


    }

    private void addAppointment() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_appointment, null);
        dialogBuilder.setView(dialogView);
        // ButterKnife.bind(this, dialogView);
        //TODO: replace this with Butterknife
         apptTitle = dialogView.findViewById(R.id.appt_title);
         addButton = dialogView.findViewById(R.id.add_appt_button);
         cancelButton = dialogView.findViewById(R.id.cancel_appt_button);
         apptLocation = dialogView.findViewById(R.id.appt_location);
         apptDate = dialogView.findViewById(R.id.appt_date);
         apptTime = dialogView.findViewById(R.id.appt_time);




        ownersSpinner = dialogView.findViewById(R.id.appt_owner);
        ArrayAdapter<String> ownersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, owners);
        ownersSpinner.setAdapter(ownersAdapter);
        ownersSpinner.setSelection(owners.size() - 1);
        //ToDo: spinner with rotation doesn't allow selection and pick the first member only
        ownersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPosition = i;


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                String AM_PM;
                if (hour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }
                int hour_12_format;

                if (hour > 12) {

                    // If the hour is greater than or equal to 12
                    // Then we subtract 12 from the hour to make it 12 hour format time
                    hour_12_format = hour - 12;
                } else {
                    hour_12_format = hour;
                }


               /*
               String myFormat = "hh:mm a"; // your own format
               SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
               String  formated_time = sdf.format(myCalendar.getTime());
               apptTime.setText(formated_time);
               */
                apptTime.setText(hour_12_format + ":" + minutes + " " + AM_PM);
                //TODO: make time format 00:00 and put string in string.xml


            }
        };
        apptTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(dialogView.getContext(), time,
                        myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                        false).show();
            }
        });


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
                //TODO: add data validation & default values

                Appointment appt = new Appointment();
                appt.setApptTitle(apptTitle.getText().toString());
                appt.setApptLocation(apptLocation.getText().toString());
                appt.setApptTime(apptTime.getText().toString());
                appt.setApptDate(apptDate.getText().toString());
                if (mPosition < owners.size() - 1) {
                    appt.setApptOwner(members.get(mPosition).getName());
                    appt.setOwnerId(members.get(mPosition).getId());
                } else {
                    appt.setApptOwner("No Owner");
                    appt.setOwnerId("0");

                }

                Log.w("AppointmentActivity", "Item selected " + mPosition);
                String key = apptRef.push().getKey();
                appt.setApptId(key);

                apptRef.child(key).setValue(appt);
                Log.w("addApptDialog", "Members list has " + members.size());
                Log.w("addApptDialog", "Owners list has " + owners.size());

                resetMemberSpinner();
                getAllData();

                dialog.dismiss();

            }

        });
        dialog.show();
    }
    //TODO: display error messages as needed
    //TODO: fix the UI and do data validations

    @Override
    public void onPause(){
        super.onPause();
        if(dialog!=null && dialog.isShowing())
            dialogShown=true;
        else
            dialogShown=false;

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //------------ Save UI status------------------------------
        outState.putBoolean(DIALOG_STATUES, dialogShown);
        outState.putInt(FILTER_SELECTION,memberSpinner.getSelectedItemPosition());
        outState.putParcelableArrayList(APPOINTMENTS_LIST, appointmentList);
        outState.putParcelableArrayList(MEMBERS_LIST,members);
        outState.putStringArrayList(OWNERS_LIST, owners);

        //-------------Save Dialog Status------------------------
        if(dialogShown){
            if(!apptTitle.getText().toString().isEmpty())
                outState.putString(APPOINTMENT_TITLE_TEXT, apptTitle.getText().toString());
            if(!apptLocation.getText().toString().isEmpty())
                outState.putString(LOCATION_TEXT, apptLocation.getText().toString());
            if(!apptTime.getText().toString().isEmpty())
                outState.putString(APPT_TIME_TEXT, apptTime.getText().toString());
            if(!apptDate.getText().toString().isEmpty())
                outState.putString(APPT_DATE_TEX, apptDate.getText().toString());



        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);



        if(savedInstanceState.getBoolean(DIALOG_STATUES)){
            addAppointment();
            if(savedInstanceState.containsKey(APPOINTMENT_TITLE_TEXT)){
                String apptTitleText=savedInstanceState.getString(APPOINTMENT_TITLE_TEXT);
                apptTitle.setText(apptTitleText);
            }
            if(savedInstanceState.containsKey(APPT_DATE_TEX)){
                String dateText=savedInstanceState.getString(APPT_DATE_TEX);
                apptDate.setText(dateText);
            }
            if(savedInstanceState.containsKey(APPT_TIME_TEXT)){
                String timeText=savedInstanceState.getString(APPT_TIME_TEXT);
                apptTime.setText(timeText);
            }
            if(savedInstanceState.containsKey(LOCATION_TEXT)){
                String apptLocationText=savedInstanceState.getString(LOCATION_TEXT);
                apptLocation.setText(apptLocationText);
            }

        }
    }


}
