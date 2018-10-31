package com.example.somayahalharbi.momsplanner;

import android.app.DatePickerDialog;
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
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.somayahalharbi.momsplanner.adapters.ToDoAdapter;
import com.example.somayahalharbi.momsplanner.helpers.WidgetUpdateHelper;
import com.example.somayahalharbi.momsplanner.models.Member;
import com.example.somayahalharbi.momsplanner.models.ToDo;
import com.example.somayahalharbi.momsplanner.widget.MomsPlannerWidgetProvider;
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

public class ToDoActivity extends AppCompatActivity {
    @BindView(R.id.to_do_fab)
    FloatingActionButton addToDo;
    public static final String TO_DO_NODE = "todo";
    private static FirebaseDatabase database;
    @BindView(R.id.to_do_recyclerView)
    RecyclerView toDoRecyclerView;
    @BindView(R.id.member_spinner)
    Spinner memberSpinner;
    DatabaseReference toDoRef;
    DatabaseReference ownersRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    private ToDoAdapter toDoAdapter;
    // Firebase database
    private ArrayList<ToDo> toDoList = new ArrayList<>();
    ArrayList<Member> members;
    ArrayList<String> owners;
    private int mPosition;
    private ArrayList<String> spinnerMembers;
    private int filterSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        ButterKnife.bind(this);
        //Firebase DB
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        toDoRef = database.getReference("users").child(user.getUid()).child(TO_DO_NODE);
        getMembers();


        addToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
        getAllData();

        LinearLayoutManager toDoLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        toDoRecyclerView.setLayoutManager(toDoLinearLayoutManager);
        toDoAdapter = new ToDoAdapter(this);
        toDoRecyclerView.setAdapter(toDoAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    toDoAdapter.remove(position);
                   WidgetUpdateHelper.updateWidgetData(ToDoActivity.this);

                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(toDoRecyclerView);

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
        memberSpinner.setSelection(spinnerMembers.size() - 1);


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

    private void getData(int position) {
        if (position < owners.size() - 1)
            getFilteredData(position);

        else
            getAllData();
    }

    private void getFilteredData(int position) {

        Log.w("getFilkteredData", "Members list has " + members.size());
        Log.w("getFilteredDatas", "Owners list has " + owners.size());

        Query queryRef = toDoRef.orderByChild("ownerId").equalTo(members.get(position).getId());
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toDoList = new ArrayList<>();
                for (DataSnapshot tasksSnapshot : dataSnapshot.getChildren()) {
                    ToDo todo = tasksSnapshot.getValue(ToDo.class);
                    toDoList.add(todo);

                }

                toDoAdapter.setData(toDoList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.w("GetFilteredData", "Get Filtered data was just executed");


    }

    private void getAllData() {

        toDoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toDoList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    ToDo todo = taskSnapshot.getValue(ToDo.class);
                    toDoList.add(todo);
                }
                Log.w("GetAllData", "Get All data was just executed and selected position is " + filterSelection);

                toDoAdapter.setData(toDoList);
                WidgetUpdateHelper.updateWidgetData(ToDoActivity.this);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AppointmentActivity", "loadAppointments:onCancelled", databaseError.toException());


            }
        });
    }



    private void addTask() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_todo, null);
        dialogBuilder.setView(dialogView);
        // ButterKnife.bind(this, dialogView);
        //TODO: replace this with Butterknife
        final EditText task = dialogView.findViewById(R.id.task);
        final Button addButton = dialogView.findViewById(R.id.add_to_do);
        final Button cancelButton = dialogView.findViewById(R.id.cancel_btn);
        final EditText dueBy = dialogView.findViewById(R.id.to_do_due_by);
        final RadioGroup priorityRadioGroup = dialogView.findViewById(R.id.task_priority);

        final Spinner ownersSpinner = dialogView.findViewById(R.id.todo_owner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, owners);
        ownersSpinner.setAdapter(adapter);
        ownersSpinner.setSelection(owners.size() - 1);
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
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dueBy.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dueBy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(dialogView.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                ToDo todo = new ToDo();

                todo.setToDo(task.getText().toString());
                todo.setChecked(false);
                int priorityId = priorityRadioGroup.getCheckedRadioButtonId();
                int priority = 0;
                if (priorityId == -1)
                    priority = 0;
                if (priorityId == R.id.high_priority)
                    priority = 3;
                if (priorityId == R.id.medium_priority)
                    priority = 2;
                if (priorityId == R.id.low_priority)
                    priority = 1;
                Log.d("Priority", Integer.toString(priority));
                todo.setPriority(priority);
                todo.setDueBy(dueBy.getText().toString());
                if (mPosition < owners.size() - 1) {
                    todo.setOwner(members.get(mPosition).getName());
                    todo.setOwnerId(members.get(mPosition).getId());
                } else {
                    todo.setOwner("No Owner");
                    todo.setOwnerId("0");

                }
                String key = toDoRef.push().getKey();
                todo.setTaskId(key);
                toDoRef.child(key).setValue(todo);
                resetMemberSpinner();
                getAllData();

                dialog.dismiss();

            }

        });
        dialog.show();
    }

   /* public void updateWidgetData() {
        Log.w("to do activity", "update widget data is just called");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MomsPlannerWidgetProvider.sendRefreshBroadcast(ToDoActivity.this);
            }
        });
    }*/

}
//TODO: display error messages as needed
//TODO: fix the UI and add data validations
