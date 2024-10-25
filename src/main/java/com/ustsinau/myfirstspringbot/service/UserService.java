package com.ustsinau.myfirstspringbot.service;

import com.ustsinau.myfirstspringbot.model.User;
import com.ustsinau.myfirstspringbot.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.Message;


import java.sql.Timestamp;


@Component
@Slf4j
@AllArgsConstructor
public class UserService {


    private final UserRepository userRepository;


    public void registerUser(Message msg) {

        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }


}
