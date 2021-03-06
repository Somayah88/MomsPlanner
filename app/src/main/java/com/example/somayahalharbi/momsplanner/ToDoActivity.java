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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.somayahalharbi.momsplanner.adapters.ToDoAdapter;
import com.example.somayahalharbi.momsplanner.helpers.WidgetUpdateHelper;
import com.example.somayahalharbi.momsplanner.models.Member;
import com.example.somayahalharbi.momsplanner.models.ToDo;
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
    //----------------- DB keys------------
    public static final String TO_DO_NODE = "todo";
    //-------------- Dialog save state-----------------
    private static final String DIALOG_STATUES = "dialog_status";
    private static final String TASK_EDIT_TEXT = "task_text";
    private static final String DUE_BY_TEXT = "due_by_text";
    //----------------- UI save state-----------
    private static final String FILTER_SELECTION = "filter_selection";
    private static final String TODO_LIST = "to_do_list";
    private static final String MEMBERS_LIST = "members_list";
    private static final String OWNERS_LIST = "owners_list";
    private static final String MEMBERS_NODE = "member";
    private static final String USER_NODE = "users";
    private static FirebaseDatabase database;
    //---------------------------------------
    @BindView(R.id.to_do_fab)
    FloatingActionButton addToDo;
    @BindView(R.id.to_do_recyclerView)
    RecyclerView toDoRecyclerView;
    @BindView(R.id.member_spinner)
    Spinner memberSpinner;
    DatabaseReference toDoRef;
    DatabaseReference ownersRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    ArrayList<Member> members;
    ArrayList<String> owners;
    //-------- Dialog Content---------
    EditText taskEditText;
    Button addButton;
    Button cancelButton;
    EditText dueByEditText;
    RadioGroup priorityRadioGroup;
    private ToDoAdapter toDoAdapter;
    // Firebase database
    private ArrayList<ToDo> toDoList = new ArrayList<>();
    private int mPosition;
    private ArrayList<String> spinnerMembers;
    private int filterSelection = -1;
    private boolean dialogShown;
    private AlertDialog dialog;

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
        toDoRef = database.getReference(USER_NODE).child(user.getUid()).child(TO_DO_NODE);


        addToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

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
        if (savedInstanceState != null && savedInstanceState.containsKey(TODO_LIST)) {
            filterSelection = savedInstanceState.getInt(FILTER_SELECTION);
            toDoList = savedInstanceState.getParcelableArrayList(TODO_LIST);
            members = savedInstanceState.getParcelableArrayList(MEMBERS_LIST);
            owners = savedInstanceState.getStringArrayList(OWNERS_LIST);
            toDoAdapter.setData(toDoList);
            createFilterSpinner();

        } else {

            getMembers();
            getAllData();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //------------ Save UI state------------------------------
        outState.putInt(FILTER_SELECTION, memberSpinner.getSelectedItemPosition());
        outState.putParcelableArrayList(TODO_LIST, toDoList);
        outState.putParcelableArrayList(MEMBERS_LIST, members);
        outState.putStringArrayList(OWNERS_LIST, owners);

        //-------------Save Dialog State------------------------
        outState.putBoolean(DIALOG_STATUES, dialogShown);
        if (dialogShown) {
            if (!taskEditText.getText().toString().isEmpty())
                outState.putString(TASK_EDIT_TEXT, taskEditText.getText().toString());
            if (!dueByEditText.getText().toString().isEmpty())
                outState.putString(DUE_BY_TEXT, dueByEditText.getText().toString());

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(DIALOG_STATUES)) {
            addTask();
            if (savedInstanceState.containsKey(TASK_EDIT_TEXT)) {
                String taskText = savedInstanceState.getString(TASK_EDIT_TEXT);
                taskEditText.setText(taskText);
            }
            if (savedInstanceState.containsKey(DUE_BY_TEXT)) {
                String dueByText = savedInstanceState.getString(DUE_BY_TEXT);

                dueByEditText.setText(dueByText);
            }

        }
    }

    private void getMembers() {

        ownersRef = database.getReference(USER_NODE).child(user.getUid()).child(MEMBERS_NODE);


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
                owners.add("No Owner");
                createFilterSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ToDoActivity", "loadToDos:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void createFilterSpinner() {
        spinnerMembers = new ArrayList<>();
        spinnerMembers.addAll(owners);
        spinnerMembers.remove(owners.size() - 1);
        spinnerMembers.add("All");


        ArrayAdapter<String> membersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerMembers);
        membersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(membersAdapter);
        if (filterSelection == -1)
            memberSpinner.setSelection(spinnerMembers.size() - 1);
        else
            memberSpinner.setSelection(filterSelection);

        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();


            }
        });


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

                toDoAdapter.setData(toDoList);
                WidgetUpdateHelper.updateWidgetData(ToDoActivity.this);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ToDoActivity", "loadToDos:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();


            }
        });
    }


    private void addTask() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_todo, null);
        dialogBuilder.setView(dialogView);
        taskEditText = dialogView.findViewById(R.id.task);
        addButton = dialogView.findViewById(R.id.add_to_do);
        cancelButton = dialogView.findViewById(R.id.cancel_btn);
        dueByEditText = dialogView.findViewById(R.id.to_do_due_by);
        priorityRadioGroup = dialogView.findViewById(R.id.task_priority);

        final Spinner ownersSpinner = dialogView.findViewById(R.id.todo_owner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, owners);
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

                dueByEditText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dueByEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(dialogView.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                ToDo todo = new ToDo();
                if (taskEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.task_title_empty), Toast.LENGTH_SHORT).show();
                } else {
                    todo.setToDo(taskEditText.getText().toString());
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
                    if (dueByEditText.getText().toString().isEmpty())
                        todo.setDueBy(getResources().getString(R.string.due_by_default));
                    else
                        todo.setDueBy(dueByEditText.getText().toString());
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
            }

        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        dialogShown = dialog != null && dialog.isShowing();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
