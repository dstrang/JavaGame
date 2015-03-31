package gameEngine.input.action;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class MoveYAxis extends AbstractInputAction{
	private ICamera camera;
	private double speed = 0.015;
	
	public MoveYAxis(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 	
		Vector3D viewDir = camera.getViewDirection().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = new Vector3D();
		
		if(e.getValue() < -0.2){
			newLocVec = curLocVector.add(viewDir.mult(speed));
		}else if(e.getValue() > 0.2){
			newLocVec = curLocVector.minus(viewDir.mult(speed));
		}else{
			newLocVec = curLocVector;
		}
		
		double newX = newLocVec.getX();
		double newY = newLocVec.getY();
		double newZ = newLocVec.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);
	}
}
