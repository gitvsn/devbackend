package com.vsn.services.interfaces;

import com.vsn.dto.RegistrationUserDTO;
import com.vsn.entities.registration.User;
import com.vsn.entities.registration.UserToken;
import com.vsn.exceptions.RegistrationValidDataException;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findByEmail(String email);
    User findById(Long id);

    User register(RegistrationUserDTO userDTO) throws RegistrationValidDataException, IOException;

    void changePassword(User user,String password) throws RegistrationValidDataException;
    boolean changeEmail(User user,String email);

     UserToken getTokenByUserEmail(String email);

    void changePassword(User user,String oldPassword,String newPassword);

    void saveUserToken(User user, String token);
}
