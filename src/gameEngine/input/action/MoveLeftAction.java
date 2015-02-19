package gameEngine.input.action;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import samples.SetSpeedAction;

public class MoveLeftAction extends AbstractInputAction{
	private ICamera camera;
	
	public MoveLeftAction(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 	
		float moveAmount = (float) -0.05;
		Vector3D viewDir = camera.getRightAxis().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = curLocVector.add(viewDir.mult(moveAmount));
		double newX = newLocVec.getX();
		double newY = newLocVec.getY();
		double newZ = newLocVec.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);
	}
}
