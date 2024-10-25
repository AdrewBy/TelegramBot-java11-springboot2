package com.ustsinau.myfirstspringbot.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
@PropertySource("application.yaml")
public class BotConfig {

  @Value("${telegram.bot.bot-name}")
  String botName;

  @Value("${telegram.bot.token}")
  String token;

  @Value("${telegram.bot.owner}")
  Long ownerId;
}
