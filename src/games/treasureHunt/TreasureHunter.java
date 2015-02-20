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
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventListener;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.scene.shape.Line;
import sage.scene.shape.Rectangle;
import utilities.Util;

public class TreasureHunter extends BaseGame{
	 IDisplaySystem display;
	 ICamera camera;
	 IEventManager eventManager;
	 
	 Util util = new Util();
	 
	 protected void initGame()
	 { 	
		 initGameObjects();
		 IInputManager im = getInputManager();
		 eventManager = EventManager.getInstance();
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
	 
	 public void update(float elapsedTime){
		 
		 for(SceneNode s: getGameWorld()){
			 if(s instanceof Rectangle){
				 Rectangle rect = (Rectangle) s;
				 if(rect.getWorldBound().contains(camera.getLocation())){
					 CollectEvent collect = new CollectEvent();
					 eventManager.triggerEvent(collect);
				 }
			 }
		 }
		 
		 super.update(elapsedTime);
	 }
	 
	 private void initGameObjects()
	 { 
		 IDisplaySystem display = getDisplaySystem();
		  display.setTitle("Treasure Hunter");
		  
		  camera = display.getRenderer().getCamera();
		  camera.setPerspectiveFrustum(45, 1, 0.01, 1000);
		  camera.setLocation(new Point3D(1, 1, 20));
		  
//		  // add pyramid
		  Pyramid aPyr = new Pyramid();
		  Matrix3D pyrM = aPyr.getLocalTranslation();
		  pyrM.translate(0,5,0);
		  aPyr.setLocalTranslation(pyrM);
		  addGameWorldObject(aPyr);
		  
//		  // add cube
//		  Cube cube = new Cube();
//		  Matrix3D cubeMatrix = cube.getLocalTranslation();
//		  int cubeSize = 10;
//		  cube.scale(cubeSize, cubeSize, cubeSize);
//		  cubeMatrix.translate(0,cubeSize,0);
//		  cube.setLocalTranslation(cubeMatrix);
//		  addGameWorldObject(cube);
		  
		  // add XZ plane
		  float planeSize = 100.0f;
		  Rectangle plane = new Rectangle(planeSize, planeSize);
		  plane.setColor(Color.GRAY);
		  plane.rotate(90, new Vector3D(1,0,0));
		  addGameWorldObject(plane);
		  
		  // add coins
		  int numberOfCoins = util.randomInteger(10, 20);
		  for(int i = 0; i < numberOfCoins; i++){
//			  Rectangle rect = new Rectangle(0.5f, 0.5f);
//			  rect.setColor(util.randomColor());
//			  Matrix3D rectangleMatrix = rect.getLocalTranslation();
//			  rectangleMatrix.translate(util.randomInteger(-30, 30),1,util.randomInteger(-30, 30));
//			  rect.setLocalTranslation(rectangleMatrix);
		  		
			  Coin coin = new Coin();
			  addGameWorldObject(coin);
			  eventManager.addListener(coin, CollectEvent.class);
		  }
		  
		  
		  // add teapot
//		  Teapot teap = new Teapot(Color.blue);
//		  Matrix3D teaM = teap.getLocalTranslation();
//		  teaM.translate(-1,1,-5);
//		  teap.setLocalTranslation(teaM);
//		  addGameWorldObject(teap);
		  
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
	  
	 public static void main(String[] args)
	 { 
		 new TreasureHunter().start();
	 }
}
