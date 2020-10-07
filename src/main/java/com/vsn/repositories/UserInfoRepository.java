package com.vsn.repositories;

import com.vsn.entities.registration.UserInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {
    Optional<UserInfo> getByUserId(Long userId);
}
