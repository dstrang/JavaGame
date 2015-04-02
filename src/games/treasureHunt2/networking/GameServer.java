package games.treasureHunt2.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServer extends GameConnectionServer<UUID> {
	public GameServer(int localPort) throws IOException {
		super(localPort, ProtocolType.TCP);
	}

	public void acceptClient(IClientInfo ci, Object o) {
		String message = (String) o;

		String[] messageTokens = message.split(",");
		if (messageTokens.length > 0) {
			if (messageTokens[0].compareTo("join") == 0) {
				UUID clientID = UUID.fromString(messageTokens[1]);
				addClient(ci, clientID);
				sendJoinedMessage(clientID, true);
			}
		}
	}

	public void processPacket(Object o, InetAddress senderIP, int sndPort) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if (msgTokens.length > 0) {
			if (msgTokens[0].compareTo("bye") == 0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			if (msgTokens[0].compareTo("create") == 0) {
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = { msgTokens[2], msgTokens[3], msgTokens[4] };
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
			}
			if (msgTokens[0].compareTo("dsfr") == 0) {
			}
			if (msgTokens[0].compareTo("move") == 0) {
			}
		}
	}

	public void sendJoinedMessage(UUID clientID, boolean success) {
		try {
			String message = new String("join,");
			if (success)
				message += "success";
			else
				message += "failure";
			sendPacket(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendCreateMessages(UUID clientID, String[] position) {
		try {
			String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sndDetailsMsg(UUID clientID, UUID remoteId, String[] position){
		
	}
	
	public void sendWantsDetailsMessages(UUID clientID){
		
	}
	
	public void sendMoveMessages(UUID clientID, String[] position){
		
	}
		
	public void sendByeMessages(UUID clientID){
		
	}


}
