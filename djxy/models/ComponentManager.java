package djxy.models;

import djxy.controllers.MainController;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ComponentManager {
    
    private final boolean playerNeedAuthentication;
    private final ArrayList<String> listOfComponentIdToListen;
    private final HashMap<String, String> imagesToDownload;

    /**
     * Called when the player has been authenticated or when he reset his screen.
     *
     * @param playerUUID The player to init the his screen
     */
    public abstract void initPlayerGUI(String playerUUID);

    /**
     * Called when a player click on a button you are listening.
     *
     * @param playerUUID The player who send the form
     * @param form The form received
     */
    public abstract void receiveForm(String playerUUID, Form form);
    
    public ComponentManager(boolean needPlayerAuthentication) {
        this.playerNeedAuthentication = needPlayerAuthentication;
        listOfComponentIdToListen = new ArrayList<>();
        imagesToDownload = new HashMap<>();
    }


    public boolean isPlayerNeedAuthentication() {
        return playerNeedAuthentication;
    }

    public ArrayList<String> getListOfComponentIdToListen() {
        return listOfComponentIdToListen;
    }

    public HashMap<String, String> getImagesToDownload() {
        return imagesToDownload;
    }
    
    public void addComponentIdToListen(String componentId){
        listOfComponentIdToListen.add(componentId);
    }
    
    public void addImageToDownload(String url, String name){
        imagesToDownload.put(url, name);
    }
    
}
