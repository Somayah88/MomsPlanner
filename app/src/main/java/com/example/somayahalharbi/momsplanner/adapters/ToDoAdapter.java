package com.example.somayahalharbi.momsplanner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.somayahalharbi.momsplanner.ToDoActivity.TO_DO_NODE;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoAdapterViewHolder> {
    private ArrayList<ToDo> toDos = new ArrayList<>();
    private Context mContext;
    private static FirebaseDatabase database;
    private DatabaseReference toDoRef;
    private FirebaseUser user;
    private FirebaseAuth mFirebaseAuth;


    public ToDoAdapter(Context context) {
        mContext = context;
    }


    @NonNull
    @Override
    public ToDoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutFromListItem = R.layout.task_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutFromListItem, viewGroup, false);

        return new ToDoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoAdapterViewHolder viewHolder, final int position) {

        viewHolder.toDoTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean isChecked) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                user = mFirebaseAuth.getCurrentUser();
                if (database == null) {
                    database = FirebaseDatabase.getInstance();

                }
                toDoRef = database.getReference("users").child(user.getUid()).child(TO_DO_NODE).child(toDos.get(position).getTaskId()).child("checked");
                toDoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().setValue(isChecked);
                        toDos.get(position).setChecked(isChecked);
                        notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });

            }
        });

        String task = toDos.get(position).getToDo();
        String dueBy = toDos.get(position).getDueBy();
        String owner = toDos.get(position).getOwner();
        boolean checked = toDos.get(position).isChecked();
        int priority = toDos.get(position).getPriority();
        viewHolder.toDoTask.setText(task);
        if (!dueBy.isEmpty())
            viewHolder.taskDueBy.setText(dueBy);
        else
            viewHolder.taskDueBy.setText(mContext.getResources().getString(R.string.due_by_default));

        viewHolder.taskOwner.setText(owner);
        viewHolder.toDoTask.setChecked(checked);
        if (priority == 1) {
            viewHolder.taskPriority.setText(mContext.getResources().getString(R.string.priority_low));
            viewHolder.taskPriority.setTextColor(ContextCompat.getColor(mContext, R.color.low));

        } else if (priority == 2) {
            viewHolder.taskPriority.setText(mContext.getResources().getString(R.string.priority_medium));
            viewHolder.taskPriority.setTextColor(ContextCompat.getColor(mContext, R.color.medium));
        } else if (priority == 3) {
            viewHolder.taskPriority.setText(mContext.getResources().getString(R.string.priority_high));
            viewHolder.taskPriority.setTextColor(ContextCompat.getColor(mContext, R.color.high));
        }
    }

    @Override
    public int getItemCount() {
        return toDos.size();
    }

    public void clear() {
        int size = toDos.size();
        toDos.clear();
        notifyItemRangeChanged(0, size);

    }

    public void setData(ArrayList<ToDo> toDoList) {
        clear();
        toDos = toDoList;
        notifyDataSetChanged();

    }

    public void remove(int position) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();

        }
        ToDo todo = toDos.get(position);
        toDoRef = database.getReference("users").child(user.getUid()).child(TO_DO_NODE).child(todo.getTaskId());
        toDoRef.removeValue();

        notifyItemRemoved(position);
        notifyDataSetChanged();


    }

    public class ToDoAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_title)
        CheckBox toDoTask;
        @BindView(R.id.due_by)
        TextView taskDueBy;
        @BindView(R.id.priority)
        TextView taskPriority;
        @BindView(R.id.owner)
        TextView taskOwner;


        public ToDoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}
