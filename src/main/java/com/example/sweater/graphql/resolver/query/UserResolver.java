package com.example.sweater.graphql.resolver.query;

import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserResolver implements GraphQLQueryResolver {
    //обрабатывает запросы graphql на получение user
    private final UserRepo userRepo;

    public List<User> users() {
        return userRepo.findAll();
    }

    public User userById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new RuntimeException("data not found"));
    }
}
