package games.treasureHunt2;

import gameEngine.input.action.ForceQuit;
import gameEngine.input.action.MoveX;
import gameEngine.input.action.MoveZ;
import games.treasureHunt2.cameras.OrbitCameraController;
import games.treasureHunt2.events.CollectEvent;
import games.treasureHunt2.interfaces.ICollectible;
import games.treasureHunt2.objects.ScoreHUD;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import utilities.Util;

public class TreasureHunter extends BaseGame {
	private IDisplaySystem display;
	private IInputManager inputManager;
	private ScriptEngineManager scriptManager;
	private ScriptEngine scriptEngine;
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

		createPlayers();
		createScene();
		initInput();
		initConfig();
	}

	private IDisplaySystem createDisplaySystem() {
		display = new FSDisplaySystem(800, 600, 24, 20, false,
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
		return display;
	}

	protected void shutdown() {
		display.close();
	}

	protected void render() {
		renderer.setCamera(camera1);
		super.render();
	}

	private void createPlayers() {
		player1 = new Pyramid("PLAYER1");
		player1.scale(0.2f, 0.2f, 0.2f);
		player1.translate(50, 1, 0);
		player1.rotate(-90, new Vector3D(0, 1, 0));
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
		// add XZ plane
		float planeSize = 100.0f;
		Rectangle plane = new Rectangle(planeSize, planeSize);
		plane.setColor(Color.GRAY);
		plane.rotate(90, new Vector3D(1, 0, 0));
		addGameWorldObject(plane);

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
	}

	private void initInput() {
		// String gamepad = inputManager.getFirstGamepadName();
		String keyboard = inputManager.getKeyboardName();

		cam1Controller = new OrbitCameraController(camera1, 90, player1,
				inputManager, keyboard);



		super.update(0.0f);
	}

	public void update(float elapsedTime) {

		for (SceneNode s : getGameWorld()) {
			if (s instanceof ICollectible) {
				ICollectible collectible = (ICollectible) s;
				if (collectible.worldBound()
						.intersects(player1.getWorldBound())) {
					player1Treasures++;
					CollectEvent collect = new CollectEvent(player1Treasures);
					eventManager.triggerEvent(collect);
					removeGameWorldObject((SceneNode) collectible);
					break;
				}
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
			System.out
					.println("No such method exception in " + configFile + e2);
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
		new TreasureHunter().start();
	}
}
