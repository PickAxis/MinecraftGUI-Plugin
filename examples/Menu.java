package examples;

import djxy.controllers.MainController;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = Menu.id, name = "Menu MinecraftGUI Plugin", version = "1.0")
public final class Menu extends ComponentManager{

    protected static final String id = "Menu";
    private static final Color colorBackground = new Color(45, 137, 239, 255);
    private static final Color colorHover = new Color(100, 100, 100, 70);
    private static final Color colorClick = new Color(100, 100, 100, 140);

    private final HashMap<String, String> playersLastButtonClicked;
    private final HashMap<String, Boolean> playersMenuVisible;
    private final ArrayList<Button> buttons;
    
    //Description of every component on the function initComps()
    private Component mainPanel;
    private Component mainPanelCloseButton;
    private Component menuImage;
    private Component menuOpenCloseButton;
    private Component menuPanel;
    private Component menuTitle;
    private Component menuList;
    private Attributes menuListButtonNext;
    private Attributes menuListButtonPrevious;

    public Menu() throws Exception {
        super(false);//The plugin don't require the authentication of the player.
        playersMenuVisible = new HashMap<>();
        playersLastButtonClicked = new HashMap<>();
        buttons = new ArrayList<>();
        initComps();

        //Components to listen when clicked
        addComponentIdToListen(mainPanelCloseButton.getId());
        addComponentIdToListen(menuOpenCloseButton.getId());
    }

    //Create a button that open the default web browser of the player
    public void addButton(Icon icon, String id, String value, String url){
        buttons.add(new Button(icon, id, value, url));
    }

    //Create a normal button
    public void addButton(Icon icon, String id, String value){
        buttons.add(new Button(icon, id, value, ""));
    }

    //When the player is authenticated, I send all the component to create the menu, the main panel and every buttons
    @Override
    public void initPlayerGUI(MainController mc, String string) {
        //Menu
        mc.createComponent(string, menuPanel);
        mc.createComponent(string, menuImage);
        mc.createComponent(string, menuTitle);
        mc.createComponent(string, menuOpenCloseButton);
        mc.createComponent(string, menuList);
        mc.updateComponent(string, menuListButtonNext);
        mc.updateComponent(string, menuListButtonPrevious);
        
        //Main panel
        mc.createComponent(string, mainPanel);
        mc.createComponent(string, mainPanelCloseButton);

        //Buttons
        for(Button button : buttons){
            mc.createComponent(string, button.panel);
            mc.createComponent(string, button.image);
            mc.createComponent(string, button.text);
            mc.createComponent(string, button.input);
            mc.createComponent(string, button.button);
            mc.createComponent(string, button.line);
        }
    }

