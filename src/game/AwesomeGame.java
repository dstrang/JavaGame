package game;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import interfaces.ICollectible;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import networking.GameClient;
import networking.GameServer;
import cameras.OrbitCameraController;
import displays.FSDisplaySystem;
import events.CollectEvent;
import objects.ScoreHUD;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import utilities.Util;

public class AwesomeGame extends BaseGame {
	private IDisplaySystem display;
	private IInputManager inputManager;
	private ScriptEngineManager scriptManager;
	private ScriptEngine scriptEngine;
	private SkyBox skybox;
	private ICamera camera1;
	private IRenderer renderer;
	private IEventManager eventManager;
	private SceneNode player1;
	private ScoreHUD scoreHUD1;
	private OrbitCameraController cam1Controller;
	private Util util = new Util();
	private int player1Treasures = 0;
	private Group treasures;
	private String configFile = "config.js";
	private TerrainBlock terrain;
	private HillHeightMap heightMap;
	private static GameServer GameServer;
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private GameClient thisClient;
	private boolean serverConnected;

	public AwesomeGame(String serverAddr, int sPort) {
		super();
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.TCP;
	}

	protected void initSystem() {
		setDisplaySystem(createDisplaySystem());
		setInputManager(new InputManager());
		setGameWorld(new ArrayList<SceneNode>());
		scriptManager = new ScriptEngineManager();
		scriptEngine = scriptManager.getEngineByName("js");
	}

	protected void initGame() {

		display = this.getDisplaySystem();
		inputManager = this.getInputManager();
		eventManager = EventManager.getInstance();
		renderer = display.getRenderer();

		display.setTitle("TBD");

		createScene();
		createPlayers();
		initInput();
		initConfig();
		this.executeScript(scriptEngine, configFile);

		try {
			thisClient = new GameClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (thisClient != null) {
			thisClient.sendJoinMessage();
		}
	}

	private IDisplaySystem createDisplaySystem() {
		display = new FSDisplaySystem(800, 600, 24, 20, false, "sage.renderer.jogl.JOGLRenderer");
		System.out.println("\nWaiting for display creation...");

		int count = 0;

		while (!display.isCreated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				throw new RuntimeException("Display creation interrupted");
			}

			count++;
			System.out.print("+");
			if (count % 80 == 0) {
				System.out.println();
			}

			if (count > 2000) {
				throw new RuntimeException("Unable to create display");
			}
		}

		System.out.println();
		return display;
	}

	protected void shutdown() {
		super.shutdown();
		if (thisClient != null) {
			thisClient.sendByeMessage();
			try {
				thisClient.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		display.close();
	}

	protected void render() {
		renderer.setCamera(camera1);
		super.render();
	}

	private void createPlayers() {
		player1 = new Pyramid("PLAYER1");
		player1.scale(0.2f, 0.2f, 0.2f);
		player1.translate(0, 1, 0);
		player1.rotate(45, new Vector3D(0, 1, 0));
		addGameWorldObject(player1);
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(45, 1, 0.01, 1000);
		camera1.setViewport(0.0, 1.0, 0.0, 1.0);
		createPlayerHUDs();
	}

	private void createPlayerHUDs() {
		scoreHUD1 = new ScoreHUD(0.01, 0.06);
		camera1.addToHUD(scoreHUD1);
	}

	private void createScene() {
		// initialize skybox
		skybox = new SkyBox("Skybox", 20.0f, 20.0f, 20.0f);

		// load textures
		Texture northTexture = TextureManager.loadTexture2D("/JavaGame/src/images/jajalien1_front.jpg");
		Texture southTexture = TextureManager.loadTexture2D("/JavaGame/src/images/jajalien1_back.jpg");
		Texture eastTexture = TextureManager.loadTexture2D("/JavaGame/src/images/jajalien1_right.jpg");
		Texture westTexture = TextureManager.loadTexture2D("/JavaGame/src/images/jajalien1_left.jpg");
		Texture upTexture = TextureManager.loadTexture2D("/JavaGame/src/images/jajalien1_top.jpg");

		// attach textures to skybox
		skybox.setTexture(SkyBox.Face.North, northTexture);
		skybox.setTexture(SkyBox.Face.South, southTexture);
		skybox.setTexture(SkyBox.Face.East, eastTexture);
		skybox.setTexture(SkyBox.Face.West, westTexture);
		skybox.setTexture(SkyBox.Face.Up, upTexture);
		addGameWorldObject(skybox);
		eventManager.addListener(scoreHUD1, CollectEvent.class);

		// add axes
		Point3D origin = new Point3D(0, 0, 0);
		Point3D xEnd = new Point3D(100, 0, 0);
		Point3D yEnd = new Point3D(0, 100, 0);
		Point3D zEnd = new Point3D(0, 0, 100);
		Line xAxis = new Line(origin, xEnd, Color.red, 2);
		Line yAxis = new Line(origin, yEnd, Color.green, 2);
		Line zAxis = new Line(origin, zEnd, Color.blue, 2);
		addGameWorldObject(xAxis);
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);

		// ImageBasedHeightMap heightMap = new
		// ImageBasedHeightMap("./src/images/heightmap.jpg");
		heightMap = new HillHeightMap(129, 2000, 5.0f, 20.0f, (byte) 2, 12345);
		heightMap.setHeightScale(0.1f);
		terrain = createTerrainBlock(heightMap);
		TextureState grassState;
		Texture grassTexture = TextureManager.loadTexture2D("/JavaGame/src/images/grass.jpg");
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);

		grassState = (TextureState) renderer.createRenderState(RenderStateType.Texture);

		grassState.setTexture(grassTexture, 0);
		grassState.setEnabled(true);
		terrain.setRenderState(grassState);

		// add terrain
		// float [] heightMap = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
		// 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
		// terrain = new TerrainBlock("Terrain", 4, new Vector3D(1,1,1),
		// heightMap, heightMap);

		// terrain.setDetailTexture(1, 1);

		// terrain.setTexture(eastTexture);

		addGameWorldObject(terrain);
	}

