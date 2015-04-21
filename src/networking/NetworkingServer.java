package networking;

import java.io.IOException;

public class NetworkingServer {
	
	private static GameServer server;

	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);
		server = new GameServer(port);
	}
	
}
