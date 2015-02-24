package gameEngine.input.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class TurnUpAction extends AbstractInputAction {
	
	private ICamera camera;
	private double speed = 0.2;

	public TurnUpAction(ICamera c) {
		camera = c;
	}

	public void performAction(float time, Event e) {
		Matrix3D rotationAmt = new Matrix3D();

		Vector3D upAxis = camera.getUpAxis();
		Vector3D viewDirection = camera.getViewDirection();
		Vector3D rightAxis = camera.getRightAxis();

		rotationAmt.rotate(speed, rightAxis);

		upAxis = upAxis.mult(rotationAmt);
		viewDirection = viewDirection.mult(rotationAmt);

		camera.setUpAxis(upAxis.normalize());
		camera.setViewDirection(viewDirection.normalize());
	}
}
