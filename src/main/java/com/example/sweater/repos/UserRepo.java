package com.example.sweater.repos;

import com.example.sweater.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    List<User> findAll();

    User findUserById(Long id);

    User findByUsername(String username);

    User findByActivationCode(String code);
}
