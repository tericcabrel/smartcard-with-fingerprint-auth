package com.tericcabrel.osiris.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.tericcabrel.osiris.dtos.UserRegistrationDto;
import com.tericcabrel.osiris.models.User;
import com.tericcabrel.osiris.repositories.UserRepository;
import com.tericcabrel.osiris.services.interfaces.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public User save(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setUid(userRegistrationDto.getUid());
        user.setName(userRegistrationDto.getName());
        user.setBirthDate(userRegistrationDto.getBirthDate());
        user.setFinger(userRegistrationDto.getFinger());

        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));
        user.setCreatedAt(zonedDateTimeNow)
             .setUpdatedAt(zonedDateTimeNow);

        return userRepository.save(user);
    }
}
