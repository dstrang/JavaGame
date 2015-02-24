package gameEngine.input.action;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import samples.SetSpeedAction;

public class MoveZAxis extends AbstractInputAction{
	private ICamera camera;
	private double speed = 0.015;
	
	public MoveZAxis(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 	
		Vector3D upAxis = camera.getUpAxis().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = new Vector3D();
		
		if(e.getValue() < -0.2){
			newLocVec = curLocVector.add(upAxis.mult(speed));
		}else if(e.getValue() > 0.2 && curLocVector.getY() > 1){
			newLocVec = curLocVector.minus(upAxis.mult(speed));
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
