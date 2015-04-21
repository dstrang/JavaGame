package networking;

import game.AwesomeGame;
import graphicslib3D.Vector3D;
import interfaces.GhostAvatar;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import sage.networking.IGameConnection.ProtocolType;
import sage.networking.client.GameConnectionClient;

public class GameClient extends GameConnectionClient {
	private AwesomeGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;

	public GameClient(InetAddress remAddr, int remPort, ProtocolType pType, AwesomeGame game) throws IOException {
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}

	protected void processPacket(Object o) {
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if (msgTokens[0].compareTo("join") == 0) {
			if (msgTokens[1].compareTo("success") == 0) {
				game.setIsConnected(true);
				sendCreateMessage(game.getPlayerPosition()); 
			}
			if (msgTokens[1].compareTo("failure") == 0)
				game.setIsConnected(false);
		}
		if (msgTokens[0].compareTo("bye") == 0) {
			UUID ghostID = UUID.fromString(msgTokens[1]);
			removeGhostAvatar(ghostID);
		}
		if (msgTokens[0].compareTo("dsfr") == 0) {
			UUID ghostID = UUID.fromString(msgTokens[1]);
//			createGhostAvatar(ghostID, ghostPosition);
			createGhostAvatar(ghostID);
		}
		if (msgTokens[0].compareTo("wsds") == 0) {
		}

		if (msgTokens[0].compareTo("wsds") == 0) {
		}

		if (msgTokens[0].compareTo("move") == 0) {
		}

	}
	
	private void createGhostAvatar(UUID ghostID) {
		
	}

	private void removeGhostAvatar(UUID ghostID) {
		
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

	public void sendJoinMessage() {
		try {
			sendPacket(new String("join," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendByeMessage() {
	}

	public void sendDetailsForMessage(UUID remId, Vector3D pos) {
	}

	public void sendMoveMessage(Vector3D pos) {
	}

}
