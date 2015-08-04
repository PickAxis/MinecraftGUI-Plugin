package djxy.models;

public interface ComponentManager {

    /**
     * Called when the player has been authenticated or when he reset his screen.
     *
     * @param playerUUID The player to init the his screen
     */
    void initPlayerGUI(String playerUUID);

    /**
     * Called when a player click on a button you are listening.
     *
     * @param playerUUID The player who send the form
     * @param form The form received
     */
    void receiveForm(String playerUUID, Form form);
    
}
