package samples;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;

import java.awt.Color;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.shape.Line;
import sage.scene.shape.Teapot;

public class TestGame extends BaseGame {
	 IDisplaySystem display;
	 ICamera camera;
	 protected void initGame()
	 { 	
		 initGameObjects();
		 IInputManager im = getInputManager();
//		 String gpName = im.getFirstGamepadName();
		 String kbName = im.getKeyboardName();
		 
		 SetSpeedAction setSpeed = new SetSpeedAction();
		 IAction mvForward = new ForwardAction(camera, setSpeed);
		 IAction mvBackward = new BackwardAction(camera, setSpeed);
		 
		 im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.UP,
				 mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 
		 im.associateAction (
				 kbName, net.java.games.input.Component.Identifier.Key.DOWN,
				 mvBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		 
		 //im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, mvForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 //im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._4, setSpeed, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	 }
	 
	 private void initGameObjects()
	 { 
		 IDisplaySystem display = getDisplaySystem();
		  display.setTitle("My Cool Game");
		  camera = display.getRenderer().getCamera();
		  camera.setPerspectiveFrustum(45, 1, 0.01, 1000);
		  camera.setLocation(new Point3D(1, 1, 20));
		  SimplePyramid aPyr = new SimplePyramid();
		  Matrix3D pyrM = aPyr.getLocalTranslation();
		  pyrM.translate(2,-2,-8);
		  aPyr.setLocalTranslation(pyrM);
		  addGameWorldObject(aPyr);
		  Teapot teap = new Teapot(Color.blue);
		  Matrix3D teaM = teap.getLocalTranslation();
		  teaM.translate(-1,1,-5);
		  teap.setLocalTranslation(teaM);
		  addGameWorldObject(teap);
		  Point3D origin = new Point3D(0,0,0);
		  Point3D xEnd = new Point3D(100,0,0);
		  Point3D yEnd = new Point3D(0,100,0);
		  Point3D zEnd = new Point3D(0,0,100);
		  Line xAxis = new Line (origin, xEnd, Color.red, 2);
		  Line yAxis = new Line (origin, yEnd, Color.green, 2);
		  Line zAxis = new Line (origin, zEnd, Color.blue, 2);
		  addGameWorldObject(xAxis); addGameWorldObject(yAxis);
		  addGameWorldObject(zAxis);
	 }
	  
	 public static void main(String[] args)
	 { 
		 new TestGame().start();
	 }


}
