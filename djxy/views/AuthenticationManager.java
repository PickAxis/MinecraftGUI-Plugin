package djxy.views;

import djxy.controllers.MainController;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.*;
import djxy.models.component.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class AuthenticationManager extends ComponentManager {

    private final static int maxTry = 3;
    private final static String panelId = "@AUTH_PANEL";
    private final static String buttonId = "@AUTH_BUTTON";
    private final static String inputId = "@AUTH_INPUT";

    private final HashMap<String, Integer> playersTrying;
    private final HashMap<String, String> playersCode;
    private final MainController mainController;

    public AuthenticationManager(MainController mainController) {
        super(true);
        this.mainController = mainController;
        playersTrying = new HashMap<>();
        playersCode = new HashMap<>();
    }

    @Override
    public void initPlayerGUI(String playerUUID) {
        Component panel = new Component(ComponentType.PANEL, panelId);

        panel.getAttributes().setPosition(Position.MIDDLE);
        panel.getAttributes().setXRelative(-.5f);
        panel.getAttributes().setYRelative(-.5f);
        panel.getAttributes().setWidth(ComponentState.NORMAL, 200);
        panel.getAttributes().setHeight(ComponentState.NORMAL, 62);
        panel.getAttributes().setBackground(ComponentState.NORMAL, new Color(22, 73, 154, 255));
        panel.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, true, true, true));
        panel.getAttributes().setBorderSize(ComponentState.NORMAL, 2);
        panel.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(69, 90, 100, 255));

        Component input = new Component(ComponentType.INPUT_NUMERIC_NO_DECIMAL, inputId, panel.getId());

        input.getAttributes().setPosition(Position.MIDDLE);
        input.getAttributes().setXRelative(-.5f);
        input.getAttributes().setYRelative(-27);
        input.getAttributes().setWidth(ComponentState.NORMAL, 160);
        input.getAttributes().setHeight(ComponentState.NORMAL, 21);
        input.getAttributes().setBackground(ComponentState.NORMAL, new Color(110, 126, 148, 255));
        input.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(false, false, false, true));
        input.getAttributes().setBorderSize(ComponentState.NORMAL, 1);
        input.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(182, 182, 182, 255));
        input.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
        input.getAttributes().setTextColor(ComponentState.NORMAL, new Color(33, 33, 33, 255));
        input.getAttributes().setFont(ComponentState.NORMAL, djxy.models.component.Font.NORMAL);
        input.getAttributes().setFont(ComponentState.HOVER, djxy.models.component.Font.SHADOW);

        Component button = new Component(ComponentType.BUTTON, buttonId, panel.getId());

        button.getAttributes().setPosition(Position.MIDDLE);
        button.getAttributes().setXRelative(-.5f);
        button.getAttributes().setYRelative(6);
        button.getAttributes().setWidth(ComponentState.NORMAL, 75);
        button.getAttributes().setHeight(ComponentState.NORMAL, 21);
        button.getAttributes().setBackground(ComponentState.NORMAL, new Color(27, 88, 184, 255));
        button.getAttributes().setBackground(ComponentState.HOVER, new Color(31, 174, 255, 255));
        button.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, true, true, true));
        button.getAttributes().setBorderSize(ComponentState.NORMAL, 2);
        button.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(182, 182, 182, 255));
        button.getAttributes().setValue("Send code");
        button.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
        button.getAttributes().setTextColor(ComponentState.NORMAL, new Color(255, 0, 0, 255));
        button.getAttributes().addInput(input.getId());


        mainController.sendCommandsTo(playerUUID, panel.getCommands());
        mainController.sendCommandsTo(playerUUID, input.getCommands());
        mainController.sendCommandsTo(playerUUID, button.getCommands());

        initPlayerAuth(playerUUID);
    }

    @Override
    public void receiveForm(String playerUUID, Form form) {
        String codeReceived = form.getInput(inputId);
        String playerCode = playersCode.get(playerUUID);

        if(codeReceived.equals(playerCode)){
            mainController.sendCommandsTo(playerUUID, mainController.createCommandRemoveComponent(panelId));
            mainController.setPlayersAuthenticated(playerUUID);
        }
        else{
            playersTrying.put(playerUUID, playersTrying.get(playerUUID)+1);


            if(playersTrying.get(playerUUID) == maxTry)
                mainController.closePlayerConnection(playerUUID);
        }
    }

    private void initPlayerAuth(String playerUUID){
        String code = (new Random().nextInt(900000)+100000)+"";
        playersTrying.put(playerUUID, 0);
        playersCode.put(playerUUID, code);
        System.out.println(code);
    }
}
