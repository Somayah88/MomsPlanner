package com.example.somayahalharbi.momsplanner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.models.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.somayahalharbi.momsplanner.FamilyActivity.MEMBER;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberAdapterViewHolder> {
    private ArrayList<Member> members = new ArrayList<>();
    private static FirebaseDatabase database;



    @NonNull
    @Override
    public MemberAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutFromListItem = R.layout.family_member_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutFromListItem, viewGroup, false);
        return new MemberAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapterViewHolder viewHolder, int position) {
        String name = members.get(position).getName();
        String dob = members.get(position).getDOB();
        viewHolder.memeberName.setText(name);
        viewHolder.memberDOB.setText(dob);

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void clear() {
        int size = members.size();
        members.clear();
        notifyItemRangeChanged(0, size);
    }

    public void setData(ArrayList<Member> memberList) {
        clear();
        members = memberList;
        notifyDataSetChanged();
    }

    public class MemberAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.member_name)
        TextView memeberName;
        @BindView(R.id.member_dob)
        TextView memberDOB;


        public MemberAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void removeMember(int position) {
        DatabaseReference membersRef;
        FirebaseUser user;
        FirebaseAuth mFirebaseAuth;
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();

        }
        Member member = members.get(position);
        membersRef = database.getReference("users").child(user.getUid()).child(MEMBER).child(member.getId());
        membersRef.removeValue();
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

}
