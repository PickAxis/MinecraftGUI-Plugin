/*
 * Copyright (C) Samuel Marchildon-Lavoie - All Rights Reserved
 *
 * License: https://github.com/djxy/MinecraftGUI-Plugin/blob/master/License.txt
 */

package djxy.controllers;

import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.Component;
import djxy.models.component.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class AuthenticationManager implements ComponentManager {

    private final static int maxTry = 3;
    private final static String panelId = "@AUTH_PANEL";
    private final static String inputId = "@AUTH_INPUT";
    private final static String imageId = "@AUTH_IMAGE_LOGO";
    private final static String lineId = "@AUTH_LINE";
    private final static String paraId = "@AUTH_PARA";
    private final static String buttonSendCodeInChatId = "@AUTH_BUTTON_SEND_CODE_IN_CHAT";
    private final static String buttonAuthenticateId = "@AUTH_BUTTON_AUTHENTICATE";

    private final HashMap<String, Boolean> playersAuthenticated;
    private final HashMap<String, Integer> playersTrying;
    private final HashMap<String, String> playersCode;
    private final MainController mainController;
    private Component panel;
    private Component image;
    private Component line;
    private Component input;
    private Component paragraph;
    private Component buttonAuthenticate;
    private Component buttonSendCodeInChat;

    public AuthenticationManager(MainController mainController) {
        this.mainController = mainController;
        playersAuthenticated = new HashMap<>();
        playersTrying = new HashMap<>();
        playersCode = new HashMap<>();
        initComps();
    }

    public void addPlayerToAuthenticate(String playerUUID){
        playersAuthenticated.put(playerUUID, false);
    }

    public boolean isPlayerAuthenticated(String playerUUID){
        return playersAuthenticated.get(playerUUID);
    }

    @Override
    public void initPlayerGUI(String playerUUID) {
        mainController.sendComponentCreate(playerUUID, panel);
        mainController.sendComponentCreate(playerUUID, image);
        mainController.sendComponentCreate(playerUUID, line);
        mainController.sendComponentCreate(playerUUID, input);
        mainController.sendComponentCreate(playerUUID, paragraph);
        mainController.sendComponentCreate(playerUUID, buttonSendCodeInChat);
        mainController.sendComponentCreate(playerUUID, buttonAuthenticate);

        initPlayerAuth(playerUUID);
    }

    @Override
    public void receiveForm(String playerUUID, Form form) {
        if(form.getButtonId().equals(buttonAuthenticateId)) {
            String codeReceived = form.getInput(inputId);
            String playerCode = playersCode.get(playerUUID);

            if (codeReceived.equals(playerCode)) {
                mainController.sendComponentRemove(playerUUID, panelId);
                playersAuthenticated.put(playerUUID, true);
            } else {
                playersTrying.put(playerUUID, playersTrying.get(playerUUID) + 1);

                if (playersTrying.get(playerUUID) == maxTry)
                    mainController.closePlayerConnection(playerUUID);
            }
        }
        else {
            sendPlayerCode(playerUUID);
        }
    }

    private void sendPlayerCode(String playerUUID){
        mainController.getPluginInterface().sendAuthenticationCode(playerUUID, playersCode.get(playerUUID));
    }

    private void initPlayerAuth(String playerUUID) {
        String code = (new Random().nextInt(900000)+100000)+"";
        playersTrying.put(playerUUID, 0);
        playersCode.put(playerUUID, code);
        sendPlayerCode(playerUUID);
    }

    private void initComps(){
        panel = new Component(panelId, ComponentType.PANEL);

        panel.getAttributes().setPosition(Position.MIDDLE);
        panel.getAttributes().setXRelative(-.5f);
        panel.getAttributes().setYRelative(-.5f);
        panel.getAttributes().setWidth(ComponentState.NORMAL, 200);
        panel.getAttributes().setHeight(ComponentState.NORMAL, 91);
        panel.getAttributes().setBackground(ComponentState.NORMAL, new Color(58, 58, 58, 255));

        image = new Component(imageId, ComponentType.IMAGE, panel);

        image.getAttributes().setXRelative(2);
        image.getAttributes().setYRelative(2);
        image.getAttributes().setWidth(ComponentState.NORMAL, 75);
        image.getAttributes().setHeight(ComponentState.NORMAL, 26);
        image.getAttributes().setImageName(ComponentState.NORMAL, "logoMinecraftGUI.png");
        image.getAttributes().setImageType(ComponentState.NORMAL, ImageType.CUSTOM);

        line = new Component(lineId, ComponentType.PANEL, panel);

        line.getAttributes().setYRelative(30);
        line.getAttributes().setMarginLeft(ComponentState.NORMAL, 2);
        line.getAttributes().setMarginRight(ComponentState.NORMAL, 2);
        line.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        line.getAttributes().setHeight(ComponentState.NORMAL, 1);
        line.getAttributes().setBackground(ComponentState.NORMAL, new Color(108, 108, 108, 255));

        paragraph = new Component(paraId, ComponentType.PARAGRAPH, panel);

        paragraph.getAttributes().setYRelative(33);
        paragraph.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        paragraph.getAttributes().setHeight(ComponentState.NORMAL, 27);
        paragraph.getAttributes().setMarginLeft(ComponentState.NORMAL, 3);
        paragraph.getAttributes().setMarginRight(ComponentState.NORMAL, 3);
        paragraph.getAttributes().setValue("This server need an authentication. Enter the code you received in the chat. You only have 3 chances.");
        paragraph.getAttributes().setTextColor(ComponentState.NORMAL, new Color(240, 240, 240, 255));

        input = new Component(inputId, ComponentType.INPUT_INTEGER, panel);

        input.getAttributes().setYRelative(65);
        input.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        input.getAttributes().setHeight(ComponentState.NORMAL, 9);
        input.getAttributes().setMarginLeft(ComponentState.NORMAL, 8);
        input.getAttributes().setMarginRight(ComponentState.NORMAL, 2);
        input.getAttributes().setBackground(ComponentState.NORMAL, new Color(88, 88, 88, 255));
        input.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, false, false, false));
        input.getAttributes().setBorderSize(ComponentState.NORMAL, 2);
        input.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(108, 108, 108, 255));
        input.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, false, true));
        input.getAttributes().setPaddingSize(ComponentState.NORMAL, 4);
        input.getAttributes().setTextColor(ComponentState.NORMAL, new Color(240, 240, 240, 255));
        input.getAttributes().setHint("Code");

        buttonSendCodeInChat = new Component(buttonSendCodeInChatId, ComponentType.BUTTON, panel);

        buttonSendCodeInChat.getAttributes().setYRelative(80);
        buttonSendCodeInChat.getAttributes().setXRelative(1f);
        buttonSendCodeInChat.getAttributes().setWidth(ComponentState.NORMAL, .5f);
        buttonSendCodeInChat.getAttributes().setHeight(ComponentState.NORMAL, 20);
        buttonSendCodeInChat.getAttributes().setBackground(ComponentState.NORMAL, new Color(70, 70, 70, 255));
        buttonSendCodeInChat.getAttributes().setBackground(ComponentState.HOVER, new Color(82, 82, 82, 255));
        buttonSendCodeInChat.getAttributes().setBackground(ComponentState.CLICK, new Color(94, 94, 94, 255));
        buttonSendCodeInChat.getAttributes().setValue("Send code");
        buttonSendCodeInChat.getAttributes().setTextColor(ComponentState.NORMAL, new Color(240, 240, 240, 255));

        buttonAuthenticate = new Component(buttonAuthenticateId, ComponentType.BUTTON, panel);

        buttonAuthenticate.getAttributes().setYRelative(80);
        buttonAuthenticate.getAttributes().setWidth(ComponentState.NORMAL, .5f);
        buttonAuthenticate.getAttributes().setHeight(ComponentState.NORMAL, 20);
        buttonAuthenticate.getAttributes().setBackground(ComponentState.NORMAL, new Color(70, 70, 70, 255));
        buttonAuthenticate.getAttributes().setBackground(ComponentState.HOVER, new Color(82, 82, 82, 255));
        buttonAuthenticate.getAttributes().setBackground(ComponentState.CLICK, new Color(94, 94, 94, 255));
        buttonAuthenticate.getAttributes().setValue("Authenticate");
        buttonAuthenticate.getAttributes().setTextColor(ComponentState.NORMAL, new Color(240, 240, 240, 255));

        buttonAuthenticate.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(false, false, true, false));
        buttonAuthenticate.getAttributes().setBorderSize(ComponentState.NORMAL, 1);
        buttonAuthenticate.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(108, 108, 108, 255));
        buttonAuthenticate.getAttributes().addInput(inputId);
    }
}
