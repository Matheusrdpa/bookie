package com.estudo.bookie.factory;

import com.estudo.bookie.entities.User;

import java.util.HashSet;
import java.util.Set;

public class UserFactory {
   public static User createUser() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        User user = new User(1L, "Test", "teste@email.com", "123456", roles);
        return user;
    }
}
