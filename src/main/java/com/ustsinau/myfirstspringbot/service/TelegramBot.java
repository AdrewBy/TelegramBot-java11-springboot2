package com.ustsinau.myfirstspringbot.service;


import com.ustsinau.myfirstspringbot.button.Button;
import com.ustsinau.myfirstspringbot.config.BotConfig;
import com.ustsinau.myfirstspringbot.model.Ads;
import com.ustsinau.myfirstspringbot.model.User;
import com.ustsinau.myfirstspringbot.repository.AdsRepository;
import com.ustsinau.myfirstspringbot.repository.UserRepository;
import com.ustsinau.myfirstspringbot.keyboard.Keyboard;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {


    private final UserRepository userRepository;
    private final UserService userService;

    private final AdsRepository adsRepository;

    private final Keyboard keyboard;
    private final Button button;

    private final BotConfig config;

    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";


    @Autowired
    public TelegramBot(UserRepository userRepository, UserService userService, AdsRepository adsRepository, Keyboard keyboard, Button button, BotConfig config) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.adsRepository = adsRepository;
        this.keyboard = keyboard;
        this.button = button;
        this.config = config;
        initializeCommands();
    }

    private void initializeCommands() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/register", "register"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            //send messages all users from owner
            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user : users) {
                    prepareAndSendMessageWithoutKeyboard(user.getChatId(), textToSend);
                }
            } else {

                switch (messageText) {
                    case "/start":

                        userService.registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/help":

                        prepareAndSendMessageWithoutKeyboard(chatId, HELP_TEXT);
                        break;

                    case "/register":

                        register(chatId);
                        break;

                    case "/mydata":

                        myDataCommandReceived(chatId, "Here your data");
                        break;
                    default:

                        prepareAndSendMessageWithoutKeyboard(chatId, "Sorry, command was not recognized");

                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                String text = "You pressed YES button";
                executeEditMessageText(text, chatId, messageId);
            } else if (callbackData.equals(NO_BUTTON)) {
                String text = "You pressed NO button";
                executeEditMessageText(text, chatId, messageId);
            }
        }


    }

    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        message.setReplyMarkup(button.buttonForRegister());

        executeMessage(message);
    }

    private void myDataCommandReceived(long chatId, String message) {

        String answer = EmojiParser.parseToUnicode("Hi, " + message + " :wink:");
        log.info("Replied to user " + message);

        sendMessage(chatId, answer, keyboard.keyboardForMyData());
    }

    private void startCommandReceived(long chatId, String name) {

        //https://emojibase.dev/ -  Emojibase
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :wink:");
        log.info("Replied to user " + name);

        sendMessage(chatId, answer, keyboard.keyboardForStart());
    }


    private void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void prepareAndSendMessageWithoutKeyboard(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

   private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }



    @Scheduled(cron = "0 * * * * *")
    private void sendAds(){

        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for(Ads ad: ads) {
            for (User user: users){
                prepareAndSendMessageWithoutKeyboard(user.getChatId(), ad.getAd());
            }
        }

    }
}