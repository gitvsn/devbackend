package com.vsn.services.interfaces;

import com.vsn.entities.confirm.ConfirmLogin;
import com.vsn.entities.registration.User;

public interface ConfirmLoginService {
    ConfirmLogin getByUser(User user);

    Boolean checkConfirmCode(User user,String code);
    ConfirmLogin createConfirmLogin(User user);
    ConfirmLogin createConfirmRestorePassword(User user);

    Long getUserIdByCode(String code);
    void deleteCode(ConfirmLogin code);
}
