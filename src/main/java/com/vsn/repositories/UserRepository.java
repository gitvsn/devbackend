package com.vsn.repositories;

import com.vsn.entities.registration.User;
import org.springframework.data.repository.CrudRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {
    List<User> findAll();
    Optional<User> findUserByEmail(String email);
    Optional<User> findById(long id);

}
