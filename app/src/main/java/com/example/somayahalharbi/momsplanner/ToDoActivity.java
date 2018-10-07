package com.example.somayahalharbi.momsplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ToDoActivity extends AppCompatActivity {
    @BindView(R.id.to_do_fab)
    FloatingActionButton addToDo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        ButterKnife.bind(this);
        addToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
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
        //TODO: replace dummy data
        String[] owners = {"Faisal", "Somayah", "Sarah"};


        final Spinner ownersSpinner = dialogView.findViewById(R.id.owner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, owners);
        ownersSpinner.setAdapter(adapter);
        ownersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: implement this

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
                //TODO: add to do task functionality goes here
            }

        });
        dialog.show();
    }


}
