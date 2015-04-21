package input;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class TurnPitch extends AbstractInputAction {
	private ICamera camera;
	private double speed = 0.2;

	public TurnPitch(ICamera c) {
		camera = c;
	}

	public void performAction(float time, Event e) {
		Matrix3D rotationAmt = new Matrix3D();
		Vector3D viewDirection = camera.getViewDirection();
		Vector3D upAxis = camera.getUpAxis();
		Vector3D rightAxis = camera.getRightAxis();

		if (e.getValue() < -0.2) {
			rotationAmt.rotate(speed, rightAxis);
		} else if (e.getValue() > 0.2) {
			rotationAmt.rotate(speed * -1, rightAxis);
		} else {
			rotationAmt.rotate(0, rightAxis);
		}

		viewDirection = viewDirection.mult(rotationAmt);
		upAxis = upAxis.mult(rotationAmt);

		camera.setUpAxis(upAxis.normalize());
		camera.setViewDirection(viewDirection.normalize());
	}
}
