package cameras;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.util.MathUtils;

public class OrbitCameraController {
	private ICamera cam; // the camera being controlled
	private SkyBox skybox; //skybox moves with the camera
	private SceneNode target; // the target the camera looks at
	private float cameraAzimuth = 0; // rotation of camera around target Y axis
	private float cameraElevation; // elevation of camera above target
	private float cameraDistanceFromTarget;
	private Point3D targetPos; // avatar’s position in the world
	private Vector3D worldUpVec;
	private float turnSpeed = 0.1f;
	private boolean toggle = true;

	public OrbitCameraController(ICamera cam, SkyBox skybox, float azimuth, SceneNode target, IInputManager inputMgr, String controllerName) {
		this.cam = cam;
		this.skybox = skybox;
		this.target = target;
		worldUpVec = new Vector3D(0, 1, 0);
		cameraDistanceFromTarget = 5.0f;
		cameraAzimuth = azimuth; // start from BEHIND and ABOVE the target
		cameraElevation = 20.0f; // elevation is in degrees
		update(0.0f); // initialize camera state
		setupInput(inputMgr, controllerName);
	}

	public void update(float time) {
		updateTarget();
		updateCameraPosition();
		cam.lookAt(targetPos, worldUpVec); // SAGE built-in function
	}

	private void updateTarget() {
		targetPos = new Point3D(target.getWorldTranslation().getCol(3));
	}

	private void updateCameraPosition() {
		double theta = cameraAzimuth;
		double phi = cameraElevation;
		double r = cameraDistanceFromTarget;
		// calculate new camera position in Cartesian coords
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		Point3D desiredCameraLoc = relativePosition.add(targetPos);
		cam.setLocation(desiredCameraLoc);
		
		//move skybox to camera location
		Point3D camLoc = cam.getLocation();
		Matrix3D camTrans = new Matrix3D();
		camTrans.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		skybox.setLocalTranslation(camTrans);
		
//		skybox.translate((float)desiredCameraLoc.getX(), (float)desiredCameraLoc.getY(), (float)desiredCameraLoc.getZ());
	}

	private void setupInput(IInputManager im, String cn) {
		IAction orbitAction = new OrbitAroundAction();
		IAction zoomAction = new ZoomAction();
		IAction toggleAction = new ToggleAvatarRotation();
		im.associateAction(cn, Identifier.Axis.RX, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Axis.RY, zoomAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.LEFT, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.RIGHT, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.UP, zoomAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.DOWN, zoomAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.T, toggleAction, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateAction(cn, Identifier.Button._0, toggleAction, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}

	private class OrbitAroundAction extends AbstractInputAction {
		public void performAction(float time, Event e) {
			float rotAmount = 0;
			switch(e.getComponent().toString()){
			case "Left":
				rotAmount = turnSpeed * time;
				if (toggle) 
				{
					target.rotate(rotAmount, worldUpVec);
				}
				break;
			case "Right":
				rotAmount = turnSpeed * time * -1;
				if (toggle) 
				{
					target.rotate(rotAmount, worldUpVec);
				}
				break;
			case "X Rotation":
				if (e.getValue() < -0.2) {
					rotAmount = turnSpeed * time;
					if (toggle) 
					{
						target.rotate(rotAmount, worldUpVec);
					}
				} else if (e.getValue() > 0.2) {
					rotAmount = turnSpeed * time * -1;
					if (toggle) 
					{
						target.rotate(rotAmount, worldUpVec);
					}
				}
				break;
			}


			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
		}

	}
	
	private class ZoomAction extends AbstractInputAction{
		public void performAction(float time, Event e) {
			float zoomAmount = 0.1f;
			switch(e.getComponent().toString()){
			case "Up":
				zoomAmount = zoomAmount * -1;
				break;
			case "Down":
				
				break;
			case "Y Rotation":
				if (e.getValue() < -0.2) {
					zoomAmount = zoomAmount * -1;
				} else if (e.getValue() > 0.2) {
					
				}
				break;
			}
			cameraDistanceFromTarget += zoomAmount;
			if(cameraDistanceFromTarget < 1.0){
				cameraDistanceFromTarget = 1.0f;
			}else if(cameraDistanceFromTarget > 5.0){
				cameraDistanceFromTarget = 5.0f;
			}
		}
	}
	
	private class ToggleAvatarRotation extends AbstractInputAction
	{
		public void performAction(float time, Event e)
		{
			toggle = !toggle; //Toggle whether avatar will rotate with camera
		}
	}
}
