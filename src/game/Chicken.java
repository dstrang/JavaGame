package game;

import interfaces.ICollectible;

import java.awt.Color;

import sage.scene.bounding.BoundingVolume;
import sage.scene.shape.Sphere;

public class Chicken extends Sphere implements ICollectible{
	public Chicken(){
		super(0.1, 10, 10, Color.RED);
	}

	@Override
	public BoundingVolume worldBound() {
		return this.getWorldBound();
	}
}
