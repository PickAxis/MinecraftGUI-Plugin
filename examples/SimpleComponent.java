package examples;

import djxy.controllers.MainController;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.Attributes;
import djxy.models.component.Component;
import djxy.models.component.ComponentState;
import djxy.models.component.ComponentType;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;

import java.awt.*;

public class SimpleComponent extends ComponentManager {

    private Component panel;

    public SimpleComponent() {
        super(false);//The plugin don't require the authentication of the player.
        initPanel();
    }

    @Override
    public void initPlayerGUI(String playerUUID) {
        //This function will create the panel on the player screen
        MainController.createComponent(playerUUID, panel);

        Attributes attributes = new Attributes(panel.getId());

        attributes.setBackground(ComponentState.CLICK, Color.white);

        //This function will send the modification of the panel to the player
        MainController.updateComponent(playerUUID, attributes);
    }

    @Override
    public void receiveForm(String playerUUID, Form form) {}

    @Subscribe
    public void onServerAboutToStartEvent(ServerAboutToStartEvent event){
        //When the Sponge called this event, you need to register your ComponentManager for receive the events of MinecraftGUI
        MainController.addComponentManager(this);
    }

    //This function will create the component and set his attributes
    private void initPanel(){
        panel = new Component(ComponentType.PANEL, "simplePanel");

        /* The component state is the current state of the component with the mouse
         * Normal: the mouse is not on the component.
         * Hover: The mouse is hover the component.
         * Click: Ths mouse is hover the component and the left button of the mouse is pressed.
         * 
         * When the attribute has a state, the value of this component will change depending the state of the component.
         */
        panel.getAttributes().setWidth(ComponentState.NORMAL, 200);
        panel.getAttributes().setHeight(ComponentState.NORMAL, 200);
        panel.getAttributes().setBackground(ComponentState.NORMAL, Color.orange);
        panel.getAttributes().setBackground(ComponentState.HOVER, Color.pink);
    }

}
