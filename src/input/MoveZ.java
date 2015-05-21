package input;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;

public class MoveZ extends AbstractInputAction{
	private Model3DTriMesh player;
	private double speed = 0.015;
	
	public MoveZ(Model3DTriMesh player)
	{ 
		this.player = player;
	}
 
	public void performAction(float time, Event e)
	{
		
		Matrix3D playerMatrix = player.getLocalRotation();
		Vector3D direction = new Vector3D(0,0,1);
		direction = direction.mult(playerMatrix);
		
		switch (e.getComponent().toString()) {
		case "W":
			direction.scale(speed);
			player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
			break;
		case "S":
			direction.scale(-speed);
			player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
			break;
		case "Y Axis":
			if (e.getValue() < -0.3)
			{
				direction.scale(speed);	
				player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
			}
			else if (e.getValue() > 0.3)
			{
				direction.scale(-speed);	
				player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
			}
			break;
		}

		if (!player.isAnimating())
			player.startAnimation("walk");
	}
}
