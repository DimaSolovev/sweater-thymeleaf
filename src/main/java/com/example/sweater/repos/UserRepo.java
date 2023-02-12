package com.example.sweater.repos;

import com.example.sweater.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT u FROM  User u JOIN FETCH u.messages WHERE u.id=?1")
    User findUserById(Long id);

    User findByUsername(String username);

    User findByActivationCode(String code);
}