    @Override
    public void receiveForm(MainController mc, String playerUUID, Form form) {
        
        if(form.getButtonId().equals(mainPanelCloseButton.getId())){
            Attributes mainPanelUpdate = new Attributes(mainPanel.getId());
            mainPanelUpdate.setVisible(false);
            mc.updateComponent(playerUUID, mainPanelUpdate);
        }
        else if(form.getButtonId().equals(menuOpenCloseButton.getId())){
            boolean visible;

            if(playersMenuVisible.get(playerUUID) == Boolean.TRUE){//If menu is visible
                visible = false;
                playersMenuVisible.put(playerUUID, Boolean.FALSE);

                String panelId = playersLastButtonClicked.get(playerUUID);

                //If the player clicked on one of the button of the list, it will remove his white border
                if(panelId != null){
                    Attributes update = new Attributes(panelId);

                    update.setBorderSize(ComponentState.NORMAL, 0);
                    mc.updateComponent(playerUUID, update);
                }
            }
            else{
                visible = true;
                playersMenuVisible.put(playerUUID, Boolean.TRUE);
            }

            Attributes menuPanelUpdate = new Attributes(menuPanel.getId());
            menuPanelUpdate.setVisible(visible);
            mc.updateComponent(playerUUID, menuPanelUpdate);
            Attributes mainPanelUpdate = new Attributes(mainPanel.getId());
            mainPanelUpdate.setVisible(visible);
            mc.updateComponent(playerUUID, mainPanelUpdate);
        }
        else{
            String panelId = playersLastButtonClicked.get(playerUUID);

            //If the player clicked on one of the button of the list, it will remove his white border
            if(panelId != null){
                Attributes update = new Attributes(panelId);

                update.setBorderSize(ComponentState.NORMAL, 0);
                mc.updateComponent(playerUUID, update);
            }

            Attributes update = new Attributes(form.getInput(form.getButtonId() + "Input"));

            update.setBorderSize(ComponentState.NORMAL, 4);
            mc.updateComponent(playerUUID, update);

            Attributes mainPanelUpdate = new Attributes(mainPanel.getId());

            mainPanelUpdate.setVisible(true);

            switch(form.getButtonId()){
                case id+"Home": mainPanelUpdate.setBackground(ComponentState.NORMAL, new Color(108, 154, 51)); break;
                case id+"Skills": mainPanelUpdate.setBackground(ComponentState.NORMAL, new Color(38, 91, 106)); break;
                case id+"Money": mainPanelUpdate.setBackground(ComponentState.NORMAL, new Color(170, 81, 57)); break;
                case id+"Email": mainPanelUpdate.setBackground(ComponentState.NORMAL, new Color(147, 49, 87)); break;
                case id+"Friends": mainPanelUpdate.setBackground(ComponentState.NORMAL, new Color(44, 71, 112)); break;
                default: mainPanelUpdate.setVisible(false); break;
            }

            mc.updateComponent(playerUUID, mainPanelUpdate);

            //Set the last button clicked on the list
            playersLastButtonClicked.put(playerUUID, form.getInput(form.getButtonId() + "Input"));
        }
    }

    //Register the ComponentManager
    @Subscribe
    public void onServerAboutToStartEvent(ServerAboutToStartEvent event){
        addButton(Icon.HOME, id+"Home", "Home");
        addButton(Icon.BOOK, id + "Skills", "Skills");
        addButton(Icon.MONEY, id + "Money", "Money");
        addButton(Icon.EMAIL, id + "Email", "Email");
        addButton(Icon.USER, id + "Friends", "Friends");
        addButton(Icon.FACEBOOK, id + "Facebook", "Facebook", "https://www.facebook.com/");
        addButton(Icon.TWITTER, id + "Twitter", "Twitter", "https://twitter.com/");
        addButton(Icon.INSTAGRAM, id + "Instagram", "Instagram", "https://instagram.com/");
        addButton(Icon.YOUTUBE, id + "Youtube", "Youtube", "https://www.youtube.com/");
        MainController.getInstance().addComponentManager(this);
    }