	private TerrainBlock createTerrainBlock(AbstractHeightMap heightMap) {
		float heightScale = .005f;
		Vector3D terrainScale = new Vector3D(.2, heightScale, .2);
		int terrainSize = heightMap.getSize();
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);
		String name = "terrain" + heightMap.getClass().getSimpleName();
		TerrainBlock terrain = new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
		return terrain;

	}

	private void initInput() {
		String gamepad = inputManager.getFirstGamepadName();
		String keyboard = inputManager.getKeyboardName();
		String controller = gamepad != null ? gamepad : keyboard;

		cam1Controller = new OrbitCameraController(camera1, skybox, 225, player1, inputManager, keyboard);

		super.update(0.0f);
	}

	public void update(float elapsedTime) {

		if (thisClient != null)
			thisClient.processPackets();

		// for (SceneNode s : getGameWorld()) {
		// if (s instanceof ICollectible) {
		// ICollectible collectible = (ICollectible) s;
		// if (collectible.worldBound().intersects(player1.getWorldBound())) {
		// player1Treasures++;
		// CollectEvent collect = new CollectEvent(player1Treasures);
		// eventManager.triggerEvent(collect);
		// removeGameWorldObject((SceneNode) collectible);
		// break;
		// }
		// }
		// }

		if (terrain.getWorldBound().intersects(player1.getWorldBound())) {
			Point3D avLoc = new Point3D(player1.getLocalTranslation().getCol(3));
			float x = (float) avLoc.getX();
			float z = (float) avLoc.getZ();
			float terHeight = terrain.getHeight(x, z);
			float desiredHeight = terHeight + (float) terrain.getOrigin().getY() + 0.5f;
			if (!Float.isNaN(desiredHeight)) {
				player1.getLocalTranslation().setElementAt(1, 3, desiredHeight);
			}
		}

		scoreHUD1.updateTime(elapsedTime);

		cam1Controller.update(elapsedTime);
		super.update(elapsedTime);
	}

	private void initConfig() {

		this.executeScript(scriptEngine, configFile);

		Invocable invocableEngine = (Invocable) scriptEngine;

		try {
			invocableEngine.invokeFunction("initInput", this, inputManager, player1);
		} catch (ScriptException e1) {
			System.out.println("ScriptException in " + configFile + e1);
		} catch (NoSuchMethodException e2) {
			System.out.println("No such method exception in " + configFile + e2);
		} catch (NullPointerException e3) {
			System.out.println("Null ptr exception reading " + configFile + e3);
		}

		super.update(0.0f);
	}

	private void executeScript(ScriptEngine engine, String scriptFileName) {
		try {
			FileReader fileReader = new FileReader(scriptFileName);
			engine.eval(fileReader);
			fileReader.close();
		} catch (FileNotFoundException e1) {
			System.out.println(scriptFileName + " not found " + e1);
		} catch (IOException e2) {
			System.out.println("IO problem with " + scriptFileName + e2);
		} catch (ScriptException e3) {
			System.out.println("ScriptException in " + scriptFileName + e3);
		} catch (NullPointerException e4) {
			System.out.println("Null ptr exception in " + scriptFileName + e4);
		}
	}

	public static void main(String[] args) {
		String server = "localhost";
		int port = 50001;

		new AwesomeGame(server, port).start();
	}

	public Vector3D getPlayerPosition() {
		return player1.getLocalTranslation().getCol(3);
	}

	public void setIsConnected(boolean b) {
		serverConnected = true;
	}
}
