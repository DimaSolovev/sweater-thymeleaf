package com.example.sweater.graphql;

import com.example.sweater.domain.User;
import com.example.sweater.domain.input.UserInput;
import com.example.sweater.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserGraphQLController {

    private final UserRepo userRepo;
    @QueryMapping
    Iterable<User> users() {
        return userRepo.findAll();
    }

    @QueryMapping
    User userById(@Argument Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("data not found"));
    }

    @MutationMapping
    public User addUser(@Argument UserInput userInput) {
        log.info("Save user with email {}", userInput.getEmail());
        User user = new User();
        user.setActive(userInput.getActive());
        user.setEmail(userInput.getEmail());
        user.setRoles(userInput.getRoles());
        user.setPassword(userInput.getPassword());
        user.setUsername(userInput.getUsername());
        return userRepo.save(user);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        log.info("Delete user with id {}", id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id" + id));
        userRepo.delete(user);
        return true;
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UserInput userInput) {
        log.info("Update user with id {}", id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        user.setUsername(userInput.getUsername());
        user.setPassword(userInput.getPassword());
        user.setEmail(userInput.getEmail());
        return userRepo.save(user);
    }
}

