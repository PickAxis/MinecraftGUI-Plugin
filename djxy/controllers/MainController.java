package djxy.controllers;

import djxy.controllers.NetworkController.PlayerConnection;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.Attributes;
import djxy.models.component.Component;
import djxy.views.AuthenticationManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class MainController {

    protected static final String PATH = "mods/MinecraftGUI";
    private static MainController instance = null;

    /**
     * You need to use this method to register your ComponentManager.
     *
     * @param manager The ComponentManager you are using to receive the events from MinecraftGUI.
     */
    public static void addComponentManager(ComponentManager manager){
        if(instance.canAddComponentManager){
            instance.playerNeedAuthentication = instance.playerNeedAuthentication == false?manager.isPlayerNeedAuthentication():true;

            instance.componentManagers.add(manager);

            for(String componentId : manager.getListOfComponentIdToListen()){
                ArrayList<ComponentManager> componentManagers = instance.componentManagersPerComponent.get(componentId);

                if(componentManagers == null){
                    instance.componentManagersPerComponent.put(componentId, new ArrayList<ComponentManager>());
                    componentManagers = instance.componentManagersPerComponent.get(componentId);
                }

                componentManagers.add(manager);
            }
        }
    }

    /**
     * This method will create one component on the screen of the player
     *
     * @param playerUUID The player to send the component
     * @param component The component to create
     */
    public static void createComponent(String playerUUID, Component component){
        Boolean auth = instance.playersAuthenticated.get(playerUUID);

        if(auth != null && auth == true){
            instance.componentLocationController.setComponentLocation(playerUUID, component.getAttributes());
            instance.networkController.sendCommandTo(playerUUID, component.getCommands());
        }
    }

    /**
     * This method will change properties of one component on the screen of the player
     *
     * @param playerUUID The player to send the update
     * @param attributes The attributes to send
     */
    public static void updateComponent(String playerUUID, Attributes attributes){
        Boolean auth = instance.playersAuthenticated.get(playerUUID);

        if(auth != null && auth == true){
            instance.componentLocationController.setComponentLocation(playerUUID, attributes);
            instance.networkController.sendCommandTo(playerUUID, attributes.getCommands());
        }
    }

    /**
     * This method will remove a component of the player's screen
     *
     * @param playerUUID The player to remove the component
     * @param componentId The id of the component to remove
     */
    public static void removeComponent(String playerUUID, String componentId){
        Boolean auth = instance.playersAuthenticated.get(playerUUID);

        if(auth != null && auth == true)
            instance.networkController.sendCommandTo(playerUUID, instance.createCommandRemoveComponent(componentId));
    }

    //**************************************************************************
    //**************************************************************************
    
    private final ArrayList<ComponentManager> componentManagers;
    private final HashMap<String, ArrayList<ComponentManager>> componentManagersPerComponent;
    private final HashMap<String, Boolean> playersAuthenticated;
    private final NetworkController networkController;
    private final ComponentLocationController componentLocationController;
    private final AuthenticationManager authenticationManager;
    private boolean canAddComponentManager = false;
    private boolean playerNeedAuthentication = false;

    public MainController() throws Exception {
        if(instance != null)
            throw new Exception();
        
        instance = this;
        playersAuthenticated = new HashMap<>();
        componentManagers = new ArrayList<>();
        componentManagersPerComponent = new HashMap<>();
        authenticationManager = new AuthenticationManager(this);
        networkController = new NetworkController(this, 20000);
        componentLocationController = new ComponentLocationController();
    }

    public void sendCommandsTo(String playerUUID, JSONArray commands){
        networkController.sendCommandTo(playerUUID, commands);
    }

    public void setPlayersAuthenticated(String playerUUID){
        playersAuthenticated.put(playerUUID, true);
        callInitPlayerGUIEvent(networkController.getPlayerConnection(playerUUID));
    }

    public void closePlayerConnection(String playerUUID){
        networkController.closePlayer(playerUUID);
    }

    private void clearPlayerScreen(String playerUUID){
        networkController.sendCommandTo(playerUUID, createCommandClearScreen());
    }

    protected void newPlayerConnected(PlayerConnection playerConnection){
        playersAuthenticated.put(playerConnection.getPlayerUUID(), false);

        if(playerNeedAuthentication)
            authenticationManager.initPlayerGUI(playerConnection.getPlayerUUID());
        else{
            playersAuthenticated.put(playerConnection.getPlayerUUID(), true);
            callInitPlayerGUIEvent(playerConnection);
        }
    }

    protected void receiveCommand(PlayerConnection playerConnection, JSONObject object){
        String command[] = ((String) object.get("Command")).split(" ");
        
        if(command.length == 2){
            if(command[0].equals("FORM")){
                if(command[1].equals("INPUT")){
                    if(playersAuthenticated.get(playerConnection.getPlayerUUID()).equals(Boolean.TRUE))
                        callReceiveInputFormEvent(playerConnection, object);
                    else
                        authenticationManager.receiveForm(playerConnection.getPlayerUUID(), new Form(object));
                }
            }
            else if(command[0].equals("SET")){
                if(command[1].equals("LOCATION_RELATIVE")){
                    setComponentLocationRelative(playerConnection.getPlayerUUID(), object);
                }
            }
            else if(command[0].equals("SCREEN")){
                if(command[1].equals("CLEAR")){
                    
                }
            }
        }
    }
    
    private void setComponentLocationRelative(String playerUUID, JSONObject object){
        String componentId = (String) object.get("ComponentId");
        int x = (int) (long) object.get("XRelative");
        int y = (int) (long) object.get("YRelative");
        
        componentLocationController.setComponentLocationRelative(playerUUID, componentId, x, y);
    }

    private void callInitPlayerGUIEvent(PlayerConnection playerConnection){
        for(ComponentManager manager : componentManagers){
            networkController.sendCommandTo(playerConnection.getPlayerUUID(), createCommandsDownloadImage(manager));
            manager.initPlayerGUI(playerConnection.getPlayerUUID());
        }
    }
    
    private void callReceiveInputFormEvent(PlayerConnection playerConnection, JSONObject object){
        String buttonId = (String) object.get("ButtonId");
        ArrayList<ComponentManager> managers = componentManagersPerComponent.get(buttonId);
        
        if(managers != null){
            Form form = new Form(object);
            
            for(ComponentManager manager : managers)
                manager.receiveForm(playerConnection.getPlayerUUID(), form);
        }
    }

    public void serverInit(){
        new File(PATH).mkdirs();
        componentLocationController.load();
        canAddComponentManager = true;
    }

    public void serverIsStarting(){
        canAddComponentManager = false;
        System.out.println(componentManagers.size());
        networkController.start();
    }

    public void serverIsStopping(){
        networkController.closeServer();
        componentLocationController.save();
    }

    public void playerJoin(String playerUUID){
        networkController.addPlayerConnected(playerUUID);
    }

    public void playerQuit(String playerUUID){
        networkController.closePlayer(playerUUID);
    }
    
    private JSONArray createCommandClearScreen(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("Command", "CLEAR SCREEN");
        
        array.add(object);
        
        return array;
    }

    private JSONArray createCommandsDownloadImage(ComponentManager manager){
        JSONArray array = new JSONArray();

        for(Map.Entry pairs : manager.getImagesToDownload().entrySet()){
            JSONObject object = new JSONObject();

            object.put("Command", "DOWNLOAD IMAGE");
            object.put("Url", pairs.getKey());
            object.put("File", pairs.getValue());

            array.add(object);
        }

        return array;
    }

    private JSONArray createCommandsDownloadImage(String url, String imageFileName){
        JSONArray array = new JSONArray();

        JSONObject object = new JSONObject();

        object.put("Command", "DOWNLOAD IMAGE");
        object.put("Url", url);
        object.put("File", imageFileName);

        array.add(object);

        return array;
    }
    
    public JSONArray createCommandRemoveComponent(String componentId){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("Command", "REMOVE COMPONENT");
        object.put("ComponentId", componentId);
        
        array.add(object);
        
        return array;
    }
    
}
