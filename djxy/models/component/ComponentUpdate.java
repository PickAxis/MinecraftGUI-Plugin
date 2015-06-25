package djxy.models.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ComponentUpdate {
    
    private final String id;
    private final HashMap<ComponentAttribute, HashMap<ComponentState, String>> attributesWithState;
    private final HashMap<ComponentAttribute, String> attributesWithoutState;
    private final ArrayList<String> inputs;
    
    public ComponentUpdate(String componentId) {
        this.id = componentId;
        attributesWithState = new HashMap<>();
        attributesWithoutState = new HashMap<>();
        inputs = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    
    public void setMarginLeft(int margin){
        addUpdate(ComponentAttribute.MARGIN_LEFT, margin+"");
    }
    
    public void setMarginTop(int margin){
        addUpdate(ComponentAttribute.MARGIN_TOP, margin+"");
    }
    
    public void setMarginRight(int margin){
        addUpdate(ComponentAttribute.MARGIN_RIGHT, margin+"");
    }
    
    public void setMarginBot(int margin){
        addUpdate(ComponentAttribute.MARGIN_BOT, margin+"");
    }
    
    public void setPosition(Position position){
        addUpdate(ComponentAttribute.POSITION, position.name());
    }
    
    public void setLocationFreeze(boolean freeze){
        addUpdate(ComponentAttribute.LOCATION_FREEZE, freeze+"");
    }
    
    public void setVisible(boolean visible){
        addUpdate(ComponentAttribute.VISIBLE, visible+"");
    }
    
    public void setXRelative(int x){
        addUpdate(ComponentAttribute.X_RELATIVE, x+"");
    }
    
    public void setXRelative(float x){
        addUpdate(ComponentAttribute.X_RELATIVE, x*100+"%");
    }
    
    public void setYRelative(int y){
        addUpdate(ComponentAttribute.Y_RELATIVE, y+"");
    }
    
    public void setYRelative(float y){
        addUpdate(ComponentAttribute.Y_RELATIVE, y*100+"%");
    }
    
    public void setValue(String value){
        addUpdate(ComponentAttribute.VALUE, value);
    }
    
    public void setURL(String url){
        addUpdate(ComponentAttribute.URL, url);
    }
    
    public void setHint(String hint){
        addUpdate(ComponentAttribute.HINT, hint);
    }
    
    public void setImageType(ComponentState state, ImageType image){
        addUpdate(state, ComponentAttribute.IMAGE_TYPE, image.name());
    }
    
    public void setImageName(ComponentState state, String imageName){
        addUpdate(state, ComponentAttribute.IMAGE_NAME, imageName);
    }
    
    public void setFont(ComponentState state, Font font){
        addUpdate(state, ComponentAttribute.FONT, font.name());
    }
    
    public void setWidth(ComponentState state, int width){
        addUpdate(state, ComponentAttribute.WIDTH, width+"");
    }
    
    public void setWidth(ComponentState state, float width){
        addUpdate(state, ComponentAttribute.WIDTH, width*100+"%");
    }
    
    public void setHeight(ComponentState state, int height){
        addUpdate(state, ComponentAttribute.HEIGHT, height+"");
    }
    
    public void setHeight(ComponentState state, float height){
        addUpdate(state, ComponentAttribute.HEIGHT, height*100+"%");
    }
    
    public void setPaddingSide(ComponentState state, Side side){
        addUpdate(state, ComponentAttribute.PADDING_SIDE, side.isLeft()+","+side.isTop()+","+side.isRight()+","+side.isBottom());
    }
    
    public void setPaddingSize(ComponentState state, int size){
        addUpdate(state, ComponentAttribute.PADDING_SIZE, size+"");
    }
    
    public void setBorderSide(ComponentState state, Side side){
        addUpdate(state, ComponentAttribute.BORDER_SIDE, side.isLeft()+","+side.isTop()+","+side.isRight()+","+side.isBottom());
    }
    
    public void setBorderSize(ComponentState state, int size){
        addUpdate(state, ComponentAttribute.BORDER_SIZE, size+"");
    }
    
    public void setBorderColor(ComponentState state, Color color){
        addUpdate(state, ComponentAttribute.BORDER_COLOR, color.getRed()+","+color.getGreen()+","+color.getBlue()+","+color.getAlpha());
    }
    
    public void setBackground(ComponentState state, Background background){
        addUpdate(state, ComponentAttribute.BACKGROUND, background.name());
    }
    
    public void setBackground(ComponentState state, Color color){
        addUpdate(state, ComponentAttribute.BACKGROUND, color.getRed()+","+color.getGreen()+","+color.getBlue()+","+color.getAlpha());
    }
    
    public void setTextAlignment(ComponentState state, TextAlignment textAlignment){
        addUpdate(state, ComponentAttribute.TEXT_ALIGNMENT, textAlignment.name());
    }
    
    public void setTextColor(ComponentState state, Color color){
        addUpdate(state, ComponentAttribute.TEXT_COLOR, color.getRed()+","+color.getGreen()+","+color.getBlue()+","+color.getAlpha());
    }
    
    public void setMaxTextLines(int maxLines){
        addUpdate(ComponentAttribute.MAX_TEXT_LINES, maxLines+"");
    }
    
    public void addInput(String inputId){
        inputs.add(inputId);
    }
    
    public JSONArray getCommands(){
        JSONArray commands = new JSONArray();
        
        for(String inputId : inputs)
            commands.add(createAddInputCommand(inputId));
        
        for(Map.Entry pairs : attributesWithoutState.entrySet()){
            ComponentAttribute attribute = (ComponentAttribute) pairs.getKey();
            String value = (String) pairs.getValue();
            
            commands.add(createUpdateCommand(ComponentState.NORMAL, attribute, value));
        }
        
        for(Map.Entry pairs : attributesWithState.entrySet()){
            ComponentAttribute attribute = (ComponentAttribute) pairs.getKey();
            HashMap<ComponentState, String> states = (HashMap<ComponentState, String>) pairs.getValue();
            
            for(Map.Entry pairs2 : states.entrySet())
                commands.add(createUpdateCommand((ComponentState) pairs2.getKey(), attribute, (String) pairs2.getValue()));
        }
        
        return commands;
    }
    
    private JSONObject createAddInputCommand(String inputId){
        JSONObject object = new JSONObject();
        
        object.put("Command", "SET INPUT");
        object.put("ButtonId", id);
        object.put("InputId", inputId);
        
        return object;
    }
    
    private JSONObject createUpdateCommand(ComponentState state, ComponentAttribute attribute, String value){
        JSONObject update = new JSONObject();
        
        update.put("Command", "SET COMPONENT");
        update.put("ComponentId", id);
        update.put("State", state.name());
        update.put("Attribute", attribute.name());
        update.put("Value", value);
        
        return update;
    }
    
    private void addUpdate(ComponentState state, ComponentAttribute attribute, String value){
        HashMap<ComponentState, String> states = attributesWithState.get(attribute);
        
        if(states == null){
            attributesWithState.put(attribute, new HashMap<ComponentState, String>());
            states = attributesWithState.get(attribute);
        }
        
        states.put(state, value);
    }
    
    private void addUpdate(ComponentAttribute attribute, String value){
        attributesWithoutState.put(attribute, value);
    }
}
