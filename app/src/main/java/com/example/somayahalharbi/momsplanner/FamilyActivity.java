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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.somayahalharbi.momsplanner.adapters.MembersAdapter;
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

public class FamilyActivity extends AppCompatActivity {
    @BindView(R.id.add_member_fab)
    FloatingActionButton addMember;
    @BindView(R.id.family_member_recyclerView)
    RecyclerView familyMembersRecyclerView;
    public static final String MEMBER_NODE = "member";
    public static final String USERS_NODE = "users";
    private static FirebaseDatabase database;
    private DatabaseReference familyMemberRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    private MembersAdapter familyMembersAdapter;
    private ArrayList<Member> membersList;
    private AlertDialog dialog;
    private boolean dialogShown;
    //---------- Save Dialog State-------------
    private static final String DIALOG_STATUES="dialog_status";
    private static final String NAME_EDIT_TEXT="name_text";
    private static final String BIRTHDAY_TEXT="birthday_text";
    //--------------- Save UI state-------------------------
    private static final String MEMBERS_LIST="members_list";

     EditText name;
     EditText birthday;
     Button addButton;
     Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        ButterKnife.bind(this);
        //#######################################################
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();

        }
        familyMemberRef = database.getReference(USERS_NODE).child(user.getUid()).child(MEMBER_NODE);
        //#########################################################
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMembers();
            }
        });


        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        familyMembersRecyclerView.setLayoutManager(membersLayoutManager);
        familyMembersAdapter = new MembersAdapter();
        familyMembersRecyclerView.setAdapter(familyMembersAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    familyMembersAdapter.removeMember(position);
                    getMembers();

                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(familyMembersRecyclerView);

        if(savedInstanceState!=null && savedInstanceState.containsKey(MEMBERS_LIST))
        {
            membersList=savedInstanceState.getParcelableArrayList(MEMBERS_LIST);
            familyMembersAdapter.setData(membersList);
        }
        else
            getMembers();


    }

    private void getMembers() {
        familyMemberRef.addValueEventListener(new ValueEventListener() {
            @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            membersList = new ArrayList<>();
            for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                Member member = memberSnapshot.getValue(Member.class);
                membersList.add(member);
            }
            familyMembersAdapter.setData(membersList);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
        }
    });

}


    private void addMembers(){
        final AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_member, null);
        dialogBuilder.setView(dialogView);
       name = dialogView.findViewById(R.id.member_name);
       addButton  = dialogView.findViewById(R.id.add_member_button);
       cancelButton = dialogView.findViewById(R.id.cancel_member_button);
       birthday = dialogView.findViewById(R.id.member_dob);

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

                birthday.setText(sdf.format(myCalendar.getTime()));
            }

        };

        birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(dialogView.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
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
                Member member = new Member();
                if (name.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.member_name_empty), Toast.LENGTH_SHORT).show();

                } else {
                    member.setName(name.getText().toString());
                    member.setDOB(birthday.getText().toString());
                    String key = familyMemberRef.push().getKey();
                    member.setId(key);
                    familyMemberRef.child(key).setValue(member);
                    getMembers();
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
        outState.putParcelableArrayList(MEMBERS_LIST,membersList);
        outState.putBoolean(DIALOG_STATUES, dialogShown);
        if(dialogShown){
            if(!name.getText().toString().isEmpty())
                outState.putString(NAME_EDIT_TEXT, name.getText().toString());
            if(!birthday.getText().toString().isEmpty())
                outState.putString(BIRTHDAY_TEXT, birthday.getText().toString());

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(DIALOG_STATUES)){
            addMembers();
            if(savedInstanceState.containsKey(NAME_EDIT_TEXT)){
                String nameText=savedInstanceState.getString(NAME_EDIT_TEXT);
                name.setText(nameText);
            }
            if(savedInstanceState.containsKey(BIRTHDAY_TEXT)){
                String dobText=savedInstanceState.getString(BIRTHDAY_TEXT);
                birthday.setText(dobText);
            }

        }
    }

}
