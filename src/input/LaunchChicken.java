package input;

import objects.Avatar;
import game.AwesomeGame;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;
import sage.model.loader.OBJLoader;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.scene.SceneNode;
import sage.scene.TriMesh;

public class LaunchChicken extends AbstractInputAction{
	private Avatar player;
	private AwesomeGame game;
	private IPhysicsEngine physicsEngine;
	private double speed = 0.015;
 
	public LaunchChicken(AwesomeGame awesomeGame, Avatar player, IPhysicsEngine physicsEngine) {
		this.game = awesomeGame;
		this.player = player;
		this.physicsEngine = physicsEngine;
	}

	public void performAction(float time, Event e)
	{
		game.launchChicken();
	}
		

		

	
}
