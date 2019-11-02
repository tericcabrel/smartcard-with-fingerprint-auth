package com.tericcabrel.osiris.bootstrap;

import com.tericcabrel.osiris.models.User;
import com.tericcabrel.osiris.repositories.UserRepository;
import com.tericcabrel.osiris.utils.Helpers;
import com.tericcabrel.osiris.utils.Messaging;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class Seeder implements ApplicationListener<ContextRefreshedEvent> {
    private UserRepository userRepository;

    public Seeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadUsers();

        Messaging.getConnection();
    }

    private void loadUsers() {
        String[][] users = new String[][] {
            new String[] { "Jonathan Sullivan", "1989-04-10", "No" },
            new String[] { "Bryan Mendez", "1998-04-23", "No" },
            new String[] { "Donna Carpenter", "1996-03-19", "No" },
            new String[] { "Andrea Washington", "1990-11-22", "No" },
            new String[] { "Eric Lawrence", "1998-08-01", "No" },
        };

        for (String[] u: users) {
            User user = userRepository.findByName(u[0]);
            if (user == null) {
                ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));

                user = new User();
                user.setName(u[0])
                    .setUid(Helpers.generateRandomString())
                    .setBirthDate(u[1])
                    .setFinger(u[2])
                    .setCreatedAt(zonedDateTimeNow)
                    .setUpdatedAt(zonedDateTimeNow);

                userRepository.save(user);
            }
        }
    }
}
