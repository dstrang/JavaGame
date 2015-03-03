package gameEngine.input.action;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class MoveZ extends AbstractInputAction{
	private ICamera camera;
	private double speed = 0.015;
	
	public MoveZ(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 			
		Vector3D viewDir = camera.getViewDirection().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = null;
		
		switch (e.getComponent().toString()) {
		case "W":
			newLocVec = curLocVector.add(viewDir.mult(speed));
			break;
		case "S":
			newLocVec = curLocVector.add(viewDir.mult(speed * -1));
			break;
		}
				
		double newX = newLocVec.getX();
		double newY = (curLocVector.getY() >= 1) ? newLocVec.getY() : 1.0;
		double newZ = newLocVec.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);
	}
}
