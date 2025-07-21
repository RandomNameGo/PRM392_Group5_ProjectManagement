package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface UserActionListener {
        void onEdit(User user, String uid);
        void onDelete(String uid);
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private OnUserClickListener clickListener;
    private List<User> userList;
    private UserActionListener listener;

    public UserAdapter(List<User> list, UserActionListener listener, OnUserClickListener clickListener) {
        this.userList = list;
        this.listener = listener;
        this.clickListener = clickListener;
    }

    public void setUserList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User u = userList.get(position);
        holder.txtName.setText(u.name);
        holder.txtEmail.setText(u.email + " (" + u.role + ")");

        String uid = u.uid;
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(u, uid));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(uid));

        holder.itemView.setOnClickListener(v -> clickListener.onUserClick(u));

        //Animations
        Animation slideIn = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_right);
        slideIn.setStartOffset(300);
        holder.actionsLayout.startAnimation(slideIn);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail;
        Button btnEdit, btnDelete;
        LinearLayout actionsLayout;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textName);
            txtEmail = itemView.findViewById(R.id.textEmail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            actionsLayout = itemView.findViewById(R.id.actionsBtn);
        }
    }
}
