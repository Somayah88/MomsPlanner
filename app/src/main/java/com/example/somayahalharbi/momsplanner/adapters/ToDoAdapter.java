package com.example.somayahalharbi.momsplanner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.models.ToDo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoAdapterViewHolder> {
    private ArrayList<ToDo> toDos = new ArrayList<ToDo>();
    private Context mContext;

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
    public void onBindViewHolder(@NonNull ToDoAdapterViewHolder viewHolder, int position) {
        String task = toDos.get(position).getToDo();
        String dueBy = toDos.get(position).getDueBy();
        String owner = toDos.get(position).getOwner();
        boolean checked = toDos.get(position).isChecked();
        int priority = toDos.get(position).getPriority();
        viewHolder.toDoTask.setText(task);
        viewHolder.taskDueBy.setText(dueBy);
        viewHolder.taskOwner.setText(owner);
        viewHolder.toDoTask.setChecked(checked);
        if (priority == 1) {
            viewHolder.taskPriority.setText("High");
            viewHolder.taskPriority.setTextColor(ContextCompat.getColor(mContext, R.color.high));

        } else if (priority == 2) {
            viewHolder.taskPriority.setText("Medium");
            viewHolder.taskPriority.setTextColor(ContextCompat.getColor(mContext, R.color.medium));
        } else {
            viewHolder.taskPriority.setText("Low");
            viewHolder.taskPriority.setTextColor(ContextCompat.getColor(mContext, R.color.low));
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
