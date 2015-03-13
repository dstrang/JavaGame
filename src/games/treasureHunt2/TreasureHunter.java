package games.treasureHunt2;

import gameEngine.input.action.ForceQuit;
import gameEngine.input.action.MoveX;
import gameEngine.input.action.MoveXAxis;
import gameEngine.input.action.MoveY;
import gameEngine.input.action.MoveYAxis;
import gameEngine.input.action.MoveZ;
import gameEngine.input.action.MoveZAxis;
import gameEngine.input.action.TurnDownAction;
import gameEngine.input.action.TurnLeftAction;
import gameEngine.input.action.TurnPitch;
import gameEngine.input.action.TurnRightAction;
import gameEngine.input.action.TurnUpAction;
import gameEngine.input.action.TurnYaw;
import games.treasureHunt.events.CollectEvent;
import games.treasureHunt.interfaces.ICollectible;
import games.treasureHunt.objects.Ball;
import games.treasureHunt.objects.Coin;
import games.treasureHunt.objects.ScoreHUD;
import games.treasureHunt.objects.TreasureChest;
import games.treasureHunt2.cameras.OrbitCameraController;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import net.java.games.input.Component.Identifier;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.ThirdPersonCameraController;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.scene.HUDString;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import utilities.Util;

public class TreasureHunter extends BaseGame {
	private IDisplaySystem display;
	private ICamera camera1, camera2;
	private OrbitCameraController cameraController;
	private IRenderer renderer;
	private IEventManager evManager;
	private IInputManager im;
	private SceneNode player1, player2;
	private ScoreHUD scoreHUD1, scoreHUD2;
	private OrbitCameraController cam1Controller;
	private OrbitCameraController cam2Controller;
	private Util util = new Util();
	private int treasures = 0;

	protected void initGame() {
		display.setTitle("Treasure Hunter");
		renderer = display.getRenderer();
		im = getInputManager();
		createScene();
		createPlayers();
		initInput();
	}
	
	protected void createDisplay() {
		display = new FSDisplaySystem(1920, 1080, 24, 20, false,
				"sage.renderer.jogl.JOGLRenderer");
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
	}

	protected void shutdown() {
		display.close();
	}
	
	protected void render(){
		renderer.setCamera(camera1);
		super.render();
		
		renderer.setCamera(camera2);
		super.render();
	}



	private void createPlayers() {
		player1 = new Pyramid("PLAYER1");
		player1.scale(0.5f, 0.5f, 0.5f);
		player1.translate(0, 1, 50);
		player1.rotate(180, new Vector3D(0, 1, 0));
		addGameWorldObject(player1);
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(60, 2, 1, 1000);
		camera1.setViewport(0.0, 1.0, 0.0, 0.45);
		
		player2 = new Cube("PLAYER2");
		player2.scale(0.5f, 0.5f, 0.5f);
		player2.translate(50, 1, 0);
		player2.rotate(-90, new Vector3D(0, 1, 0));
		addGameWorldObject(player2);
		camera2 = new JOGLCamera(renderer);
		camera2.setPerspectiveFrustum(60, 2, 1, 1000);
		camera2.setViewport(0.0, 1.0, 0.55, 1.0);
		
		createPlayerHUDs();
	}

	private void createPlayerHUDs() {
		HUDString player1ID = new HUDString("Player1");
		player1ID.setName("Player1ID");
		player1ID.setLocation(0.01, 0.06);
		player1ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		player1ID.setColor(Color.red);
		player1ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera1.addToHUD(player1ID);
		HUDString player2ID = new HUDString("Player2");
		player2ID.setName("Player2ID");
		player2ID.setLocation(0.01, 0.06);
		player2ID.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		player2ID.setColor(Color.yellow);
		player2ID.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera2.addToHUD(player2ID);

	}

