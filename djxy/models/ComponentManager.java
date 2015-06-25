package djxy.models;

import djxy.controllers.MainController;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ComponentManager {
    
    private final boolean playerNeedAuthentication;
    private final ArrayList<String> listOfComponentIdToListen;
    private final HashMap<String, String> imagesToDownload;

    public abstract void initPlayerGUI(MainController mainController, String playerUUID);
    public abstract void receiveForm(MainController mainController, String playerUUID, Form form);
    
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
