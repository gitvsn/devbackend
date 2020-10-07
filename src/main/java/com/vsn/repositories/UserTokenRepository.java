package com.vsn.repositories;

import com.vsn.entities.registration.UserToken;
import org.springframework.data.repository.CrudRepository;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {
    UserToken findByUserId(Long userId);
    UserToken findByToken(String code);
}
