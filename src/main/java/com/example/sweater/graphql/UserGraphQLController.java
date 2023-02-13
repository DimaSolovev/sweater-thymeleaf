package com.example.sweater.graphql;

import com.example.sweater.domain.User;
import com.example.sweater.domain.input.UserInput;
import com.example.sweater.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

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
        return userRepo.findUserById(id);
    }

    @MutationMapping
    User addUser(@Argument UserInput userInput) {
        return userRepo.save(new User())
    }

    @MutationMapping
    Boolean deleteUser(@Argument Long id) {
        userRepo.deleteById(id);
        return true;
    }
}
