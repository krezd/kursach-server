package com.example.kursachserver.dto.response;

import com.example.kursachserver.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class UserResponseGenerator {
    private List<UserResponse> users = new ArrayList<>();

    public UserResponseGenerator(List<User> userList){
        for(User user: userList){
            this.users.add(new UserResponse(user));
        }
    }
}
