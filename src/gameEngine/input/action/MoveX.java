package gameEngine.input.action;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class MoveX extends AbstractInputAction {
	private ICamera camera;
	private double speed = -0.01;

	public MoveX(ICamera c) {
		camera = c;
	}

	public void performAction(float time, Event e) {
		Vector3D rightAxis = camera.getRightAxis().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = null;

		switch (e.getComponent().toString()) {
		case "A":
			newLocVec = curLocVector.add(rightAxis.mult(speed));
			break;
		case "D":
			newLocVec = curLocVector.add(rightAxis.mult(speed * -1));
			break;
		}

		double newX = newLocVec.getX();
		double newY = newLocVec.getY();
		double newZ = newLocVec.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);
	}
}
