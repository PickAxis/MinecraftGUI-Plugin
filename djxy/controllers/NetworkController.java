package djxy.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class NetworkController {
    
    private final HashMap<String, Boolean> playersConnected;//List of all the player connected on the server and the boolean indicate if he is connected with the client
    private final HashMap<String, PlayerConnection> playerConnections;
    private final ServerConnection serverConnection;
    private final MainController mainController;

    protected NetworkController(MainController mainController, int port) {
        this.mainController = mainController;
        this.playersConnected = new HashMap<>();
        playerConnections = new HashMap<>();
        serverConnection = new ServerConnection(port);
    }
    
    public void start(){
        serverConnection.start();
    }

    public boolean changePlayerConnectionState(String playerUUID){
        PlayerConnection playerConnection = getPlayerConnection(playerUUID);

        if(playerConnection.canSendCommand == true){
            sendCommandTo(playerUUID, mainController.createCommandClearScreen());
            playerConnection.canSendCommand = false;
            return false;
        }
        else{
            playerConnection.canSendCommand = true;
            mainController.callInitPlayerGUIEvent(playerConnection);
            return true;
        }
    }
    
    public void addPlayerConnected(String playerUUID){
        playersConnected.put(playerUUID, false);
    }
    
    protected PlayerConnection getPlayerConnection(String playerUUID){
        return playerConnections.get(playerUUID);
    }
    
    protected void sendCommandTo(String playerUUID, JSONArray array){
        PlayerConnection playerConnection = playerConnections.get(playerUUID);
        
        if(playerConnection != null && playerConnection.canSendCommand)
            playerConnection.sendCommand(array.toJSONString());
    }
    
    private void addPlayerConnection(PlayerConnection playerConnection){
        Boolean playerConnected = playersConnected.get(playerConnection.playerUUID);

        if(playerConnected != null && !playerConnected){
            playerConnections.put(playerConnection.playerUUID, playerConnection);
            mainController.newPlayerConnected(playerConnection);
        }
        else
            playerConnection.close();
    }
    
    protected void closeServer(){
        for(PlayerConnection playerConnection : playerConnections.values()){
            playerConnection.close();
            playersConnected.remove(playerConnection.playerUUID);
        }
        
        playerConnections.clear();
        
        serverConnection.close();
    }
    
    protected void closePlayer(String playerUUID){
        PlayerConnection playerConnection = playerConnections.get(playerUUID);
        
        if(playerConnection != null){
            playerConnection.close();
            playersConnected.remove(playerUUID);
            playerConnections.remove(playerUUID);
        }
    }
    
    private class ServerConnection{
        
        private final List<PlayerConnection> playerConnectionsWaitingUUID;
        private ServerSocket serverSocket;
        private int port;
        
        public ServerConnection(int port){
            playerConnectionsWaitingUUID = Collections.synchronizedList(new ArrayList<PlayerConnection>());
            try {
                this.port = port;
                serverSocket = new ServerSocket();
            } catch (IOException ex) {}
        }
        
        private void start(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverSocket.bind(new InetSocketAddress(port));
                        
                        System.out.println("Server listening!");
                        startCleaningThread();
                        
                        while(true){
                            PlayerConnection playerConnection = new PlayerConnection(serverSocket.accept());
                            
                            playerConnectionsWaitingUUID.add(playerConnection);
                            
                            new Thread(playerConnection).start();
                        }
                        
                    } catch (Exception ex) {}
                }
            }, "Server MinecraftGUI").start();
        }
        
        private void close(){
            try{
                System.out.println("Server closing!");
                serverSocket.close();
            }catch(Exception e){}
        }
        
        private void startCleaningThread(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!serverSocket.isClosed()){
                        ArrayList<PlayerConnection> clone = new ArrayList<>(playerConnectionsWaitingUUID);
                        long time = System.currentTimeMillis();
                        int connectionClosed = 0;
                        
                        for(PlayerConnection playerConnection : clone){
                            if(time - playerConnection.timeCreated > 5000){
                                playerConnectionsWaitingUUID.remove(playerConnection);

                                if(playerConnection.playerUUID.equals("")){
                                    playerConnection.close();
                                    connectionClosed++;
                                }
                            }
                        }
                        
                        System.out.println("There was "+clone.size()+" connection waiting UUID and "+connectionClosed+" have been closed.");
                        System.out.println("Currently there is "+playerConnections.size()+" player connected.");
                        
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {}
                    }
                }
            }).start();
        }
        
    }
    
    protected class PlayerConnection implements Runnable{
        
        private final long timeCreated;
        private final Socket socket;
        private boolean canSendCommand = true;
        private BufferedReader in;
        private DataOutputStream out;
        private String playerUUID = "";

        public PlayerConnection(Socket socket) {
            this.timeCreated = System.currentTimeMillis();
            this.socket = socket;
            try {
                System.out.println("New player connection!");
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {}
        }

        public String getPlayerUUID() {
            return playerUUID;
        }

        private void sendCommand(String command){
            try {
                out.writeBytes(command+"\n");
            } catch (Exception e) {
                closePlayer(playerUUID);
            }
        }
        
        @Override
        public void run() {
            try{
                JSONParser parser = new JSONParser();
                
                while(true){
                    String command = in.readLine();

                    if(playerUUID.equals("")){
                        playerUUID = command;
                        addPlayerConnection(this);
                    }
                    else{
                        Object obj = parser.parse(command);
                        JSONArray array = (JSONArray) obj;
                        
                        for (Object jsonObject : array) 
                            mainController.receiveCommand(this, (JSONObject) jsonObject);
                    }
                    
                }
            }catch(Exception e){
                closePlayer(playerUUID);
            }
        }
        
        private void close(){
            try {
                System.out.println("Player connection closed!");
                socket.close();
            } catch (IOException ex) {}
        }
        
    }
}
