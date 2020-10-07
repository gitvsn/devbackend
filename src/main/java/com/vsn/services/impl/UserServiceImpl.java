package com.vsn.services.impl;

import com.vsn.dto.RegistrationUserDTO;
import com.vsn.entities.registration.*;
import com.vsn.exceptions.RegistrationValidDataException;
import com.vsn.repositories.UserRepository;
import com.vsn.repositories.UserTokenRepository;
import com.vsn.securiry.jwt.JwtTokenProvider;
import com.vsn.services.interfaces.UserInfoService;
import com.vsn.services.interfaces.UserService;
import com.vsn.services.interfaces.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final UserInfoService userInfoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserTokenRepository userTokenRepository;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User register(RegistrationUserDTO userDTO) throws RegistrationValidDataException, IOException {
        User user = saveUser(createUser(userDTO)
                .orElseThrow(() -> new IllegalStateException("It is not possible to create a new user")));

        try {
            walletService.createWallets(user);
        }catch (RuntimeException rollback){
            userRepository.delete(user);
            throw  new IOException("error create wallet");
        }

        userInfoService.saveUserInfo(createDefaultUserInfo(user,userDTO));

        return user;
    }


    private User saveUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public UserToken getTokenByUserEmail(String email){
        User user = findByEmail(email);
        return userTokenRepository.findByUserId(user.getId());
    }


    private Optional<User> createUser(RegistrationUserDTO userDTO) throws RegistrationValidDataException {
        validRegistrationUserDTO(userDTO);
        String encodedPass = passwordEncoder.encode(userDTO.getPassword());
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodedPass);
        user.setUserRole(UserRole.ROLE_USER);
        user.setCreated(new Date().getTime());
        user.setUserStatus(UserStatus.NOT_VERIFIED);
        return Optional.of(user);
    }

    private void validRegistrationUserDTO(RegistrationUserDTO userDTO) throws RegistrationValidDataException {
        if (userRepository.findUserByEmail(userDTO.getEmail()).isPresent()) {
            throw new RegistrationValidDataException("Email is un ready in use");
        }
        if (!passwordInputIsValid(userDTO.getPassword())) {
            throw new RegistrationValidDataException("Invalid password");
        }
        if (!emailInputIsValid(userDTO.getEmail())) {
            throw new RegistrationValidDataException("Invalid email");
        }
    }

    @Override
    public void changePassword(User user, String password) throws RegistrationValidDataException {
        if (!passwordInputIsValid(password)) {
            throw new RegistrationValidDataException("Invalid password");
        }

        String encodedPass = passwordEncoder.encode(password);
        user.setPassword(encodedPass);

        saveUser(user);
    }

    @Override
    public void changePassword(@NotNull User user, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            return;
        }

        String old = passwordEncoder.encode(oldPassword);
        if (!user.getPassword().equals(old) || !passwordInputIsValid(newPassword)) {
            throw new RuntimeException("Wrong password");
        }

        String newPass = passwordEncoder.encode(newPassword);
        user.setPassword(newPass);
        saveUser(user);
    }

    @Override
    public void saveUserToken(User user, String token) {
        UserToken userToken = getTokenByUserEmail(user.getEmail());

        if(getTokenByUserEmail(user.getEmail()) == null){
             userToken = new UserToken();
             userToken.setUserId(user.getId());
        }
        userToken.setToken(token);
        userTokenRepository.save(userToken);
    }

    @Override
    public boolean changeEmail(User user, String email) {
        if (!emailInputIsValid(email)) {
            return false;
        }
        user.setEmail(email);
        saveUser(user);
        return true;
    }

    private boolean passwordInputIsValid(String password) {
        // String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,}";

        //return (password.matches(pattern));
        if (password.length() > 4)
            return true;
        else return false;
    }

    private boolean emailInputIsValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }


    private UserInfo createDefaultUserInfo(User user,RegistrationUserDTO userDTO) {
        String secret = GoogleTwoFAService.generateSecretKey();

        UserInfo userInfo = new UserInfo();
        userInfo.setCountry("");
        userInfo.setEmail(user.getEmail());
        userInfo.setName(userDTO.getName());
        userInfo.setPhone("");
        userInfo.setTwoFaEnable(false);
        userInfo.setSurname(userDTO.getSurname());
        userInfo.setUserId(user.getId());
        userInfo.setSecret(secret);
        userInfo.setTwoFaLink(GoogleTwoFAService.getGoogleAuthenticatorBarCode(secret, "sincere", user.getEmail()));


        return userInfo;
    }
}
