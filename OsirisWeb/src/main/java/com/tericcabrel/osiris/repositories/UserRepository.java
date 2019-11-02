package com.tericcabrel.osiris.repositories;

import com.tericcabrel.osiris.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUid(String uid);

    User findByName(String name);

    List<User> findAllByOrderByCreatedAtDesc();
}
