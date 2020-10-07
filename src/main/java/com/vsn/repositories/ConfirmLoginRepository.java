package com.vsn.repositories;

import com.vsn.entities.confirm.ConfirmLogin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmLoginRepository extends CrudRepository<ConfirmLogin, Long> {
     Optional<ConfirmLogin> getByUserId(Long userId);
     ConfirmLogin getByCode(String code);
}
