package com.yolshin.lifegamebot.component;

import com.yolshin.lifegamebot.component.lifeGame.LifeGameRestApiClient;
import com.yolshin.lifegamebot.component.lifeGame.dto.FileDTO;
import com.yolshin.lifegamebot.component.lifeGame.dto.OrderPictureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.starter.AfterBotRegistration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class LifeGameBot extends TelegramLongPollingBot implements LongPollingBot {
    Logger log = LoggerFactory.getLogger(LifeGameBot.class);

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    LifeGameRestApiClient api;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("onUpdateReceived start");
        if (!update.hasMessage()) return;
        if (!update.getMessage().getFrom().getUserName().equals("YGreens")) return;

        if (update.getMessage().hasPhoto()) {
            List<PhotoSize> t = update.getMessage().getPhoto();


            GetFile getFile = new GetFile();
            getFile.setFileId(t.get(3).getFileId());
            try {
                String filePath = execute(getFile).getFilePath();
                File file = downloadFile(filePath);

                String[] path = filePath.split("/");

                FileDTO fileDTO = api.createFile(file, path[path.length - 1]);

                OrderPictureDTO orderPictureDTO = new OrderPictureDTO();
                orderPictureDTO.setPicture(fileDTO);
                orderPictureDTO.setDescription("OCR не проводился");
                api.createEntityOrderPicture(orderPictureDTO);

                System.out.println(file.getName());
            } catch (TelegramApiException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            log.info(messageText);
        }
        log.info("onUpdateReceived end, {}", update);
    }

    @AfterBotRegistration
    public void afterBotRegistration(BotSession botSession) {
        log.info("LifeGameBot registered");
    }
}
