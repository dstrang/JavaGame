package input;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;

public class MoveX extends AbstractInputAction {
	private Model3DTriMesh player;
	private double speed = 0.01;

	public MoveX(Model3DTriMesh player) {
		this.player = player;
	}

	public void performAction(float time, Event e) {
		Matrix3D playerMatrix = player.getLocalRotation();
		Vector3D direction = new Vector3D(1,0,0);
		direction = direction.mult(playerMatrix);
		
		switch (e.getComponent().toString()) {
		case "A":
			direction.scale(speed);
			break;
		case "D":
			direction.scale(speed * -1);
			break;
		}

		
		player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
		if (!player.isAnimating())
			player.startAnimation("walk");
	}
}
