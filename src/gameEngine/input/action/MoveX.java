package gameEngine.input.action;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

public class MoveX extends AbstractInputAction {
	private SceneNode player;
	private double speed = 0.01;

	public MoveX(SceneNode player) {
		this.player = player;
	}

	public void performAction(float time, Event e) {
		Matrix3D playerMatrix = player.getLocalRotation();
		Vector3D direction = new Vector3D(1,0,0);
		direction = direction.mult(playerMatrix);
		
		switch (e.getComponent().toString()) {
		case "A":
			direction.scale(speed * time);
			break;
		case "D":
			direction.scale(speed * time * -1);
			break;
		}
		
		player.translate((float)direction.getX(), (float)direction.getY(), (float)direction.getZ());
	}
}
