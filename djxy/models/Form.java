package djxy.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;

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

    public String getInput(String inputId){
        return inputs.get(inputId);
    }
    
    private void initForm(JSONObject obj){
        JSONArray array = (JSONArray) obj.get("Inputs");

        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;

            inputs.put((String) jsonObject.get("Id"), (String) jsonObject.get("Value"));
        }
    }

    @Override
    public String toString(){
        return inputs.toString();
    }

}
