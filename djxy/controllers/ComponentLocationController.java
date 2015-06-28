package djxy.controllers;

import djxy.models.component.Attributes;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public final class ComponentLocationController {
    
    private static final String FILE = "/LocationsRelative.json";
    private final HashMap<String, HashMap<String, LocationRelative>> playersComponentLocationRelatives;

    public ComponentLocationController() {
        this.playersComponentLocationRelatives = new HashMap<>();
    }
    
    public void setComponentLocationRelative(String playerUUID, String componentId, int xRelative, int yRelative){
        HashMap<String, LocationRelative> locationRelatives = playersComponentLocationRelatives.get(playerUUID);
        
        if(locationRelatives == null){
            playersComponentLocationRelatives.put(playerUUID, new HashMap<String, LocationRelative>());
            locationRelatives = playersComponentLocationRelatives.get(playerUUID);
        }
        
        locationRelatives.put(componentId.toLowerCase(), new LocationRelative(xRelative, yRelative));
    }
    
    public void setComponentLocation(String playerUUID, Attributes attributes){
        HashMap<String, LocationRelative> locationRelatives = playersComponentLocationRelatives.get(playerUUID);
        
        if(locationRelatives != null){
            LocationRelative locationRelative = locationRelatives.get(attributes.getId().toLowerCase());
            
            if(locationRelative != null){
                attributes.setXRelative(locationRelative.xRelative);
                attributes.setYRelative(locationRelative.yRelative);
            }
        }
    }
    
    public void load(){
        try {
            File file = new File(MainController.PATH+FILE);
            
            if(!file.exists()){
                file.createNewFile();
                return;
            }
            
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new FileReader(file));
            
            for(Object obj : array){
                JSONObject object = (JSONObject) obj;
                setComponentLocationRelative((String) object.get("PlayerUUID"), (String) object.get("ComponentId"), (int) (long) object.get("XRelative"), (int) (long) object.get("YRelative"));
            }
        } catch (Exception ex) {}
    }
    
    public void save(){
        try {
            File file = new File(MainController.PATH+FILE);
            
            if(!file.exists())
                file.createNewFile();
            
            FileWriter fw = new FileWriter(file);
            
            JSONArray array = new JSONArray();
            
            for(Map.Entry pairs : playersComponentLocationRelatives.entrySet()){
                String playerUUID = (String) pairs.getKey();
                HashMap<String, LocationRelative> locationsRelative = (HashMap<String, LocationRelative>) pairs.getValue();
                
                for(Map.Entry pairs2 : locationsRelative.entrySet()){
                    JSONObject object = new JSONObject();
                    String componentId = (String) pairs2.getKey();
                    LocationRelative locationRelative = (LocationRelative) pairs2.getValue();
                    
                    object.put("PlayerUUID", playerUUID);
                    object.put("ComponentId", componentId);
                    object.put("XRelative", locationRelative.xRelative);
                    object.put("YRelative", locationRelative.yRelative);
                    array.add(object);
                }
            }
            
            fw.write(array.toJSONString());
            fw.close();
            fw.flush();
        }catch(Exception e){}
    }
    
    private class LocationRelative{
        
        private final int xRelative;
        private final int yRelative;

        public LocationRelative(int xRelative, int yRelative) {
            this.xRelative = xRelative;
            this.yRelative = yRelative;
        }
    }
    
}
