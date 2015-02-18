package samples;

import net.java.games.input.Event;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;


import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class ForwardAction extends AbstractInputAction
{ 
	private ICamera camera;
	private SetSpeedAction runAction;
	
	public ForwardAction(ICamera c, SetSpeedAction r)
	{ 
		camera = c;
		runAction = r;
	}
 
	public void performAction(float time, Event e)
	{ 	
		float moveAmount ;
 
		if (runAction.isRunning())
		{ 
			moveAmount = (float) 0.5 ; 
		}
		else
		{ 
			moveAmount = (float) 0.1 ;
		}
 
		Vector3D viewDir = camera.getViewDirection().normalize();
		Vector3D curLocVector = new Vector3D(camera.getLocation());
		Vector3D newLocVec = curLocVector.add(viewDir.mult(moveAmount));
		double newX = newLocVec.getX();
		double newY = newLocVec.getY();
		double newZ = newLocVec.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);
	}

}

