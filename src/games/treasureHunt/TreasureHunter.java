package games.treasureHunt;

import gameEngine.input.action.FlyDownAction;
import gameEngine.input.action.FlyUpAction;
import gameEngine.input.action.MoveBackwardAction;
import gameEngine.input.action.MoveForwardAction;
import gameEngine.input.action.MoveLeftAction;
import gameEngine.input.action.MoveRightAction;
import gameEngine.input.action.TurnDownAction;
import gameEngine.input.action.TurnLeftAction;
import gameEngine.input.action.TurnRightAction;
import gameEngine.input.action.TurnUpAction;
import games.treasureHunt.events.CollectEvent;
import games.treasureHunt.objects.Coin;
import games.treasureHunt.objects.Pyramid;
import games.treasureHunt.objects.ScoreHUD;
import games.treasureHunt.objects.TreasureChest;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.event.*;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;
import sage.scene.shape.Rectangle;
import utilities.Util;

public class TreasureHunter extends BaseGame{
	 IDisplaySystem display;
	 ICamera camera;
	 IEventManager evManager;
	 ScoreHUD scoreHUD;
	 Util util = new Util();
	 int treasures = 0;
	 
	 protected void initGame()
	 { 	
		 evManager = EventManager.getInstance();
		 
		 initGameObjects();
		 IInputManager im = getInputManager();
		 
//		 String gpName = im.getFirstGamepadName();
		 String kbName = im.getKeyboardName();
		 
		 IAction moveForward = new MoveForwardAction(camera);
		 IAction moveBackward = new MoveBackwardAction(camera);
		 IAction moveLeft = new MoveLeftAction(camera);
		 IAction moveRight = new MoveRightAction(camera);
		 IAction turnUp = new TurnUpAction(camera);
		 IAction turnDown = new TurnDownAction(camera);
		 IAction turnLeft = new TurnLeftAction(camera);
		 IAction turnRight = new TurnRightAction(camera);
		 IAction flyUp = new FlyUpAction(camera);
		 IAction flyDown = new FlyDownAction(camera);
		 IAction forceQuit = new ForceQuit(this);
		 		 
		 im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.W,
				 moveForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 
		 im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.S,
				 moveBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.A,
				 moveLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.D,
				 moveRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.UP,
				 turnUp, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				kbName, net.java.games.input.Component.Identifier.Key.DOWN,
				turnDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				kbName, net.java.games.input.Component.Identifier.Key.LEFT,
				turnLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction (
				kbName, net.java.games.input.Component.Identifier.Key.RIGHT,
				turnRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				kbName, net.java.games.input.Component.Identifier.Key.SPACE,
				flyUp, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				kbName, net.java.games.input.Component.Identifier.Key.LSHIFT,
				flyDown, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		im.associateAction (
				kbName, net.java.games.input.Component.Identifier.Key.ESCAPE,
				forceQuit, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		 super.update(0.0f);
		 //im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 //im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._4, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	 }
	 
	 
	 private void initGameObjects()
	 { 
		 IDisplaySystem display = getDisplaySystem();
		  display.setTitle("Treasure Hunter");
		  
		  camera = display.getRenderer().getCamera();
		  camera.setPerspectiveFrustum(45, 1, 0.01, 1000);
		  camera.setLocation(new Point3D(1, 1, 20));
		  
//		  // add pyramid
//		  Pyramid aPyr = new Pyramid();
//		  Matrix3D pyrM = aPyr.getLocalTranslation();
//		  pyrM.translate(0,5,0);
//		  aPyr.setLocalTranslation(pyrM);
//		  addGameWorldObject(aPyr);
		  
//		  // add chest
		  TreasureChest chest = new TreasureChest();
		  Matrix3D chestMatrix = chest.getLocalTranslation();
		  int chestSize = 1;
		  chest.scale(chestSize, chestSize, chestSize);
		  chestMatrix.translate(0,chestSize,0);
		  chest.setLocalTranslation(chestMatrix);
		  addGameWorldObject(chest);
		  
		  // add XZ plane
		  float planeSize = 100.0f;
		  Rectangle plane = new Rectangle(planeSize, planeSize);
		  plane.setColor(Color.GRAY);
		  plane.rotate(90, new Vector3D(1,0,0));
		  addGameWorldObject(plane);
		  
		  // add coins
		  int numberOfCoins = util.randomInteger(10, 20);
		  for(int i = 0; i < numberOfCoins; i++){		  		
			  Coin coin = new Coin();
			  Matrix3D coinMatrix = coin.getLocalTranslation();
			  float coinSize = 0.1f;
			  int spread = 10;
			  coin.scale(coinSize, coinSize, coinSize);
			  coinMatrix.translate(util.randomInteger(-spread, spread),1,util.randomInteger(-spread, spread));
			  coin.setLocalTranslation(coinMatrix);
			  addGameWorldObject(coin);
			  coin.updateWorldBound();
		  }		  
		  
		  // add HUD
		  scoreHUD = new ScoreHUD();
		  addGameWorldObject(scoreHUD);
		  
		  // attach event listeners
		  evManager.addListener(chest, CollectEvent.class);
		  evManager.addListener(scoreHUD, CollectEvent.class);
		  
		  // add axes
		  Point3D origin = new Point3D(0,0,0);
		  Point3D xEnd = new Point3D(100,0,0);
		  Point3D yEnd = new Point3D(0,100,0);
		  Point3D zEnd = new Point3D(0,0,100);
		  Line xAxis = new Line (origin, xEnd, Color.red, 2);
		  Line yAxis = new Line (origin, yEnd, Color.green, 2);
		  Line zAxis = new Line (origin, zEnd, Color.blue, 2);
		  addGameWorldObject(xAxis); 
		  addGameWorldObject(yAxis);
		  addGameWorldObject(zAxis);
	 }
	 
	 public void update(float elapsedTime){
		 
		 for(SceneNode s: getGameWorld()){
			 if(s instanceof Coin){
				 Coin coin = (Coin) s;
				 if(coin.getWorldBound().contains(camera.getLocation())){
					 treasures++;
					 CollectEvent collect = new CollectEvent(treasures);
					 evManager.triggerEvent(collect);
					 removeGameWorldObject(coin);
					 break;
				 }
			 }
		 }
		 
		 scoreHUD.updateTime(elapsedTime);
		 
		 super.update(elapsedTime);
	 }
	  
	 public static void main(String[] args)
	 { 
		 new TreasureHunter().start();
	 }
}
