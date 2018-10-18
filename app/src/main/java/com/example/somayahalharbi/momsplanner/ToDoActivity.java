package com.example.somayahalharbi.momsplanner;

import android.app.DatePickerDialog;
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
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.somayahalharbi.momsplanner.adapters.ToDoAdapter;
import com.example.somayahalharbi.momsplanner.models.Member;
import com.example.somayahalharbi.momsplanner.models.ToDo;
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
            //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }
        toDoRef = database.getReference("users").child(user.getUid()).child(TO_DO_NODE);

        ownersRef = database.getReference("users").child(user.getUid()).child("member");


        addToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
        toDoRecyclerView.setHasFixedSize(true);
        LinearLayoutManager toDoLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        toDoRecyclerView.setLayoutManager(toDoLinearLayoutManager);
        toDoAdapter = new ToDoAdapter(this);
        toDoRecyclerView.setAdapter(toDoAdapter);
        ValueEventListener toDoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toDoList = new ArrayList<>();
                for (DataSnapshot todoSnapshot : dataSnapshot.getChildren()) {
                    ToDo todo = todoSnapshot.getValue(ToDo.class);
                    toDoList.add(todo);
                }
                toDoAdapter.setData(toDoList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ToDoActivity", "loadTodo:onCancelled", databaseError.toException());
                // ...
            }
        };
        toDoRef.addValueEventListener(toDoListener);
        //#########################################################


        //String[] owners = {"Faisal", "Somayah", "Sarah"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getMembers());
        memberSpinner.setAdapter(adapter);
        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: implement this to get owner specific todo (Sort By Owner)


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
                Log.w("FamilyActivity", "loadMember:onCancelled", databaseError.toException());


            }
        });
        return owners;


    }

    private void setDummyData() {
        ToDo todo = new ToDo();
        todo.setToDo("Book Place For Faisal's Birthday Party");
        todo.setDueBy("10/13/2018");
        todo.setChecked(false);
        todo.setPriority(1);
        todo.setOwner("Faisal");
        toDoList.add(todo);


        todo = new ToDo();
        todo.setToDo("Email the teacher to ask about the school's birthday celebration");
        todo.setDueBy("11/10/2018");
        todo.setChecked(false);
        todo.setPriority(3);
        todo.setOwner("Faisal");
        toDoList.add(todo);
        // myRef.setValue(todo);

        todo = new ToDo();
        todo.setToDo("Buy new clothes for fall and winter");
        todo.setDueBy("11/15/2018");
        todo.setChecked(false);
        todo.setPriority(2);
        todo.setOwner("Everyone");
        toDoList.add(todo);
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


        //String[] owners = {"Faisal", "Somayah", "Sarah"};


        final Spinner ownersSpinner = dialogView.findViewById(R.id.todo_owner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getMembers());
        ownersSpinner.setAdapter(adapter);
        ownersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


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
                String myFormat = "MM/dd/yy"; //In which you need put here
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
                todo.setPriority(priority);


                todo.setDueBy(dueBy.getText().toString());
                toDoRef.push().setValue(todo);
                dialog.dismiss();

            }

        });
        dialog.show();
    }


}
