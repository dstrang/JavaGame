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
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import net.java.games.input.Component.Identifier;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.scene.shape.Line;
import sage.scene.shape.Rectangle;
import utilities.Util;

public class TreasureHunter extends BaseGame {
	IDisplaySystem display;
	ICamera camera;
	IEventManager evManager;
	ScoreHUD scoreHUD;
	Util util = new Util();
	int treasures = 0;

	protected void initGame() {
		evManager = EventManager.getInstance();

		initGameObjects();
		IInputManager im = getInputManager();

		String gpName = im.getFirstGamepadName();
		String kbName = im.getKeyboardName();

		IAction moveZ = new MoveZ(camera);
		IAction moveX = new MoveX(camera);
		IAction moveY = new MoveY(camera);
		IAction turnUp = new TurnUpAction(camera);
		IAction turnDown = new TurnDownAction(camera);
		IAction turnLeft = new TurnLeftAction(camera);
		IAction turnRight = new TurnRightAction(camera);
		IAction forceQuit = new ForceQuit(this);
		IAction moveYAxis = new MoveYAxis(camera);
		IAction moveXAxis = new MoveXAxis(camera);
		IAction moveZAxis = new MoveZAxis(camera);
		IAction turnPitch = new TurnPitch(camera);
		IAction turnYaw = new TurnYaw(camera);

		// keyboard actions
		im.associateAction(kbName, Identifier.Key.W, moveZ,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.S, moveZ,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.A, moveX,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.D, moveX,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.SPACE, moveY,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.LSHIFT, moveY,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.UP, turnUp,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.DOWN, turnDown,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.LEFT, turnLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.RIGHT, turnRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction(kbName, Identifier.Key.ESCAPE, forceQuit,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		if (gpName != null) {
			// gamepad actions
			im.associateAction(gpName, Identifier.Axis.Y, moveYAxis,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			im.associateAction(gpName, Identifier.Axis.X, moveXAxis,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			im.associateAction(gpName, Identifier.Axis.Z, moveZAxis,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			im.associateAction(gpName, Identifier.Axis.RY, turnPitch,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			im.associateAction(gpName, Identifier.Axis.RX, turnYaw,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			im.associateAction(gpName, Identifier.Button._7, forceQuit,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}

		super.update(0.0f);
	}

	private void initGameObjects() {
		IDisplaySystem display = getDisplaySystem();
		display.setTitle("Treasure Hunter");

		camera = display.getRenderer().getCamera();
		camera.setPerspectiveFrustum(45, 1, 0.01, 1000);
		camera.setLocation(new Point3D(1, 1, 20));

		// add chest
		TreasureChest chest = new TreasureChest();
		Matrix3D chestMatrix = chest.getLocalTranslation();
		int chestSize = 1;
		chest.scale(chestSize, chestSize, chestSize);
		chestMatrix.translate(0, chestSize, 0);
		chest.setLocalTranslation(chestMatrix);
		addGameWorldObject(chest);

		// add XZ plane
		float planeSize = 100.0f;
		Rectangle plane = new Rectangle(planeSize, planeSize);
		plane.setColor(Color.GRAY);
		plane.rotate(90, new Vector3D(1, 0, 0));
		addGameWorldObject(plane);

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

		// add HUD
		scoreHUD = new ScoreHUD();
		addGameWorldObject(scoreHUD);

		// attach event listeners
		evManager.addListener(chest, CollectEvent.class);
		evManager.addListener(scoreHUD, CollectEvent.class);

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

	public void update(float elapsedTime) {

		for (SceneNode s : getGameWorld()) {
			if (s instanceof ICollectible) {
				ICollectible collectible = (ICollectible) s;
				if (collectible.worldBound().contains(camera.getLocation())) {
					treasures++;
					CollectEvent collect = new CollectEvent(treasures);
					evManager.triggerEvent(collect);
					removeGameWorldObject((SceneNode) collectible);
					break;
				}
			}
		}

		scoreHUD.updateTime(elapsedTime);

		super.update(elapsedTime);
	}

	public static void main(String[] args) {
		new TreasureHunter().start();
	}
}
