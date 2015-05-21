package objects;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import interfaces.ICollectible;

import java.util.Random;

import sage.audio.Sound;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingVolume;

public class Chicken extends TriMesh implements ICollectible{
	
	TriMesh model;

	public Chicken(TriMesh model){
		this.model = model;
		this.model.scale(0.02f, 0.02f, 0.02f);
	}
	
	public void setRandomLocation(){
		Random r = new Random();
		float x = r.nextInt(15) + 25;
		float y = 1.5f;
		float z = r.nextInt(15) + 25;
		this.model.translate(x, y, z);
	}
	
	public TriMesh getModel(){
		return this.model;
	}
	
	public void updateSoundLocation(Sound sound) {
		Vector3D location = this.model.getLocalTranslation().getCol(3);
		Point3D newLoc = new Point3D(location.getX(), location.getY(), location.getZ());
		sound.setLocation(newLoc);
	}

	@Override
	public BoundingVolume worldBound() {
		return this.model.getWorldBound();
	}
}
