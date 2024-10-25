package com.ustsinau.myfirstspringbot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class Keyboard {

  private  ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
  private   List<KeyboardRow> keyboardRows = new ArrayList<>();

    public ReplyKeyboardMarkup keyboardByDefault() {

        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("get random joke");

        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup keyboardForStart() {

        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("get random joke");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup keyboardForMyData() {

        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();

        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }
}
