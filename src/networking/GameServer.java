package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class GameServer extends GameConnectionServer<UUID> {
	
	public GameServer(int localPort) throws IOException {
		super(localPort, ProtocolType.TCP);
		System.out.println("Listening on port: " + localPort);
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
			
			UUID clientID = UUID.fromString(msgTokens[1]);
			System.out.println(msgTokens[0]);

			switch(msgTokens[0]){
				case "create":
					String[] createPos = { msgTokens[2], msgTokens[3], msgTokens[4] };
					sendCreateMessages(clientID, createPos);
					break;
				case "move":
					String[] movePos = { msgTokens[2], msgTokens[3], msgTokens[4] };
					sendMoveMessages(clientID, movePos);
					break;
				case "dsfr":
					UUID remoteID = UUID.fromString(msgTokens[2]);
					String[] detailsPos = { msgTokens[3], msgTokens[4], msgTokens[5] };
					sendDetailsMessage(clientID, remoteID, detailsPos);
					break;
				case "wsds":
					sendWantsDetailsMessages(clientID);
					break;
				case "bye":
					sendByeMessages(clientID);
					break;
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
			message += "," + Float.parseFloat(position[0]);
			message += "," + Float.parseFloat(position[1]);
			message += "," + Float.parseFloat(position[2]);
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMoveMessages(UUID clientID, String[] position) {
		try {
			String message = new String("move," + clientID.toString());
			message += "," + Float.parseFloat(position[0]);
			message += "," + Float.parseFloat(position[1]);
			message += "," + Float.parseFloat(position[2]);
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendDetailsMessage(UUID clientID, UUID remoteId, String[] position) {
		try {
			String message = new String("dsfr," + clientID.toString());
			message += "," + Float.parseFloat(position[0]);
			message += "," + Float.parseFloat(position[1]);
			message += "," + Float.parseFloat(position[2]);
			sendPacket(message, remoteId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendWantsDetailsMessages(UUID clientID) {
		try {
			String message = new String("wsds," + clientID.toString());
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendByeMessages(UUID clientID) {
		try {
			String message = new String("bye," + clientID.toString());
			forwardPacketToAll(message, clientID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		removeClient(clientID);
	}

}
