package gameEngine.input.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;
import samples.SetSpeedAction;

public class MoveZAxis extends AbstractInputAction{
	private SceneNode player;
	private double speed = 0.015;
	
	public MoveZAxis(SceneNode player)
	{ 
		this.player = player;
	}
 
	public void performAction(float time, Event e)
	{ 	
		Matrix3D playerMatrix = player.getLocalRotation();
		Vector3D direction = new Vector3D(0,0,1);
		direction = direction.mult(playerMatrix);

		if(e.getValue() < -0.7){
			direction.scale(speed * time);
			player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
		}else if(e.getValue() > 0.7){
			direction.scale(speed * time * -1);
			player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
		}
		
	}
}
