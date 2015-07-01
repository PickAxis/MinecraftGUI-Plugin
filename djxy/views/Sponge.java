package djxy.views;

import djxy.controllers.MainController;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerQuitEvent;
import org.spongepowered.api.event.state.ConstructionEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "MinecraftGUIServer", name = "Minecraft GUI Server", version = "1.0")
public class Sponge {

    private MainController mainController;

    public Sponge() {
        try {
            this.mainController = new MainController();
        } catch (Exception e) {}
    }

    @Subscribe
    protected void onConstructionEvent(ConstructionEvent event) {
        mainController.serverInit();
    }

    @Subscribe
    protected void onServerStartingEvent(ServerStartingEvent event) {
        mainController.serverIsStarting();
    }

    @Subscribe
    protected void onServerStoppingEvent(ServerStoppingEvent event) {
        mainController.serverIsStopping();
    }

    @Subscribe
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        mainController.playerJoin(event.getUser().getUniqueId().toString());
    }

    @Subscribe
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        mainController.playerQuit(event.getUser().getUniqueId().toString());
    }

}
