/*
 *     Minecraft GUI Server
 *     Copyright (C) 2015  Samuel Marchildon-Lavoie
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package djxy.views;

import djxy.controllers.MainController;
import djxy.models.PluginInterface;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Bukkit extends JavaPlugin implements PluginInterface {

    private MainController mainController;

    public Bukkit() {
        try {
            this.mainController = new MainController(this);
        } catch (Exception e) {}
    }

    public void onEnable() {
        getCommand("gui-change").setExecutor(new CommandGuiConnectionState());
        getCommand("gui-reset").setExecutor(new CommandGuiResetLocation());
        getCommand("gui-reload").setExecutor(new CommandGuiReload());
        mainController.serverInit();
        mainController.serverIsStarting();
    }

    public void onDisable() {
        mainController.serverIsStopping();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        mainController.playerJoin(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        mainController.playerQuit(event.getPlayer().getUniqueId().toString());
    }

    @Override
    public void sendAuthenticationCode(String playerUUID, String code) {
        try {
            Player player = getServer().getPlayer(UUID.fromString(playerUUID));

            player.sendMessage(ChatColor.GREEN + "Your authentication code: " + ChatColor.RED + code + ChatColor.GREEN + ".");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void screenLoaded(String playerUUID) {
        try {
            Player player = getServer().getPlayer(UUID.fromString(playerUUID));

            player.sendMessage(ChatColor.GREEN + "Your screen is loaded.");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class CommandGuiReload implements CommandExecutor{
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String playerUUID = player.getUniqueId().toString();

                if(mainController.isPlayerAuthenticated(playerUUID))
                    mainController.reloadPlayerScreen(playerUUID);
            }
            return true;
        }
    }

    private class CommandGuiResetLocation implements CommandExecutor{
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String playerUUID = player.getUniqueId().toString();

                if(mainController.isPlayerAuthenticated(playerUUID)) {
                    mainController.resetPlayerComponentLocation(playerUUID);
                    sender.sendMessage(ChatColor.GREEN + "Your screen has been reset.");
                }
            }
            return true;
        }
    }

    private class CommandGuiConnectionState implements CommandExecutor{
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                String playerUUID = player.getUniqueId().toString();

                if(mainController.isPlayerAuthenticated(playerUUID)) {
                    String connectionState = mainController.changePlayerConnectionState(playerUUID) == true ? "on" : "off";

                    sender.sendMessage(ChatColor.GREEN + "Connection state changed on " + ChatColor.RED + connectionState + ChatColor.GREEN + ".");
                }
            }
            return true;
        }
    }
}
