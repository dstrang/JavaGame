package games.treasureHunt2.objects;

import games.treasureHunt.interfaces.ICollectible;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.renderer.IRenderer;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingVolume;
import sage.scene.shape.Sphere;

public class Ball extends Sphere implements ICollectible{

	private static double radius = 0.1;
	private static int stacks = 20;
	private static int slices = 20;
	private static Color color = Color.WHITE;
	
	public Ball() {
		super(radius, stacks, slices, color);
	}

	public void draw(IRenderer r){
		this.rotate(-0.1f, new Vector3D(1,1,1));
		super.draw(r);
	}

	@Override
	public BoundingVolume worldBound() {
		return this.getWorldBound();
	}

}
