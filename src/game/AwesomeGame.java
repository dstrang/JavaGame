package game;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import input.ForceQuit;
import input.Jump;
import input.LaunchChicken;
import input.MoveX;
import input.MoveZ;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier;
import networking.GameClient;
import networking.GameServer;
import objects.Avatar;
import objects.ScoreHUD;
import physics.PhysicsManager;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.action.IAction;
import sage.model.loader.OBJLoader;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.TriMesh;
import sage.scene.shape.Line;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import utilities.Util;
import cameras.OrbitCameraController;
import displays.FSDisplaySystem;
import events.CollectEvent;

public class AwesomeGame extends BaseGame {
	private IDisplaySystem display;
	private IInputManager inputManager;
	private ScriptEngineManager scriptManager;
	private ScriptEngine scriptEngine;
	private SkyBox skybox;
	private ICamera camera1;
	private IRenderer renderer;
	private IEventManager eventManager;
	// private SceneNode player1;
	private ScoreHUD scoreHUD1;
	private OrbitCameraController cam1Controller;
	private Util util = new Util();
	private int player1Treasures = 0;
	private Group treasures;
	private String configFile = "config.js";
	private TerrainBlock terrain;
	// private HillHeightMap heightMap;

	private Model3DTriMesh player;
	private Group model;
	// private Avatar player;

	private TextureState playerTextureState;
	private static GameServer GameServer;
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private GameClient thisClient;
	private boolean serverConnected;

	// physics
	private IPhysicsEngine physicsEngine;
	private IPhysicsObject chickenP, groundP;

	private TriMesh chicken;

	private boolean singlePlayer = true;

	// audio
	private IAudioManager audioManager;
	private Sound waterSound, backgroundMusic;
	private AudioResource resource1, resource2;

	private Rectangle waterPlane;

