package gameEngine.input.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class FlyDownAction extends AbstractInputAction{
	private ICamera camera;
	
	public FlyDownAction(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 	
		float moveAmount = (float) -0.01;
		Vector3D upAxis = camera.getUpAxis().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = curLocVector.add(upAxis.mult(moveAmount));
		double newX = newLocVec.getX();
		double newY = newLocVec.getY();
		double newZ = newLocVec.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);
	}
}
