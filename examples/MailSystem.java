package examples;

import djxy.controllers.MainController;
import djxy.models.ComponentManager;
import djxy.models.Form;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import djxy.models.component.*;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.LoadCompleteEvent;

//@Plugin(id = MailSystem.id, name = "Messenger Plugin", version = "1.0")
public class MailSystem extends ComponentManager {

    protected static final String id = "Messenger";

    private Component menuPanel;
    private Component menuCreateMessage;
    private Component menuReadMessage;
    private Component menuMessageReceivedImage;
    private Component menuMessageReceivedNb;

    private Component readMessagePanel;
    private Component readMessageCloseButton;
    private Component readMessageParagraphFrom;
    private Component readMessageParagraphFromPlayerName;
    private Component readMessageParagraphMessage;
    private Component readMessageParagraphMessageContent;

    private Component createMessagePanel;
    private Component createMessageCloseButton;
    private Component createMessageParagraphSendTo;
    private Component createMessageInputSendTo;
    private Component createMessageParagraphMessage;
    private Component createMessageInputMessage;
    private Component createMessageButtonSendMessage;

    private final HashMap<String, ArrayList<Message>> playersMessages;
    private final HashMap<String, String> playersUUID;//Player UUID, Player name
    private final HashMap<String, String> playersName;//Player name, Player UUID

    public MailSystem() {
        super(false);
        playersMessages = new HashMap<>();
        playersUUID = new HashMap<>();
        playersName = new HashMap<>();
        initComps();
        addComponentIdToListen(createMessageButtonSendMessage.getId());
        addComponentIdToListen(createMessageCloseButton.getId());
        addComponentIdToListen(readMessageCloseButton.getId());
        addComponentIdToListen(menuCreateMessage.getId());
        addComponentIdToListen(menuReadMessage.getId());
    }

    @Override
    public void initPlayerGUI(MainController mc, String playerUUID) {
        sendOpenMessageComponents(mc, playerUUID);
    }

    @Override
    public void receiveForm(MainController mc, String playerUUID, Form form) {
        if(form.getButtonId().equals(createMessageButtonSendMessage.getId())){
            sendMessageTo(playerUUID, form.getInput(createMessageInputSendTo.getId()), form.getInput(createMessageInputMessage.getId()));
            mc.removeComponent(playerUUID, createMessagePanel.getId());
        }
        else if(form.getButtonId().equals(createMessageCloseButton.getId()))
            mc.removeComponent(playerUUID, createMessagePanel.getId());
        else if(form.getButtonId().equals(readMessageCloseButton.getId()))
            mc.removeComponent(playerUUID, readMessagePanel.getId());
        else if(form.getButtonId().equals(menuCreateMessage.getId())){
            mc.removeComponent(playerUUID, readMessagePanel.getId());
            sendCreateMessageComponents(mc, playerUUID);
        }
        else if(form.getButtonId().equals(menuReadMessage.getId())){
            mc.removeComponent(playerUUID, createMessagePanel.getId());
            sendReadMessageComponents(mc, playerUUID);
        }
    }

    //Register the ComponentManager
    @Subscribe
    public void onLoadCompleteEvent(LoadCompleteEvent event){
        MainController.getInstance().addComponentManager(this);
    }


