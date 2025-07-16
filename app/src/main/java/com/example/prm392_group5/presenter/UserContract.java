package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.User;

import java.util.List;

public interface UserContract {
    interface View {
        void onUserCreated();
        void onUserLoaded(User user);
        void onUserUpdated();
        void onUserDeleted();
        void onError(String error);
        void onUserListLoaded(List<User> userList);

    }

    interface Presenter {
        void createUser(String uid, User user);
        void getUser(String uid);
        void updateUser(String uid, User user);
        void deleteUser(String uid);
        void getAllUsers();

    }
}