	private void createScene() {
		// add XZ plane
		float planeSize = 100.0f;
		Rectangle plane = new Rectangle(planeSize, planeSize);
		plane.setColor(Color.GRAY);
		plane.rotate(90, new Vector3D(1, 0, 0));
		addGameWorldObject(plane);

		// add chest
		TreasureChest chest = new TreasureChest();
		Matrix3D chestMatrix = chest.getLocalTranslation();
		int chestSize = 1;
		chest.scale(chestSize, chestSize, chestSize);
		chestMatrix.translate(0, chestSize, 0);
		chest.setLocalTranslation(chestMatrix);
		addGameWorldObject(chest);

		// add coins
		int numberOfCoins = util.randomInteger(10, 20);
		for (int i = 0; i < numberOfCoins; i++) {
			Coin coin = new Coin();
			Matrix3D coinMatrix = coin.getLocalTranslation();
			float coinSize = 0.1f;
			int spread = 10;
			coin.scale(coinSize, coinSize, coinSize);
			coinMatrix.translate(util.randomInteger(-spread, spread), 1,
					util.randomInteger(-spread, spread));
			coin.setLocalTranslation(coinMatrix);
			addGameWorldObject(coin);
			coin.updateWorldBound();
		}

		// add balls
		int numberOfBalls = util.randomInteger(5, 10);
		for (int i = 0; i < numberOfBalls; i++) {
			Ball ball = new Ball();
			Matrix3D ballMatrix = ball.getLocalTranslation();
			int spread = 10;
			ballMatrix.translate(util.randomInteger(-spread, spread), 1,
					util.randomInteger(-spread, spread));
			ball.setLocalTranslation(ballMatrix);
			ball.rotate(90, new Vector3D(1, 0, 0));
			addGameWorldObject(ball);
			ball.updateWorldBound();
		}

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
	}

	private void initInput() {
		String gamepad = im.getFirstGamepadName();
		String keyboard = im.getKeyboardName();
		String mouse = im.getMouseName();
		
		cam1Controller = new OrbitCameraController(camera1, player1, im, keyboard);
		cam2Controller = new OrbitCameraController(camera2, player2, im, keyboard);

		IAction player1MoveZ = new MoveZ(player1);
		im.associateAction(keyboard, Identifier.Key.W, player1MoveZ,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(keyboard, Identifier.Key.S, player1MoveZ,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		IAction player1MoveX = new MoveX(player1);
		im.associateAction(keyboard, Identifier.Key.A, player1MoveX,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(keyboard, Identifier.Key.D, player1MoveX,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
//		IAction player1TurnLeft = new TurnLeftAction(camera1);
//		im.associateAction(keyboard, Identifier.Key.Q, player1TurnLeft,
//				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//		
//		IAction player1TurnRight = new TurnRightAction(camera1);
//		im.associateAction(keyboard, Identifier.Key.E, player1TurnRight,
//				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		
		IAction player2MoveZ = new MoveZ(player2);
		im.associateAction(keyboard, Identifier.Key.I, player2MoveZ,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(keyboard, Identifier.Key.K, player2MoveZ,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		IAction player2MoveX = new MoveX(player2);
		im.associateAction(keyboard, Identifier.Key.J, player2MoveX,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(keyboard, Identifier.Key.L, player2MoveX,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
//		IAction player2TurnLeft = new TurnLeftAction(camera2);
//		im.associateAction(keyboard, Identifier.Key.U, player2TurnLeft,
//				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//		
//		IAction player2TurnRight = new TurnRightAction(camera2);
//		im.associateAction(keyboard, Identifier.Key.O, player2TurnRight,
//				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		IAction forceQuit = new ForceQuit(this);
		im.associateAction(keyboard, Identifier.Key.ESCAPE,
				forceQuit, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		super.update(0.0f);
	}

	public void update(float elapsedTime) {

//		for (SceneNode s : getGameWorld()) {
//			if (s instanceof ICollectible) {
//				ICollectible collectible = (ICollectible) s;
//				if (collectible.worldBound().contains(camera.getLocation())) {
//					treasures++;
//					CollectEvent collect = new CollectEvent(treasures);
//					evManager.triggerEvent(collect);
//					removeGameWorldObject((SceneNode) collectible);
//					break;
//				}
//			}
//		}

//		scoreHUD.updateTime(elapsedTime);

		cam1Controller.update(elapsedTime);
		cam2Controller.update(elapsedTime);
		super.update(elapsedTime);
	}

	public static void main(String[] args) {
		new TreasureHunter().start();
	}
}
