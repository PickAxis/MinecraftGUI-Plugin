package djxy.views;

import djxy.controllers.MainController;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.Component;
import djxy.models.component.*;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class AuthenticationManager extends ComponentManager {

    protected static final String id = "AuthenticationManager MinecraftGUI";
    private final static int maxTry = 3;
    private final static String panelId = "@AUTH_PANEL";
    private final static String inputId = "@AUTH_INPUT";
    private final static String imageId = "@AUTH_IMAGE_LOGO";
    private final static String lineId = "@AUTH_LINE";
    private final static String paraId = "@AUTH_PARA";
    private final static String buttonSendCodeInChatId = "@AUTH_BUTTON_SEND_CODE_IN_CHAT";
    private final static String buttonAuthenticateId = "@AUTH_BUTTON_AUTHENTICATE";

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
        super(true);
        this.mainController = mainController;
        playersTrying = new HashMap<>();
        playersCode = new HashMap<>();
        initComps();
    }

    @Override
    public void initPlayerGUI(String playerUUID) {
        mainController.sendCommandsTo(playerUUID, panel.getCommands());
        mainController.sendCommandsTo(playerUUID, image.getCommands());
        mainController.sendCommandsTo(playerUUID, line.getCommands());
        mainController.sendCommandsTo(playerUUID, input.getCommands());
        mainController.sendCommandsTo(playerUUID, paragraph.getCommands());
        mainController.sendCommandsTo(playerUUID, buttonSendCodeInChat.getCommands());
        mainController.sendCommandsTo(playerUUID, buttonAuthenticate.getCommands());

        initPlayerAuth(playerUUID);
    }

    @Override
    public void receiveForm(String playerUUID, Form form) {
        if(form.getButtonId().equals(buttonAuthenticateId)) {
            String codeReceived = form.getInput(inputId);
            String playerCode = playersCode.get(playerUUID);

            if (codeReceived.equals(playerCode)) {
                mainController.sendCommandsTo(playerUUID, mainController.createCommandRemoveComponent(panelId));
                mainController.setPlayersAuthenticated(playerUUID);
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
        try {
            Player player = Sponge.game.getServer().getPlayer(UUID.fromString(playerUUID)).get();
            String code = playersCode.get(playerUUID);

            player.sendMessage(Texts.builder("Your authentication code: ").color(TextColors.GREEN).append(Texts.builder(code).color(TextColors.RED).append(Texts.builder(".").color(TextColors.GREEN).build()).build()).build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initPlayerAuth(String playerUUID) {
        String code = (new Random().nextInt(900000)+100000)+"";
        playersTrying.put(playerUUID, 0);
        playersCode.put(playerUUID, code);
        sendPlayerCode(playerUUID);
    }

    private void initComps(){
        panel = new Component(ComponentType.PANEL, panelId);

        panel.getAttributes().setPosition(Position.MIDDLE);
        panel.getAttributes().setXRelative(-.5f);
        panel.getAttributes().setYRelative(-.5f);
        panel.getAttributes().setWidth(ComponentState.NORMAL, 200);
        panel.getAttributes().setHeight(ComponentState.NORMAL, 91);
        panel.getAttributes().setBackground(ComponentState.NORMAL, new Color(58, 58, 58, 255));

        image = new Component(ComponentType.IMAGE, imageId, panelId);

        image.getAttributes().setXRelative(2);
        image.getAttributes().setYRelative(2);
        image.getAttributes().setWidth(ComponentState.NORMAL, 75);
        image.getAttributes().setHeight(ComponentState.NORMAL, 26);
        image.getAttributes().setImageName(ComponentState.NORMAL, "logoMinecraftGUI.png");
        image.getAttributes().setImageType(ComponentState.NORMAL, ImageType.CUSTOM);

        line = new Component(ComponentType.PANEL, lineId, panelId);

        line.getAttributes().setYRelative(30);
        line.getAttributes().setMarginLeft(ComponentState.NORMAL, 2);
        line.getAttributes().setMarginRight(ComponentState.NORMAL, 2);
        line.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        line.getAttributes().setHeight(ComponentState.NORMAL, 1);
        line.getAttributes().setBackground(ComponentState.NORMAL, new Color(108, 108, 108, 255));

        paragraph = new Component(ComponentType.PARAGRAPH, paraId, panelId);

        paragraph.getAttributes().setYRelative(33);
        paragraph.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        paragraph.getAttributes().setHeight(ComponentState.NORMAL, 27);
        paragraph.getAttributes().setMarginLeft(ComponentState.NORMAL, 3);
        paragraph.getAttributes().setMarginRight(ComponentState.NORMAL, 3);
        paragraph.getAttributes().setValue("This server need an authentication. Enter the code you received in the chat. You only have 3 chances.");
        paragraph.getAttributes().setTextColor(ComponentState.NORMAL, new Color(240, 240, 240, 255));

        input = new Component(ComponentType.INPUT_NUMERIC_NO_DECIMAL, inputId, panelId);

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

        buttonSendCodeInChat = new Component(ComponentType.BUTTON, buttonSendCodeInChatId, panelId);

        buttonSendCodeInChat.getAttributes().setYRelative(80);
        buttonSendCodeInChat.getAttributes().setXRelative(1f);
        buttonSendCodeInChat.getAttributes().setWidth(ComponentState.NORMAL, .5f);
        buttonSendCodeInChat.getAttributes().setHeight(ComponentState.NORMAL, 20);
        buttonSendCodeInChat.getAttributes().setBackground(ComponentState.NORMAL, new Color(70, 70, 70, 255));
        buttonSendCodeInChat.getAttributes().setBackground(ComponentState.HOVER, new Color(82, 82, 82, 255));
        buttonSendCodeInChat.getAttributes().setBackground(ComponentState.CLICK, new Color(94, 94, 94, 255));
        buttonSendCodeInChat.getAttributes().setValue("Send code");
        buttonSendCodeInChat.getAttributes().setTextColor(ComponentState.NORMAL, new Color(240, 240, 240, 255));

        buttonAuthenticate = new Component(ComponentType.BUTTON, buttonAuthenticateId, panelId);

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
