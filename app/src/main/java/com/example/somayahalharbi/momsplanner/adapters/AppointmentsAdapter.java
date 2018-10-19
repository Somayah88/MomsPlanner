package com.example.somayahalharbi.momsplanner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.somayahalharbi.momsplanner.AppointmentsActivity.APPOINTMENT_PATH;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsAdapterViewHolder> {
    private ArrayList<Appointment> appointments = new ArrayList<>();
    private static FirebaseDatabase database;



    @NonNull
    @Override
    public AppointmentsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutFromListItem = R.layout.appointment_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutFromListItem, viewGroup, false);

        return new AppointmentsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsAdapterViewHolder viewHolder, int position) {
        String title = appointments.get(position).getApptTitle();
        String location = appointments.get(position).getApptLocation();
        String owner = appointments.get(position).getApptOwner();
        String time = appointments.get(position).getApptTime();
        String date = appointments.get(position).getApptDate();
        viewHolder.apptTitle.setText(title);
        viewHolder.apptLocation.setText(location);
        viewHolder.apptOwner.setText(owner);
        viewHolder.apptDate.setText(date);
        viewHolder.apptTime.setText(time);

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void setData(ArrayList<Appointment> appointmentList) {
        clear();
        appointments = appointmentList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = appointments.size();
        appointments.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void remove(int position) {

        DatabaseReference apptRef;
        FirebaseUser user;
        FirebaseAuth mFirebaseAuth;
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }
        Appointment appt = appointments.get(position);
        apptRef = database.getReference("users").child(user.getUid()).child(APPOINTMENT_PATH).child(appt.getApptId());
        apptRef.removeValue();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, appointments.size());


    }

    public class AppointmentsAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.appt_title)
        TextView apptTitle;
        @BindView(R.id.appt_location)
        TextView apptLocation;
        @BindView(R.id.appt_date)
        TextView apptDate;
        @BindView(R.id.appt_time)
        TextView apptTime;
        @BindView(R.id.appt_owner)
        TextView apptOwner;


        public AppointmentsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
