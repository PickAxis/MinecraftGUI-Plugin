package djxy.models;

import djxy.models.component.ComponentAttribute;
import djxy.models.component.ComponentState;

public class ButtonEvent {

    private String componentIdToUpdate;
    private ComponentState state;
    private ComponentAttribute componentAttribute;
    private String value;

    public ButtonEvent(String componentIdToUpdate, ComponentState state, ComponentAttribute componentAttribute, String value) {
        this.componentIdToUpdate = componentIdToUpdate;
        this.state = state;
        this.componentAttribute = componentAttribute;
        this.value = value;
    }

    public void setComponentIdToUpdate(String componentIdToUpdate) {
        this.componentIdToUpdate = componentIdToUpdate;
    }

    public void setState(ComponentState state) {
        this.state = state;
    }

    public void setComponentAttribute(ComponentAttribute componentAttribute) {
        this.componentAttribute = componentAttribute;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComponentIdToUpdate() {
        return componentIdToUpdate;
    }

    public ComponentState getState() {
        return state;
    }

    public ComponentAttribute getComponentAttribute() {
        return componentAttribute;
    }

    public String getValue() {
        return value;
    }

    public ButtonEvent clone(){
        return new ButtonEvent(this.componentIdToUpdate, this.state, this.componentAttribute, this.value);
    }
}
