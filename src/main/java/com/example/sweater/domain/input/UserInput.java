package com.example.sweater.domain.input;

import com.example.sweater.domain.Role;
import lombok.Data;

import java.util.Set;

@Data//сущность для input в graphql
public class UserInput {

    private Boolean active;

    private String email;

    private String password;

    private String username;

    private Set<Role> roles;
}
