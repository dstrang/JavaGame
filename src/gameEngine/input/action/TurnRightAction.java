package gameEngine.input.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class TurnRightAction extends AbstractInputAction{
	private ICamera camera;
	
	public TurnRightAction(ICamera c)
	{ 
		camera = c;
	}
 
	public void performAction(float time, Event e)
	{ 	
		 Matrix3D rotationAmt = new Matrix3D();
		 Vector3D viewDirection = camera.getViewDirection();
		 Vector3D upAxis = camera.getUpAxis();
		 Vector3D rightAxis = camera.getRightAxis();

		 rotationAmt.rotate(-0.05,upAxis);
		 
		 viewDirection = viewDirection.mult(rotationAmt);
		 rightAxis = rightAxis.mult(rotationAmt);
		 
		 camera.setRightAxis(rightAxis.normalize());
		 camera.setViewDirection(viewDirection.normalize());;
	}
}
