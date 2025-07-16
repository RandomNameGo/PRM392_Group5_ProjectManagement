package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.FirebaseRepository;
import com.example.prm392_group5.models.User;

import java.util.List;

public class UserPresenter implements UserContract.Presenter{
    private final UserContract.View view;
    private final FirebaseRepository repository;

    public UserPresenter(UserContract.View view) {
        this.view = view;
        this.repository = new FirebaseRepository();
    }

    @Override
    public void createUser(String uid, User user) {
        repository.createUser(uid, user, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onUserCreated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getUser(String uid) {
        repository.getUser(uid, new FirebaseRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                view.onUserLoaded(user);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void updateUser(String uid, User user) {
        repository.updateUser(uid, user, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onUserUpdated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void deleteUser(String uid) {
        repository.deleteUser(uid, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onUserDeleted();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getAllUsers() {
        repository.getAllUsers(new FirebaseRepository.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                view.onUserListLoaded(users);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }
}
