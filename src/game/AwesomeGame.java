package game;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import input.ForceQuit;
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
import java.util.Iterator;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier;
import networking.GameClient;
import networking.GameServer;
import objects.Avatar;
import objects.ScoreHUD;
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
import sage.scene.shape.Cube;
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

	// managers/engines
	private IDisplaySystem display;
	private IInputManager inputManager;
	private ScriptEngineManager scriptManager;
	private ScriptEngine scriptEngine;
	private IRenderer renderer;
	private IEventManager eventManager;

	// camera
	private ICamera camera1;
	private OrbitCameraController cam1Controller;

	// physics
	private IPhysicsEngine physicsEngine;
	private IPhysicsObject groundP;

	private TriMesh activeChicken;
	private Group chickens;

	// networking
	private boolean singlePlayer = true;
	private boolean serverConnected;
	private GameClient thisClient;
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;

	// display
	private boolean fullscreen = false;

	// audio
	private IAudioManager audioManager;
	private Sound waterSound, backgroundMusic, squawkSound;
	private AudioResource resource1, resource2, resource3;

	// scene
	private ScoreHUD scoreHUD1;
	private SkyBox skybox;
	private Rectangle waterPlane;
	private Cube bucketBox;
	private int playerChickens = 0;
	private int playerScore = 0;
	private TerrainBlock terrain;
	private Model3DTriMesh player;
	private Vector3D prevPosition = new Vector3D(0, 0, 0);
	private Group model;
	private TextureState playerTextureState;
	private String configFile = "config.js";

	public AwesomeGame() {
		super();
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

		initConfig();
		createScene();
		createPlayers();
		initPhysics();
		initAudio();
		initInput();

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

		// init sound files
		resource1 = audioManager.createAudioResource("src/sounds/Background.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioManager.createAudioResource("src/sounds/Ocean.wav", AudioResourceType.AUDIO_SAMPLE);
		resource3 = audioManager.createAudioResource("src/sounds/squawk.wav", AudioResourceType.AUDIO_SAMPLE);

		backgroundMusic = new Sound(resource1, SoundType.SOUND_MUSIC, 30, true);
		waterSound = new Sound(resource2, SoundType.SOUND_EFFECT, 60, true);
		squawkSound = new Sound(resource3, SoundType.SOUND_EFFECT, 100, false);

		backgroundMusic.initialize(audioManager);
		waterSound.initialize(audioManager);
		squawkSound.initialize(audioManager);

		waterSound.setMaxDistance(1.0f);
		waterSound.setMinDistance(0.5f);
		waterSound.setRollOff(0.75f);

		squawkSound.setMaxDistance(1.0f);
		squawkSound.setMinDistance(0.5f);
		squawkSound.setRollOff(0.75f);

		waterSound.setLocation(new Point3D(0, 0, 0));

		waterSound.play();
	}

	private void initPhysics() {

		// init physics engine and gravity
		String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = { 0, -1f, 0 };
		physicsEngine.setGravity(gravity);
		this.createPhysicsWorld();
	}

	private void createPhysicsWorld() {

		// init terrain as physics object
		float up[] = { -0.05f, 0.95f, 0 };
		groundP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), terrain.getWorldTransform().getValues(), up, 0.0f);
		groundP.setBounciness(1.0f);
		groundP.setDamping(1.0f, 0.5f);
		terrain.setPhysicsObject(groundP);
	}

	public void launchChicken() {

		// create and launch chicken at player position
		if (getChickenCount() > 0) {
			Vector3D playerLocation = getPlayerPosition();

			OBJLoader loader = new OBJLoader();
			activeChicken = loader.loadModel("chicken.obj");
			activeChicken.scale(0.05f, 0.05f, 0.05f);
			activeChicken.translate((float) playerLocation.getX(), (float) playerLocation.getY(), (float) playerLocation.getZ());
			addGameWorldObject(activeChicken);

			Vector3D chickenLocation = activeChicken.getLocalTranslation().getCol(3);
			Point3D chickenPosition = new Point3D(chickenLocation.getX(), chickenLocation.getY(), chickenLocation.getZ());

			float mass = 1.0f * 2;
			IPhysicsObject chickenP;
			chickenP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, activeChicken.getLocalTranslation().getValues(), 0.2f);
			chickenP.setBounciness(0.1f);
			float[] vel = { 0, 2.0f, 1.0f };
			chickenP.setLinearVelocity(vel);
			chickenP.setSleepThresholds(0.5f, 0.5f);
			activeChicken.setPhysicsObject(chickenP);

			squawkSound.setLocation(chickenPosition);
			squawkSound.play();
			if (playerChickens > 0) {
				playerChickens--;
			}
		}

	}

	private IDisplaySystem createDisplaySystem() {
		display = new FSDisplaySystem(800, 600, 24, 20, fullscreen, "sage.renderer.jogl.JOGLRenderer");
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

	// create avatar, camera, player HUD
	private void createPlayers() {
		player = createAvatar();
		addGameWorldObject(player);
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(45, 1, 0.01, 1000);
		camera1.setViewport(0.0, 1.0, 0.0, 1.0);
		scoreHUD1 = new ScoreHUD(0.01, 0.06);
		camera1.addToHUD(scoreHUD1);
	}

	private Model3DTriMesh createAvatar() {
		OgreXMLParser loader = new OgreXMLParser();
		Model3DTriMesh character = new Avatar();
		try {
			model = loader.loadModel("character.mesh.xml", "grass_mat.material", "character.skeleton.xml");
			model.updateGeometricState(0, true);
			Iterator<SceneNode> modelIterator = model.iterator();
			character = (Model3DTriMesh) modelIterator.next();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		character.scale(0.2f, 0.2f, 0.2f);
		character.translate(25, 0.5f, 25);
		character.rotate(45, new Vector3D(0, 1, 0));

		Texture p1Texture = TextureManager.loadTexture2D("src/images/rock.jpg");
		p1Texture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		playerTextureState = (TextureState) display.getRenderer().createRenderState(RenderStateType.Texture);
		playerTextureState.setTexture(p1Texture, 0);
		playerTextureState.setEnabled(true);
		character.setRenderState(playerTextureState);
		character.updateRenderStates();
		character.updateLocalBound();

		return character;
	}

	// create 20 chickens at random positions
	private void createChickens() {
		chickens = new Group();

		for (int i = 0; i < 20; i++) {
			OBJLoader loader = new OBJLoader();
			TriMesh chicken = loader.loadModel("chicken.obj");
			Random r = new Random();
			float x = r.nextInt(15) + 25;
			float y = 1.5f;
			float z = r.nextInt(15) + 25;
			chicken.translate(x, y, z);
			chicken.scale(.05f, .05f, .05f);
			chickens.addChild(chicken);
		}

		addGameWorldObject(chickens);
	}

	private void createScene() {

		// create terrain
		ImageBasedHeightMap heightMap = new ImageBasedHeightMap("./src/images/island.jpg");
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
		createChickens();

		// add bucket
		OBJLoader loader = new OBJLoader();
		TriMesh bucket = loader.loadModel("bucket.obj");
		bucket.scale(0.5f, 0.5f, 0.5f);
		bucket.translate(30, 2.8f, 30);
		addGameWorldObject(bucket);

		// bucket collision box
		bucketBox = new Cube();
		bucketBox.scale(0.25f, 0.25f, 0.25f);
		bucketBox.translate(30, 2.5f, 30);
		addGameWorldObject(bucketBox);
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

		inputManager.associateAction(keyboard, Identifier.Key.A, moveX, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.D, moveX, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.W, moveZ, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.S, moveZ, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.ESCAPE, forceQuit, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboard, Identifier.Key.SPACE, launchChicken, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		if (gamepad != null) {
			inputManager.associateAction(gamepad, Identifier.Axis.X, moveX, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			inputManager.associateAction(gamepad, Identifier.Axis.Y, moveZ, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			inputManager.associateAction(gamepad, Identifier.Button._0, launchChicken, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		}

		super.update(0.0f);
	}

	public void update(float elapsedTime) {

		if (thisClient != null) {
			thisClient.sendMoveMessage(getPlayerPosition());
			thisClient.processPackets();
		}

		Matrix3D mat;
		Vector3D translateVec;
		physicsEngine.update(20.0f);
		for (SceneNode s : getGameWorld()) {
			if (s.getPhysicsObject() != null) {
				mat = new Matrix3D(s.getPhysicsObject().getTransform());
				translateVec = mat.getCol(3);
				mat.getCol(1);
				s.getLocalTranslation().setCol(3, translateVec);
			}
		}

		// check for chicken collisions to "pick" them up
		player.updateWorldBound();
		for (SceneNode s : getGameWorld()) {
			if (s instanceof Group && !s.equals(skybox)) {
				Iterator<SceneNode> chickenIt = ((Group) s).iterator();
				while (chickenIt.hasNext()) {
					SceneNode c = chickenIt.next();
					if (c.getWorldBound().intersects(player.getWorldBound())) {
						playerChickens++;
						((Group) s).removeChild(c);
						break;
					}
				}
			}
		}

		// check if chicken is in bucket
		if (activeChicken != null) {
			if (bucketBox.getWorldBound().contains(getChickenPosition(activeChicken))) {
				scoreHUD1.updateScore(++playerScore);
				removeGameWorldObject((SceneNode) activeChicken);
				activeChicken = null;
			}
		}

		// keep player on top of terrain
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

		if (playerPosition.equals(prevPosition)) {
			player.stopAnimation();
		}

		// update time and animation
		prevPosition = playerPosition;
		scoreHUD1.updateTime(elapsedTime);
		cam1Controller.update(elapsedTime);
		player.updateAnimation(elapsedTime);

		super.update(elapsedTime);
	}

	private int getChickenCount() {
		return playerChickens;
	}

	private void initConfig() {

		this.executeScript(scriptEngine, configFile);
		skybox = (SkyBox) scriptEngine.get("skybox");
		addGameWorldObject(skybox);
		
		// init server parameters
		this.serverAddress = (String) scriptEngine.get("serverAddress");
		this.serverPort = Integer.parseInt((String) scriptEngine.get("serverPort"));
		this.serverProtocol = ProtocolType.TCP;

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

	public Vector3D getPlayerPosition() {
		Vector3D position = player.getWorldTranslation().getCol(3);
		return new Vector3D(position.getX(), position.getY(), position.getZ());
	}

	public Point3D getChickenPosition(TriMesh chicken) {
		Vector3D position = chicken.getLocalTranslation().getCol(3);
		return new Point3D(position.getX(), position.getY(), position.getZ());
	}

	public void addChicken(TriMesh chicken) {
		addGameWorldObject(chicken);
	}

	public void setIsConnected(boolean b) {
		serverConnected = b;
		System.out.println("SERVER CONNECTED: " + serverConnected);
	}

	public TriMesh addGhostToGame(float x, float y, float z) {
		Model3DTriMesh ghost = createAvatar();
		ghost.translate(x, y, z);
		addGameWorldObject(ghost);
		return ghost;
	}

	public void removeGhostFromGame(TriMesh ghost) {
		removeGameWorldObject(ghost);
	}

	public static void main(String[] args) {
		new AwesomeGame().start();
	}

}
