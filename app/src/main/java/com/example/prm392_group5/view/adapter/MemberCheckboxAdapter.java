package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.User;

import java.util.ArrayList;
import java.util.List;

public class MemberCheckboxAdapter extends RecyclerView.Adapter<MemberCheckboxAdapter.MemberViewHolder> {

    private List<User> memberList;
    private List<String> selectedMemberIds;

    public MemberCheckboxAdapter(List<User> memberList) {
        this.memberList = memberList;
        this.selectedMemberIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_checkbox, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User member = memberList.get(position);
        holder.textViewMemberName.setText(member.name + " (" + member.email + ")");
        
        // Set checkbox state without triggering listener
        holder.checkboxMember.setOnCheckedChangeListener(null);
        holder.checkboxMember.setChecked(selectedMemberIds.contains(member.uid));
        
        // Set checkbox listener
        holder.checkboxMember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedMemberIds.contains(member.uid)) {
                    selectedMemberIds.add(member.uid);
                }
            } else {
                selectedMemberIds.remove(member.uid);
            }
        });
        
        // Also handle click on the entire item
        holder.itemView.setOnClickListener(v -> {
            holder.checkboxMember.setChecked(!holder.checkboxMember.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public List<String> getSelectedMemberIds() {
        return new ArrayList<>(selectedMemberIds);
    }

    public void clearSelections() {
        selectedMemberIds.clear();
        notifyDataSetChanged();
    }

    public void setMemberSelected(String memberId, boolean selected) {
        if (selected) {
            if (!selectedMemberIds.contains(memberId)) {
                selectedMemberIds.add(memberId);
            }
        } else {
            selectedMemberIds.remove(memberId);
        }
        notifyDataSetChanged();
    }

    public void setSelectedMembers(List<String> memberIds) {
        selectedMemberIds.clear();
        if (memberIds != null) {
            selectedMemberIds.addAll(memberIds);
        }
        notifyDataSetChanged();
    }

    public void updateMembers(List<User> newMemberList) {
        this.memberList.clear();
        this.memberList.addAll(newMemberList);
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxMember;
        TextView textViewMemberName;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxMember = itemView.findViewById(R.id.checkboxMember);
            textViewMemberName = itemView.findViewById(R.id.textViewMemberName);
        }
    }
}