    //Register the player
    @Subscribe
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        playersName.put(event.getUser().getName(), event.getUser().getUniqueId().toString());
        playersUUID.put(event.getUser().getUniqueId().toString(), event.getUser().getName());
        playersMessages.put(event.getUser().getName(), new ArrayList<Message>());
    }

    private void sendMessageTo(String from, String to, String message){
        try{
            playersMessages.get(to).add(new Message(playersUUID.get(from), message));
            updateMessageNb(playersName.get(to));
        }catch(Exception e){e.printStackTrace();}
    }

    //Update the number of message the player has
    private void updateMessageNb(String playerUUID){
        Attributes cu = new Attributes(menuMessageReceivedNb.getId());
        cu.setValue(playersMessages.get(playersUUID.get(playerUUID)).size()+"");

        MainController.getInstance().updateComponent(playerUUID, cu);
    }

    //Send the components for the menu
    private void sendOpenMessageComponents(MainController mc, String playerUUID){
        mc.createComponent(playerUUID, menuPanel);
        mc.createComponent(playerUUID, menuCreateMessage);
        mc.createComponent(playerUUID, menuReadMessage);
        mc.createComponent(playerUUID, menuMessageReceivedImage);
        mc.createComponent(playerUUID, menuMessageReceivedNb);
        updateMessageNb(playerUUID);
    }

    //Send the components when the player read a message
    private void sendReadMessageComponents(MainController mc, String playerUUID){
        mc.createComponent(playerUUID, readMessagePanel);
        mc.createComponent(playerUUID, readMessageCloseButton);
        mc.createComponent(playerUUID, readMessageParagraphFrom);
        mc.createComponent(playerUUID, readMessageParagraphFromPlayerName);
        mc.createComponent(playerUUID, readMessageParagraphMessage);
        mc.createComponent(playerUUID, readMessageParagraphMessageContent);
        ArrayList<Message> mes = playersMessages.get(playersUUID.get(playerUUID));

        if(mes != null && mes.size() > 0){
            Message message = mes.get(0);
            mes.remove(0);
            updateMessageNb(playerUUID);
            Attributes cu1 = new Attributes(readMessageParagraphFromPlayerName.getId());
            Attributes cu2 = new Attributes(readMessageParagraphMessageContent.getId());

            cu1.setValue(message.from);
            cu2.setValue(message.message);

            mc.updateComponent(playerUUID, cu2);
            mc.updateComponent(playerUUID, cu1);
        }
    }

    //Send the components when the player create a message
    private void sendCreateMessageComponents(MainController mc, String playerUUID){
        mc.createComponent(playerUUID, createMessagePanel);
        mc.createComponent(playerUUID, createMessageCloseButton);
        mc.createComponent(playerUUID, createMessageInputSendTo);
        mc.createComponent(playerUUID, createMessageInputMessage);
        mc.createComponent(playerUUID, createMessageParagraphSendTo);
        mc.createComponent(playerUUID, createMessageParagraphMessage);
        mc.createComponent(playerUUID, createMessageButtonSendMessage);
    }

    //Init the components with all their attributes
    private void initComps(){
        menuPanel = new Component(ComponentType.PANEL, id+"OPEN_PANEL");

        menuPanel.getAttributes().setLocationFreeze(false);
        menuPanel.getAttributes().setWidth(ComponentState.NORMAL, 80);
        menuPanel.getAttributes().setHeight(ComponentState.NORMAL, 60);

        menuCreateMessage = new Component(ComponentType.BUTTON, id+"OPEN_CREATE_BUTTON", menuPanel.getId());

        menuCreateMessage.getAttributes().setBackground(ComponentState.NORMAL, Background.BUTTON);
        menuCreateMessage.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        menuCreateMessage.getAttributes().setHeight(ComponentState.NORMAL, 20);
        menuCreateMessage.getAttributes().setValue("Send message");
        menuCreateMessage.getAttributes().setFont(ComponentState.NORMAL, Font.SHADOW);
        menuCreateMessage.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);
        menuCreateMessage.getAttributes().setTextColor(ComponentState.HOVER, Color.YELLOW);

        menuReadMessage = new Component(ComponentType.BUTTON, id+"OPEN_READ_BUTTON", menuPanel.getId());

        menuReadMessage.getAttributes().setYRelative(1f);
        menuReadMessage.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        menuReadMessage.getAttributes().setHeight(ComponentState.NORMAL, 20);
        menuReadMessage.getAttributes().setBackground(ComponentState.NORMAL, Background.BUTTON);
        menuReadMessage.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);
        menuReadMessage.getAttributes().setValue("Read message");
        menuReadMessage.getAttributes().setFont(ComponentState.NORMAL, Font.SHADOW);
        menuReadMessage.getAttributes().setTextColor(ComponentState.HOVER, Color.YELLOW);

        menuMessageReceivedImage = new Component(ComponentType.IMAGE, id+"OPEN_MESSAGE_IMAGE", menuPanel.getId());

        menuMessageReceivedImage.getAttributes().setYRelative(40);
        menuMessageReceivedImage.getAttributes().setImageType(ComponentState.NORMAL, ImageType.MINECRAFT);
        menuMessageReceivedImage.getAttributes().setImageName(ComponentState.NORMAL, "paper");

        menuMessageReceivedNb = new Component(ComponentType.PARAGRAPH, id+"OPEN_MESSAGE_PARAGRAPH", menuMessageReceivedImage.getId());

        menuMessageReceivedNb.getAttributes().setPosition(Position.TOP_RIGHT);
        menuMessageReceivedNb.getAttributes().setWidth(ComponentState.NORMAL, 62);
        menuMessageReceivedNb.getAttributes().setHeight(ComponentState.NORMAL, 9);
        menuMessageReceivedNb.getAttributes().setXRelative(2);
        menuMessageReceivedNb.getAttributes().setYRelative(4);
        menuMessageReceivedNb.getAttributes().setFont(ComponentState.NORMAL, Font.SHADOW);
        menuMessageReceivedNb.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);

        ////////////////////////////////////////////////////////////////////////
        createMessagePanel = new Component(ComponentType.PANEL, "MESSAGE_PANEL");

        createMessagePanel.getAttributes().setPosition(Position.MIDDLE);
        createMessagePanel.getAttributes().setXRelative(-.5f);
        createMessagePanel.getAttributes().setYRelative(-.5f);
        createMessagePanel.getAttributes().setWidth(ComponentState.NORMAL, 200);
        createMessagePanel.getAttributes().setHeight(ComponentState.NORMAL, 200);
        createMessagePanel.getAttributes().setBackground(ComponentState.NORMAL, new Color(164, 55, 65, 255));
        createMessagePanel.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessagePanel.getAttributes().setBorderSize(ComponentState.NORMAL, 2);
        createMessagePanel.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessagePanel.getAttributes().setPaddingSize(ComponentState.NORMAL, 8);
        createMessagePanel.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(69, 90, 100, 255));

        createMessageCloseButton = new Component(ComponentType.BUTTON, "MESSAGE_CLOSE", createMessagePanel.getId());

        createMessageCloseButton.getAttributes().setHeight(ComponentState.NORMAL, 9);
        createMessageCloseButton.getAttributes().setWidth(ComponentState.NORMAL, 8);
        createMessageCloseButton.getAttributes().setXRelative(-7);
        createMessageCloseButton.getAttributes().setYRelative(-8);
        createMessageCloseButton.getAttributes().setValue("x");
        createMessageCloseButton.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
        createMessageCloseButton.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);
        createMessageCloseButton.getAttributes().setBackground(ComponentState.NORMAL, new Color(240, 0, 0, 255));
        createMessageCloseButton.getAttributes().setBackground(ComponentState.CLICK, new Color(200, 0, 0, 255));
        createMessageCloseButton.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, false, false, false));
        createMessageCloseButton.getAttributes().setPaddingSize(ComponentState.NORMAL, 1);

        createMessageParagraphSendTo = new Component(ComponentType.PARAGRAPH, "MESSAGE_PARA_TO", createMessagePanel.getId());

        createMessageParagraphSendTo.getAttributes().setYRelative(2);
        createMessageParagraphSendTo.getAttributes().setWidth(ComponentState.NORMAL, 40);
        createMessageParagraphSendTo.getAttributes().setHeight(ComponentState.NORMAL, 9);
        createMessageParagraphSendTo.getAttributes().setValue("Send to:");
        createMessageParagraphSendTo.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessageParagraphSendTo.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        createMessageInputSendTo = new Component(ComponentType.INPUT, "MESSAGE_INPUT_TO", createMessagePanel.getId());

        createMessageInputSendTo.getAttributes().setYRelative(2);
        createMessageInputSendTo.getAttributes().setWidth(ComponentState.NORMAL, 154);
        createMessageInputSendTo.getAttributes().setHeight(ComponentState.NORMAL, 9);
        createMessageInputSendTo.getAttributes().setXRelative(44);
        createMessageInputSendTo.getAttributes().setMaxTextLines(1);
        createMessageInputSendTo.getAttributes().setBackground(ComponentState.NORMAL, new Color(100, 100, 100, 255));
        createMessageInputSendTo.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessageInputSendTo.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        createMessageParagraphMessage = new Component(ComponentType.PARAGRAPH, "MESSAGE_PARA_MESSAGE", createMessagePanel.getId());

        createMessageParagraphMessage.getAttributes().setYRelative(22);
        createMessageParagraphMessage.getAttributes().setWidth(ComponentState.NORMAL, 50);
        createMessageParagraphMessage.getAttributes().setHeight(ComponentState.NORMAL, 9);
        createMessageParagraphMessage.getAttributes().setValue("Message");
        createMessageParagraphMessage.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessageParagraphMessage.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        createMessageInputMessage = new Component(ComponentType.INPUT, "MESSAGE_INPUT_MESSAGE", createMessagePanel.getId());

        createMessageInputMessage.getAttributes().setYRelative(34);
        createMessageInputMessage.getAttributes().setXRelative(2);
        createMessageInputMessage.getAttributes().setWidth(ComponentState.NORMAL, 196);
        createMessageInputMessage.getAttributes().setHeight(ComponentState.NORMAL, 144);
        createMessageInputMessage.getAttributes().setBackground(ComponentState.NORMAL, new Color(100, 100, 100, 255));
        createMessageInputMessage.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessageInputMessage.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);
        createMessageInputMessage.getAttributes().setMaxTextLines(16);
        createMessageInputMessage.getAttributes().setHint("Test...");

        createMessageButtonSendMessage = new Component(ComponentType.BUTTON, "MESSAGE_SEND_MESSAGE", createMessagePanel.getId());

        createMessageButtonSendMessage.getAttributes().setPosition(Position.BOTTOM_RIGHT);
        createMessageButtonSendMessage.getAttributes().setYRelative(-9);
        createMessageButtonSendMessage.getAttributes().setXRelative(-83);
        createMessageButtonSendMessage.getAttributes().setValue("Send message");
        createMessageButtonSendMessage.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
        createMessageButtonSendMessage.getAttributes().setTextColor(ComponentState.HOVER, Color.WHITE);
        createMessageButtonSendMessage.getAttributes().setWidth(ComponentState.NORMAL, 80);
        createMessageButtonSendMessage.getAttributes().setHeight(ComponentState.NORMAL, 9);
        createMessageButtonSendMessage.getAttributes().setBackground(ComponentState.NORMAL, new Color(100, 100, 100, 200));
        createMessageButtonSendMessage.getAttributes().setBackground(ComponentState.CLICK, new Color(80, 80, 80, 200));
        createMessageButtonSendMessage.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessageButtonSendMessage.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);
        createMessageButtonSendMessage.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, true, true, true));
        createMessageButtonSendMessage.getAttributes().setBorderSize(ComponentState.NORMAL, 1);
        createMessageButtonSendMessage.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(69, 90, 100, 255));

        createMessageButtonSendMessage.getAttributes().addInput(createMessageInputSendTo.getId());
        createMessageButtonSendMessage.getAttributes().addInput(createMessageInputMessage.getId());

        ////////////////////////////////////////////////////////////////////////
        readMessagePanel = new Component(ComponentType.PANEL, id+"READ_PANEL");

        readMessagePanel.getAttributes().setPosition(Position.MIDDLE);
        readMessagePanel.getAttributes().setXRelative(-.5f);
        readMessagePanel.getAttributes().setYRelative(-.5f);
        readMessagePanel.getAttributes().setWidth(ComponentState.NORMAL, 200);
        readMessagePanel.getAttributes().setHeight(ComponentState.NORMAL, 200);
        readMessagePanel.getAttributes().setBackground(ComponentState.NORMAL, new Color(164, 55, 65, 255));
        readMessagePanel.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, true, true, true));
        readMessagePanel.getAttributes().setBorderSize(ComponentState.NORMAL, 2);
        readMessagePanel.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        readMessagePanel.getAttributes().setPaddingSize(ComponentState.NORMAL, 8);
        readMessagePanel.getAttributes().setBorderColor(ComponentState.NORMAL, new Color(69, 90, 100, 255));

        readMessageCloseButton = new Component(ComponentType.BUTTON, id+"READ_CLOSE_BUTTON", readMessagePanel.getId());

        readMessageCloseButton.getAttributes().setHeight(ComponentState.NORMAL, 9);
        readMessageCloseButton.getAttributes().setWidth(ComponentState.NORMAL, 8);
        readMessageCloseButton.getAttributes().setXRelative(-7);
        readMessageCloseButton.getAttributes().setYRelative(-8);
        readMessageCloseButton.getAttributes().setValue("x");
        readMessageCloseButton.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
        readMessageCloseButton.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);
        readMessageCloseButton.getAttributes().setBackground(ComponentState.NORMAL, new Color(240, 0, 0, 255));
        readMessageCloseButton.getAttributes().setBackground(ComponentState.CLICK, new Color(200, 0, 0, 255));
        readMessageCloseButton.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, false, false, false));
        readMessageCloseButton.getAttributes().setPaddingSize(ComponentState.NORMAL, 1);

        readMessageParagraphFrom = new Component(ComponentType.PARAGRAPH, id+"READ_FROM_PARAGRAPH", readMessagePanel.getId());

        readMessageParagraphFrom.getAttributes().setYRelative(2);
        readMessageParagraphFrom.getAttributes().setWidth(ComponentState.NORMAL, 30);
        readMessageParagraphFrom.getAttributes().setHeight(ComponentState.NORMAL, 9);
        readMessageParagraphFrom.getAttributes().setValue("From:");
        readMessageParagraphFrom.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        readMessageParagraphFrom.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        readMessageParagraphFromPlayerName = new Component(ComponentType.PARAGRAPH, id+"READ_FROM_PLAYER_NAME_PARAGRAPH", readMessagePanel.getId());

        readMessageParagraphFromPlayerName.getAttributes().setYRelative(2);
        readMessageParagraphFromPlayerName.getAttributes().setWidth(ComponentState.NORMAL, 168);
        readMessageParagraphFromPlayerName.getAttributes().setHeight(ComponentState.NORMAL, 9);
        readMessageParagraphFromPlayerName.getAttributes().setXRelative(30);
        readMessageParagraphFromPlayerName.getAttributes().setMaxTextLines(1);
        readMessageParagraphFromPlayerName.getAttributes().setBackground(ComponentState.NORMAL, new Color(100, 100, 100, 255));
        readMessageParagraphFromPlayerName.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        readMessageParagraphFromPlayerName.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        readMessageParagraphMessage = new Component(ComponentType.PARAGRAPH, id+"READ_MESSAGE_PARAGRAPH", readMessagePanel.getId());

        readMessageParagraphMessage.getAttributes().setYRelative(22);
        readMessageParagraphMessage.getAttributes().setWidth(ComponentState.NORMAL, 50);
        readMessageParagraphMessage.getAttributes().setHeight(ComponentState.NORMAL, 9);
        readMessageParagraphMessage.getAttributes().setValue("Message");
        readMessageParagraphMessage.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        readMessageParagraphMessage.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        readMessageParagraphMessageContent = new Component(ComponentType.PARAGRAPH, id+"READ_MESSAGE_CONTENT_PARAGRAPH", readMessagePanel.getId());

        readMessageParagraphMessageContent.getAttributes().setYRelative(34);
        readMessageParagraphMessageContent.getAttributes().setXRelative(2);
        readMessageParagraphMessageContent.getAttributes().setWidth(ComponentState.NORMAL, 196);
        readMessageParagraphMessageContent.getAttributes().setHeight(ComponentState.NORMAL, 144);
        readMessageParagraphMessageContent.getAttributes().setBackground(ComponentState.NORMAL, new Color(100, 100, 100, 255));
        readMessageParagraphMessageContent.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
        readMessageParagraphMessageContent.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);
        readMessageParagraphMessageContent.getAttributes().setMaxTextLines(16);
    }

    private class Message{

        private final String from;
        private final String message;

        public Message(String from, String message) {
            this.from = from;
            this.message = message;
        }

    }

}
