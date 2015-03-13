package games.treasureHunt2.cameras;

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
import sage.util.MathUtils;

public class OrbitCameraController {
	private ICamera cam; // the camera being controlled
	private SceneNode target; // the target the camera looks at
	private float cameraAzimuth; // rotation of camera around target Y axis
	private float cameraElevation; // elevation of camera above target
	private float cameraDistanceFromTarget;
	private Point3D targetPos; // avatar’s position in the world
	private Vector3D worldUpVec;

	public OrbitCameraController(ICamera cam, SceneNode target, IInputManager inputMgr, String controllerName) {
		this.cam = cam;
		this.target = target;
		worldUpVec = new Vector3D(0, 1, 0);
		cameraDistanceFromTarget = 5.0f;
		cameraAzimuth = 180; // start from BEHIND and ABOVE the target
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
	}

	private void setupInput(IInputManager im, String cn) {
		IAction orbitAction = new OrbitAroundAction();
		im.associateAction(cn, Identifier.Key.Q, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.E, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.U, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn, Identifier.Key.O, orbitAction, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}

	private class OrbitAroundAction extends AbstractInputAction {
		public void performAction(float time, Event e) {
			float rotAmount = 0;
			System.out.println(e.getComponent());
			switch (e.getComponent().toString()) {
			case "Q":
			case "U":
				rotAmount = -0.1f;
				break;
			case "E":
			case "O":
				rotAmount = 0.1f;
				break;
			}
			
			
			
			
//			if (evt.getValue() < -0.2) {
//				rotAmount = -0.1f;
//			} else {
//				if (evt.getValue() > 0.2) {
//					rotAmount = 0.1f;
//				} else {
//					rotAmount = 0.0f;
//				}
//			}
			cameraAzimuth += rotAmount;
			cameraAzimuth = cameraAzimuth % 360;
		}

	}
}
