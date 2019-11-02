package com.tericcabrel.osiris.services.interfaces;

import com.tericcabrel.osiris.dtos.UserRegistrationDto;
import com.tericcabrel.osiris.models.User;

public interface UserService {

    User findByUid(String uid);

    User save(UserRegistrationDto userRegistrationDto);
}
