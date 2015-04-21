package input;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class TurnYaw extends AbstractInputAction {

	private ICamera camera;
	private double speed = 0.2;

	public TurnYaw(ICamera c) {
		camera = c;
	}

	public void performAction(float time, Event e) {
		Matrix3D rotationAmt = new Matrix3D();

		Vector3D globalUp = new Vector3D(0, 1, 0);

		Vector3D upAxis = camera.getUpAxis();
		Vector3D viewDirection = camera.getViewDirection();
		Vector3D rightAxis = camera.getRightAxis();

		if (e.getValue() < -0.2) {
			rotationAmt.rotate(speed, globalUp);
		} else if (e.getValue() > 0.2) {
			rotationAmt.rotate(speed * -1, globalUp);
		} else {
			rotationAmt.rotate(0, globalUp);
		}

		upAxis = upAxis.mult(rotationAmt);
		viewDirection = viewDirection.mult(rotationAmt);
		rightAxis = rightAxis.mult(rotationAmt);

		camera.setUpAxis(upAxis.normalize());
		camera.setViewDirection(viewDirection.normalize());
		camera.setRightAxis(rightAxis.normalize());
	}
}
