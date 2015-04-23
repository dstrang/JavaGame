package networking;

import game.AwesomeGame;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import objects.Avatar;
import sage.networking.client.GameConnectionClient;

public class GameClient extends GameConnectionClient {
	private AwesomeGame game;
	private UUID id;
	private HashMap<UUID,Avatar> ghostAvatars;

	public GameClient(InetAddress remAddr, int remPort, ProtocolType pType, AwesomeGame game) throws IOException {
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new HashMap<UUID,Avatar>();
	}

	protected void processPacket(Object o) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		
		System.out.println(msgTokens[0]);
		
		if(msgTokens.length > 0){
			
			switch(msgTokens[0]){
				case "join":
					processJoin(msgTokens[1]);
					break;
				case "create":
					processCreate(msgTokens);
					break;
				case "move":
					processMove(msgTokens);
					break;
				case "dsfr":
					processDetails(msgTokens);
					break;
				case "wsds":
					sendDetailsForMessage(UUID.fromString(msgTokens[1]), game.getPlayerPosition());
					break;
				case "bye":
					removeGhostAvatar(UUID.fromString(msgTokens[1]));
					break;
			}
			
		}

	}


	private void processDetails(String[] msgTokens) {
		UUID ghostID = UUID.fromString(msgTokens[1]);
		float x = Float.parseFloat(msgTokens[2]);
		float y = Float.parseFloat(msgTokens[3]);
		float z = Float.parseFloat(msgTokens[4]);
		if(!ghostAvatars.containsKey(ghostID)){
			Avatar ghost = game.addGhostToGame(x, y, z);
			ghostAvatars.put(ghostID, ghost);
		}
	}

	private void processMove(String[] msgTokens) {
		Avatar ghost = ghostAvatars.get(UUID.fromString(msgTokens[1]));
		float x = Float.parseFloat(msgTokens[2]);
		float y = Float.parseFloat(msgTokens[3]);
		float z = Float.parseFloat(msgTokens[4]);
		if(ghost != null){
			Matrix3D translate = new Matrix3D();
			translate.translate(x, y, z);
			ghost.setLocalTranslation(translate);
		}
	}

	private void processCreate(String[] msgTokens) {
		UUID ghostID = UUID.fromString(msgTokens[1]);
		if(!ghostAvatars.containsKey(ghostID)){
			String[] ghostPos = { msgTokens[2], msgTokens[3], msgTokens[4] };
			createGhostAvatar(ghostID, ghostPos);
		}
	}

	private void processJoin(String message) {
		if("success".equals(message)){
			game.setIsConnected(true);
			sendCreateMessage(game.getPlayerPosition());
			sendWantsMessage();
		}else{
			game.setIsConnected(false);
		}
	}

	private void createGhostAvatar(UUID ghostID, String[] position) {
		float x = Float.parseFloat(position[0]);
		float y = Float.parseFloat(position[1]);
		float z = Float.parseFloat(position[2]);
		Avatar ghost = game.addGhostToGame(x, y, z);
		ghostAvatars.put(ghostID, ghost);
	}

	private void removeGhostAvatar(UUID ghostID) {
		Avatar ghost = ghostAvatars.get(ghostID);
		if(ghost != null){
			game.removeGhostFromGame(ghost);
		}
	}

	
	
	
	public void sendJoinMessage() {
		try {
			sendPacket(new String("join," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendCreateMessage(Vector3D pos) {
		try {
			String message = new String("create," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendMoveMessage(Vector3D pos) {
		try {
			String message = new String("move," + id.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDetailsForMessage(UUID remId, Vector3D pos) {
		try {
			String message = new String("dsfr," + id.toString() + "," + remId.toString());
			message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendWantsMessage() {
		try {
			sendPacket(new String("wsds," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendByeMessage() {
		try {
			sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	

}