    @Subscribe
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        playersMenuVisible.put(event.getUser().getUniqueId().toString(), Boolean.FALSE);
    }

    private void initComps(){
        menuPanel = new Component(ComponentType.PANEL, id+"menuListButton");

        menuPanel.getAttributes().setHeight(ComponentState.NORMAL, 1f);
        menuPanel.getAttributes().setWidth(ComponentState.NORMAL, 100);
        menuPanel.getAttributes().setBackground(ComponentState.NORMAL, colorBackground);
        menuPanel.getAttributes().setVisible(false);

        menuImage = new Component(ComponentType.IMAGE, id+"menuImage");

        menuImage.getAttributes().setXRelative(2);
        menuImage.getAttributes().setImageType(ComponentState.NORMAL, ImageType.CUSTOM);
        menuImage.getAttributes().setImageName(ComponentState.NORMAL, "menuButton.png");
        menuImage.getAttributes().setWidth(ComponentState.NORMAL, 16);
        menuImage.getAttributes().setHeight(ComponentState.NORMAL, 16);
        menuImage.getAttributes().setBackground(ComponentState.NORMAL, colorBackground);
        menuImage.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, false, true, false));
        menuImage.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        menuOpenCloseButton = new Component(ComponentType.BUTTON, id+"menuOpenCloseButton", menuImage.getId());

        menuOpenCloseButton.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        menuOpenCloseButton.getAttributes().setHeight(ComponentState.NORMAL, 1f);
        menuOpenCloseButton.getAttributes().setBackground(ComponentState.HOVER, colorHover);
        menuOpenCloseButton.getAttributes().setBackground(ComponentState.CLICK, colorClick);
        menuOpenCloseButton.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, false, true, false));
        menuOpenCloseButton.getAttributes().setPaddingSize(ComponentState.NORMAL, 2);

        menuTitle = new Component(ComponentType.PARAGRAPH, id+"menuTitle", menuPanel.getId());

        menuTitle.getAttributes().setXRelative(24);
        menuTitle.getAttributes().setYRelative(5);
        menuTitle.getAttributes().setValue("Menu");
        menuTitle.getAttributes().setFont(ComponentState.NORMAL, Font.NORMAL);
        menuTitle.getAttributes().setWidth(ComponentState.NORMAL, 76);
        menuTitle.getAttributes().setHeight(ComponentState.NORMAL, 9);
        menuTitle.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);

        menuList = new Component(ComponentType.LIST, id+"menuList", menuPanel.getId());

        menuList.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        menuList.getAttributes().setHeight(ComponentState.NORMAL, 1f);
        menuList.getAttributes().setMarginTop(17);
        menuList.getAttributes().setMarginBot(16);
        menuList.getAttributes().setBorderColor(ComponentState.NORMAL, colorClick);
        menuList.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(false, true, false, false));
        menuList.getAttributes().setBorderSize(ComponentState.NORMAL, 1);

        menuListButtonNext = new Attributes(menuList.getId()+"NextList");

        menuListButtonNext.setPosition(Position.BOTTOM_RIGHT);
        menuListButtonNext.setXRelative(-1f);
        menuListButtonNext.setValue(">>");
        menuListButtonNext.setWidth(ComponentState.NORMAL, .5f);
        menuListButtonNext.setHeight(ComponentState.NORMAL, 10);
        menuListButtonNext.setBackground(ComponentState.NORMAL, colorBackground);
        menuListButtonNext.setBackground(ComponentState.HOVER, colorHover);
        menuListButtonNext.setBackground(ComponentState.CLICK, colorClick);
        menuListButtonNext.setPaddingSide(ComponentState.NORMAL, new Side(false, true, false, false));
        menuListButtonNext.setPaddingSize(ComponentState.NORMAL, 2);

        menuListButtonPrevious = new Attributes(menuList.getId()+"PreviousList");

        menuListButtonPrevious.setPosition(Position.BOTTOM_LEFT);
        menuListButtonPrevious.setWidth(ComponentState.NORMAL, .5f);
        menuListButtonPrevious.setHeight(ComponentState.NORMAL, 10);
        menuListButtonPrevious.setValue("<<");
        menuListButtonPrevious.setPaddingSide(ComponentState.NORMAL, new Side(false, true, false, false));
        menuListButtonPrevious.setPaddingSize(ComponentState.NORMAL, 2);
        menuListButtonPrevious.setBackground(ComponentState.NORMAL, colorBackground);
        menuListButtonPrevious.setBackground(ComponentState.HOVER, colorHover);
        menuListButtonPrevious.setBackground(ComponentState.CLICK, colorClick);

        mainPanel = new Component(ComponentType.PANEL, id+"MainPanel");

        mainPanel.getAttributes().setWidth(ComponentState.NORMAL, 180);
        mainPanel.getAttributes().setHeight(ComponentState.NORMAL, 200);
        mainPanel.getAttributes().setPosition(Position.MIDDLE);
        mainPanel.getAttributes().setXRelative(-.5f);
        mainPanel.getAttributes().setYRelative(-.5f);
        mainPanel.getAttributes().setVisible(false);

        mainPanelCloseButton = new Component(ComponentType.BUTTON, id+"mainPanelCloseButton", mainPanel.getId());

        mainPanelCloseButton.getAttributes().setHeight(ComponentState.NORMAL, 9);
        mainPanelCloseButton.getAttributes().setWidth(ComponentState.NORMAL, 8);
        mainPanelCloseButton.getAttributes().setValue("x");
        mainPanelCloseButton.getAttributes().setXRelative(1);
        mainPanelCloseButton.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
        mainPanelCloseButton.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);
        mainPanelCloseButton.getAttributes().setBackground(ComponentState.NORMAL, new Color(240, 0, 0, 255));
        mainPanelCloseButton.getAttributes().setBackground(ComponentState.CLICK, new Color(200, 0, 0, 255));
        mainPanelCloseButton.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, false, false, false));
        mainPanelCloseButton.getAttributes().setPaddingSize(ComponentState.NORMAL, 1);
    }

    public enum Icon {
        FACEBOOK("iconFacebook"),
        TWITTER("iconTwitter"),
        YOUTUBE("iconYoutube"),
        BOOK("iconBook"),
        EMAIL("iconEmail"),
        HOME("iconHome"),
        INSTAGRAM("iconInstagram"),
        MONEY("iconMoney"),
        USER("iconUser");

        private final String name;

        Icon(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    private class Button{

        private Component line;
        private Component panel;
        private Component image;
        private Component text;
        private Component button;
        private Component input;

        public Button(Icon icon, String buttonId, String value, String url) {
            panel = new Component(ComponentType.PANEL, buttonId+"Panel", menuList.getId());

            panel.getAttributes().setWidth(ComponentState.NORMAL, 1f);
            panel.getAttributes().setHeight(ComponentState.NORMAL, 32);
            panel.getAttributes().setMarginLeft(4);
            panel.getAttributes().setBorderColor(ComponentState.NORMAL, Color.WHITE);
            panel.getAttributes().setBorderSide(ComponentState.NORMAL, new Side(true, false, false, false));

            image = new Component(ComponentType.IMAGE, buttonId+"Image", panel.getId());

            image.getAttributes().setXRelative(8);
            image.getAttributes().setYRelative(8);
            image.getAttributes().setWidth(ComponentState.NORMAL, 16);
            image.getAttributes().setHeight(ComponentState.NORMAL, 16);
            image.getAttributes().setPaddingSide(ComponentState.NORMAL, new Side(true, true, true, true));
            image.getAttributes().setPaddingSize(ComponentState.NORMAL, 8);
            image.getAttributes().setImageType(ComponentState.NORMAL, ImageType.CUSTOM);
            image.getAttributes().setImageName(ComponentState.NORMAL, icon.getName()+".png");

            text = new Component(ComponentType.PARAGRAPH, buttonId+"Text", panel.getId());

            text.getAttributes().setValue(value);
            text.getAttributes().setBackground(ComponentState.NORMAL, colorBackground);
            text.getAttributes().setTextAlignment(ComponentState.NORMAL, TextAlignment.MIDDLE);
            text.getAttributes().setTextColor(ComponentState.NORMAL, Color.WHITE);
            text.getAttributes().setWidth(ComponentState.NORMAL, 1f);
            text.getAttributes().setHeight(ComponentState.NORMAL, 1f);
            text.getAttributes().setMarginLeft(32);
            text.getAttributes().setMarginTop(1);

            input = new Component(ComponentType.INPUT_INVISIBLE, buttonId+"Input", panel.getId());

            input.getAttributes().setValue(panel.getId());

            if(url.equals(""))
                button = new Component(ComponentType.BUTTON, buttonId, panel.getId());
            else{
                button = new Component(ComponentType.BUTTON_URL, buttonId, panel.getId());
                button.getAttributes().setURL(url);
            }

            button.getAttributes().setWidth(ComponentState.NORMAL, 1f);
            button.getAttributes().setHeight(ComponentState.NORMAL, 1f);
            button.getAttributes().setMarginLeft(-4);
            button.getAttributes().setBackground(ComponentState.HOVER, colorHover);
            button.getAttributes().setBackground(ComponentState.CLICK, colorClick);
            button.getAttributes().setBorderColor(ComponentState.NORMAL, colorClick);

            button.getAttributes().addInput(input.getId());

            addComponentIdToListen(button.getId());

            line = new Component(ComponentType.PANEL, buttonId+"Line", menuList.getId());

            line.getAttributes().setBackground(ComponentState.NORMAL, colorHover);
            line.getAttributes().setHeight(ComponentState.NORMAL, 1);
            line.getAttributes().setWidth(ComponentState.NORMAL, 1f);
        }

    }

}
