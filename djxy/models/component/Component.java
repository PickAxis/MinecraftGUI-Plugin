package djxy.models.component;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Component {
    
    private final String id;
    private final String parentId;
    private final ComponentType type;
    private final JSONObject createCommand;
    private final Attributes attributes;

    public Component(ComponentType type, String id, String parentId) {
        this.id = id;
        this.parentId = parentId;
        this.type = type;
        this.attributes = new Attributes(id);
        createCommand = new JSONObject();
        
        initComponentCreateCommand();
    }
    
    public Component(ComponentType type, String id) {
        this.id = id;
        this.parentId = "@ROOT";
        this.type = type;
        this.attributes = new Attributes(id);
        createCommand = new JSONObject();
        
        initComponentCreateCommand();
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public String getId() {
        return id;
    }
    
    private void initComponentCreateCommand(){
        createCommand.put("Command", "CREATE COMPONENT");
        createCommand.put("ComponentId", id);
        createCommand.put("ParentId", parentId);
        createCommand.put("Type", type.name());
    }
    
    public JSONArray getCommands(){
        JSONArray commands = new JSONArray();
        
        commands.add(createCommand);
        commands.addAll(attributes.getCommands());
        
        return commands;
    }
    
}
