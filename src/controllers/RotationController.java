package controllers;

import sage.scene.Controller;
import sage.scene.SceneNode;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class RotationController extends Controller {
	private double rotationRate = 50;
	private double cycleTime = 1000.0;
	private double totalTime;
	private float rotationAmount = 0.5f;

	public void setCycleTime(double c) {
		cycleTime = c;
	}

	public void update(double time)
	{
		rotationAmount = (float) (rotationRate * time);
		Matrix3D rotation = new Matrix3D();
		rotation.rotate(rotationAmount,rotationAmount,rotationAmount);
		for (SceneNode node : controlledNodes) {
			Matrix3D curRotation = node.getLocalRotation();
			curRotation.concatenate(rotation);
			node.setLocalRotation(rotation);
		}
	}
}
