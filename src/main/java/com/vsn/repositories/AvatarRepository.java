package com.vsn.repositories;

import com.vsn.entities.registration.Avatar;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends CrudRepository<Avatar, Long> {
    Optional<Avatar>getAvatarByUserId(Long userId);
    List<Avatar> findAll();
}
