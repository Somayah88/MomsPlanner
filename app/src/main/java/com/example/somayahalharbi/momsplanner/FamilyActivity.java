package com.example.somayahalharbi.momsplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FamilyActivity extends AppCompatActivity {
    @BindView(R.id.add_member_fab)
    FloatingActionButton addMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        ButterKnife.bind(this);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMembers();
            }
        });
    }

    private void addMembers(){
        final AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_member, null);
        dialogBuilder.setView(dialogView);
        // ButterKnife.bind(this, dialogView);
        //TODO: replace this with Butterknife
        final EditText name = dialogView.findViewById(R.id.member_name);
        final Button addButton = dialogView.findViewById(R.id.add_member_button);
        final Button cancelButton = dialogView.findViewById(R.id.cancel_member_button);
        final EditText birthday = dialogView.findViewById(R.id.member_dob);

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
                //TODO: add member functionality goes here
            }

        });
        dialog.show();
    }

}
