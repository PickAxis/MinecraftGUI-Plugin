package djxy.models;

import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Form {
    
    private final String buttonId;
    private final HashMap<String, String> inputs;

    public Form(JSONObject object) {
        this.buttonId = (String) object.get("ButtonId");
        this.inputs = new HashMap<>();
        initForm(object);
    }

    public String getButtonId() {
        return buttonId;
    }
    
    public void addInput(String id, String value){
        inputs.put(id, value);
    }

    public HashMap<String, String> getInputs() {
        return (HashMap<String, String>) inputs.clone();
    }
    
    private void initForm(JSONObject obj){
        JSONArray array = (JSONArray) obj.get("Inputs");
        
        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            
            addInput((String) jsonObject.get("Id"), (String) jsonObject.get("Value"));
        }
    }
    
}
