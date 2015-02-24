package gameEngine.input.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class TurnLeftAction extends AbstractInputAction{
	
	private ICamera camera;
	private double speed = 0.2;
	
	public TurnLeftAction(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 	
		 Matrix3D rotationAmt = new Matrix3D();
		 
		 Vector3D upAxis = camera.getUpAxis();
		 Vector3D viewDirection = camera.getViewDirection();
		 Vector3D rightAxis = camera.getRightAxis();

		 rotationAmt.rotate(speed,upAxis);
		 
		 viewDirection = viewDirection.mult(rotationAmt);
		 rightAxis = rightAxis.mult(rotationAmt);
		 
		 camera.setViewDirection(viewDirection.normalize());
		 camera.setRightAxis(rightAxis.normalize());
	}
}
