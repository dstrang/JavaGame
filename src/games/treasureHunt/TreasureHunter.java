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
import games.treasureHunt.objects.Coin;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;

import java.awt.Color;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;

public class TreasureHunter extends BaseGame{
	 IDisplaySystem display;
	 ICamera camera;
	 protected void initGame()
	 { 	
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
//		  pyrM.translate(2,-2,-8);
//		  aPyr.setLocalTranslation(pyrM);
//		  addGameWorldObject(aPyr);
		  
		  // add cube
		  Cube cube = new Cube();
		  Matrix3D cubeMatrix = cube.getLocalTranslation();
		  int cubeSize = 100;
		  cube.scale(cubeSize, cubeSize, cubeSize);
		  cubeMatrix.translate(0,cubeSize,0);
		  cube.setLocalTranslation(cubeMatrix);
		  addGameWorldObject(cube);
		  
		  // add coins
		  
		  
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
