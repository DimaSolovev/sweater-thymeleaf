package com.example.sweater.graphql.resolver.mutation;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.domain.input.MessageInput;
import com.example.sweater.domain.input.UserInput;
import com.example.sweater.repos.UserRepo;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserMutationResolver implements GraphQLMutationResolver {

    private final UserRepo userRepo;

    public User addUser(UserInput userInput) {
        log.info("Save user with email {}", userInput.getEmail());
        User user = new User();
        user.setActive(userInput.getActive());
        user.setEmail(userInput.getEmail());
        user.setRoles(userInput.getRoles());
        user.setPassword(userInput.getPassword());
        user.setUsername(userInput.getUsername());
        return userRepo.save(user);
    }

    public Boolean deleteUser(Long id) {
        log.info("Delete user with id {}", id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id" + id));
        userRepo.delete(user);
        return true;
    }

    public User updateUser(Long id, UserInput userInput) {
        log.info("Update user with id {}", id);
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id" + id));
        user.setUsername(userInput.getUsername());
        user.setPassword(userInput.getPassword());
        user.setEmail(userInput.getEmail());
        return userRepo.save(user);
    }
}
