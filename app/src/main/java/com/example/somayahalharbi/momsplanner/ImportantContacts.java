package com.example.somayahalharbi.momsplanner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ImportantContacts extends AppCompatActivity {
    private Button showMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_contacts);
        showMap=findViewById(R.id.show_map);
        final Context context=this;

        showMap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //TODO: Create map intent to display address

            }
        });
    }
}