	public AwesomeGame() {
		super();
		// this.serverAddress = serverAddr;
		// this.serverPort = sPort;
		// this.serverProtocol = ProtocolType.TCP;
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
		initPhysics();
		initAudio();

		if (!singlePlayer) {
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
	}

	private void initAudio() {
		audioManager = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");

		if (!audioManager.initialize()) {
			System.out.println("Audio Manager failed to initialize");
		}

		resource1 = audioManager.createAudioResource("src/sounds/Background.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioManager.createAudioResource("src/sounds/Ocean.wav", AudioResourceType.AUDIO_SAMPLE);

		backgroundMusic = new Sound(resource1, SoundType.SOUND_MUSIC, 30, true);
		waterSound = new Sound(resource2, SoundType.SOUND_EFFECT, 60, true);

		backgroundMusic.initialize(audioManager);
		waterSound.initialize(audioManager);

		waterSound.setMaxDistance(1.0f);
		waterSound.setMinDistance(0.5f);
		waterSound.setRollOff(0.75f);

		waterSound.setLocation(new Point3D(0, 0, 0));

		// backgroundMusic.play();
		waterSound.play();
	}

	private void initPhysics() {

		String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = { 0, -1f, 0 };
		physicsEngine.setGravity(gravity);

		this.createPhysicsWorld();

		// physicsManager = new PhysicsManager(player);
		//
		// // Populate physics objects if the physics engine has been
		// intialized.
		// if (physicsManager.isPhysicsEngineEnabled()) {
		// ground = physicsManager.bindGroundPhysics(terrain);
		// player.setPhysicsObject(physicsManager.bindPhysicsProperty(player,
		// 1.0f));
		// }
	}

	private void createPhysicsWorld() {
		float up[] = { -0.05f, 0.95f, 0 };
		groundP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), terrain.getWorldTransform().getValues(), up, 0.0f);
		groundP.setBounciness(1.0f);
		groundP.setDamping(1.0f, 0.5f);
//		groundP.setSleepThresholds(1.0f, 1.0f);
		terrain.setPhysicsObject(groundP);

		float mass = 1.0f;
		chickenP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, chicken.getWorldTransform().getValues(), 0.0f);
		chickenP.setBounciness(0.9f);
		float[] vel = {0, 0, 0.5f};
//		chickenP.setAngularVelocity(vel);
		chickenP.setLinearVelocity(vel);
		chickenP.setSleepThresholds(0.5f, 0.5f);
		chicken.setPhysicsObject(chickenP);
	}
	
	public void launchChicken(){
		Vector3D playerLocation = getPlayerPosition();
		OBJLoader loader = new OBJLoader();
		final TriMesh chicken = loader.loadModel("chicken.obj");
		chicken.scale(0.02f, 0.02f, 0.02f);
		chicken.translate((float)playerLocation.getX(), (float)playerLocation.getY(), (float)playerLocation.getZ());
		chicken.updateRenderStates();
		chicken.updateLocalBound();
		addGameWorldObject(chicken);
		
		float mass = 1.0f;
		IPhysicsObject chickenP;
		chickenP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, chicken.getLocalTranslation().getValues(), 0.0f);

		chickenP.setBounciness(0.9f);
		float[] vel = {0, 1.0f, 0.5f};
		chickenP.setLinearVelocity(vel);
		chickenP.setSleepThresholds(0.5f, 0.5f);
		chicken.setPhysicsObject(chickenP);
		
		new Timer().schedule(new TimerTask(){
		    public void run(){
//				removeGameWorldObject(chicken);
		    }
		}, 2000 );
		
	}

	// public PhysicsManager getPhysicsManager(){
	// return physicsManager;
	// }

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
		if (thisClient != null) {
			thisClient.sendByeMessage();
			try {
				thisClient.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// release audio
		backgroundMusic.release(audioManager);
		waterSound.release(audioManager);
		resource1.unload();
		resource2.unload();
		audioManager.shutdown();

		display.close();
	}

	protected void render() {
		renderer.setCamera(camera1);
		super.render();
	}

	private void createPlayers() {

		//player = new Avatar();
		player = createAvatar();
		//player = createAvatar("src/images/jajalien1_top.jpg");

		addGameWorldObject(player);
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(45, 1, 0.01, 1000);
		camera1.setViewport(0.0, 1.0, 0.0, 1.0);
		createPlayerHUDs();
	}
	
	private Model3DTriMesh createAvatar() {
		OgreXMLParser loader = new OgreXMLParser();
		Model3DTriMesh character = new Avatar();
		try {
			model = loader.loadModel("character.mesh.xml", "grass_mat.material", "character.skeleton.xml");
			model.updateGeometricState(0, true);
			java.util.Iterator<SceneNode> modelIterator = model.iterator();
			character = (Model3DTriMesh) modelIterator.next();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		character.scale(0.2f, 0.2f, 0.2f);
		character.translate(25, 0.5f, 25);
		character.rotate(45, new Vector3D(0, 1, 0));
		
		Texture p1Texture = TextureManager.loadTexture2D("src/images/jajalien1_top.jpg");
		p1Texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		playerTextureState = (TextureState) renderer.createRenderState(RenderStateType.Texture);
		playerTextureState.setTexture(p1Texture, 0);
		playerTextureState.setEnabled(true);
		model.setRenderState(playerTextureState);
		model.updateRenderStates();
		model.updateLocalBound();
		
		return character;
	}

//	private TriMesh createAvatar(String textureFile) {
//		OBJLoader loader = new OBJLoader();
//		TriMesh model = loader.loadModel("character.obj");
//		model.scale(0.2f, 0.2f, 0.2f);
//		model.rotate(45, new Vector3D(0, 1, 0));
//		Texture p1Texture = TextureManager.loadTexture2D(textureFile);
//		p1Texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
//		TextureState playerTextureState = (TextureState) renderer.createRenderState(RenderStateType.Texture);
//		playerTextureState.setTexture(p1Texture, 0);
//		playerTextureState.setEnabled(true);
//		model.setRenderState(playerTextureState);
//		model.updateRenderStates();
//		model.updateLocalBound();
//
//		return model;
//	}

	private TriMesh createChicken() {
		OBJLoader loader = new OBJLoader();
		TriMesh model = loader.loadModel("chicken.obj");
		model.scale(0.02f, 0.02f, 0.02f);
		model.updateRenderStates();
		model.updateLocalBound();

		return model;
	}

	private void createPlayerHUDs() {
		scoreHUD1 = new ScoreHUD(0.01, 0.06);
		camera1.addToHUD(scoreHUD1);
	}

	private void createScene() {
		// initialize skybox
		skybox = new SkyBox("Skybox", 20.0f, 20.0f, 20.0f);

		// load textures
		Texture northTexture = TextureManager.loadTexture2D("src/images/ocean_front.png");
		Texture southTexture = TextureManager.loadTexture2D("src/images/ocean_back.png");
		Texture eastTexture = TextureManager.loadTexture2D("src/images/ocean_right.png");
		Texture westTexture = TextureManager.loadTexture2D("src/images/ocean_left.png");
		Texture upTexture = TextureManager.loadTexture2D("src/images/ocean_up.png");
		Texture downTexture = TextureManager.loadTexture2D("src/images/ocean_down.png");

		// attach textures to skybox
		skybox.setTexture(SkyBox.Face.North, northTexture);
		skybox.setTexture(SkyBox.Face.South, southTexture);
		skybox.setTexture(SkyBox.Face.East, eastTexture);
		skybox.setTexture(SkyBox.Face.West, westTexture);
		skybox.setTexture(SkyBox.Face.Up, upTexture);
		skybox.setTexture(SkyBox.Face.Down, downTexture);
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

		ImageBasedHeightMap heightMap = new ImageBasedHeightMap("./src/images/island.jpg");
		// HillHeightMap heightMap = new HillHeightMap(100, 10, 20.0f, 21.0f,
		// (byte) 1);
		// heightMap.setHeightScale(1f);
		terrain = createTerrainBlock(heightMap);
		TextureState grassState;
		Texture grassTexture = TextureManager.loadTexture2D("src/images/grass.jpg");
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);

		grassState = (TextureState) renderer.createRenderState(RenderStateType.Texture);

		grassState.setTexture(grassTexture, 0);
		grassState.setEnabled(true);
		terrain.setRenderState(grassState);

		addGameWorldObject(terrain);

		// add water
		int waterSize = 100;
		waterPlane = new Rectangle(waterSize, waterSize);
		waterPlane.rotate(90, new Vector3D(1, 0, 0));
		waterPlane.translate(waterSize / 2, 0.5f, waterSize / 2);
		Texture waterTexture = TextureManager.loadTexture2D("src/images/ocean_down.png");
		waterPlane.setTexture(waterTexture);
		addGameWorldObject(waterPlane);

		// add chickens
		chicken = createChicken();
		chicken.translate(25, 1.5f, 25);
		addGameWorldObject(chicken);
	}

	private TerrainBlock createTerrainBlock(AbstractHeightMap heightMap) {
		float heightScale = .010f;
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

		cam1Controller = new OrbitCameraController(camera1, skybox, 225, player, inputManager, controller);

		IAction moveX = new MoveX(player);
		IAction moveZ = new MoveZ(player);
		IAction forceQuit = new ForceQuit(this);
		IAction launchChicken = new LaunchChicken(this, player, physicsEngine);

		inputManager.associateAction(controller, Identifier.Key.A, moveX, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(controller, Identifier.Key.D, moveX, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(controller, Identifier.Key.W, moveZ, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(controller, Identifier.Key.S, moveZ, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.ESCAPE, forceQuit, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.SPACE, launchChicken, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

		super.update(0.0f);
	}

	public void update(float elapsedTime) {

		if (thisClient != null) {
			thisClient.sendMoveMessage(getPlayerPosition());
			thisClient.processPackets();
		}

		// physicsManager.updatePhysicsState(getGameWorld());

		Matrix3D mat;
		Vector3D translateVec;
		Vector3D rotateVec;
		physicsEngine.update(20.0f);
		for (SceneNode s : getGameWorld()) {
			if (s.getPhysicsObject() != null) {
				mat = new Matrix3D(s.getPhysicsObject().getTransform());
				translateVec = mat.getCol(3);
				rotateVec = mat.getCol(1);
				s.getLocalTranslation().setCol(3, translateVec);
//				s.getLocalRotation().setCol(1, rotateVec);
				// should also get and apply rotation
			}
		}

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

		if (terrain.getWorldBound().intersects(player.getWorldBound())) {
			Point3D avLoc = new Point3D(player.getLocalTranslation().getCol(3));
			float x = (float) avLoc.getX();
			float z = (float) avLoc.getZ();
			float terHeight = terrain.getHeight(x, z);
			float desiredHeight = terHeight + (float) terrain.getOrigin().getY() + 0.4f;
			if (!Float.isNaN(desiredHeight)) {
				player.getLocalTranslation().setElementAt(1, 3, desiredHeight);
			}
		}

		Vector3D playerPosition = getPlayerPosition();
		if (playerPosition.getY() < 0.5f) {
//			player.respawn();
		}

		scoreHUD1.updateTime(elapsedTime);

		cam1Controller.update(elapsedTime);
		
		player.updateAnimation(elapsedTime);
		
		//cheap way to stop animation when not walking?
//		if (!player.isWalking())
//			player.stopAnimation();
//		player.setWalking(false);

		super.update(elapsedTime);
	}

	private void initConfig() {

		// this.executeScript(scriptEngine, configFile);
		//
		// Invocable invocableEngine = (Invocable) scriptEngine;
		//
		// try {
		// invocableEngine.invokeFunction("initInput", this, inputManager,
		// player1);
		// } catch (ScriptException e1) {
		// System.out.println("ScriptException in " + configFile + e1);
		// } catch (NoSuchMethodException e2) {
		// System.out.println("No such method exception in " + configFile + e2);
		// } catch (NullPointerException e3) {
		// System.out.println("Null ptr exception reading " + configFile + e3);
		// }

		this.serverAddress = "localhost";
		this.serverPort = 50001;
		this.serverProtocol = ProtocolType.TCP;

		// try {
		// try {
		// scriptEngine.eval(new java.io.FileReader(configFile));
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } catch (ScriptException e) {
		// e.printStackTrace();
		// }
		//
		// System.out.println(scriptEngine.get("serverAddress"));
		// System.out.println(scriptEngine.get("serverPort"));
		// System.out.println(scriptEngine.get("serverProtocol"));

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

		new AwesomeGame().start();
	}

	public Vector3D getPlayerPosition() {
		Vector3D position = player.getWorldTranslation().getCol(3);
		return new Vector3D(position.getX(), position.getY(), position.getZ());
	}
	
	public void addChicken(TriMesh chicken){
		addGameWorldObject(chicken);
	}

	public void setIsConnected(boolean b) {
		serverConnected = b;
		System.out.println("SERVER CONNECTED: " + serverConnected);
	}

	public TriMesh addGhostToGame(float x, float y, float z) {
//		TriMesh ghost = createAvatar("src/images/jajalien1_left.jpg");
		Model3DTriMesh ghost = createAvatar();
		ghost.translate(x, y, z);
		addGameWorldObject(ghost);
		return ghost;
	}

	public void removeGhostFromGame(TriMesh ghost) {
		removeGameWorldObject(ghost);
	}
}
