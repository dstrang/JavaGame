package controllers;

import sage.scene.Controller;
import sage.scene.SceneNode;
import graphicslib3D.Matrix3D;

public class TranslateController extends Controller {
	private double translationRate = .002;
	private double cycleTime = 1000.0;
	private double totalTime;
	private double direction = 0.5;

	public void setCycleTime(double c) {
		cycleTime = c;
	}

	public void update(double time) 
	{
		totalTime += time;
		double transAmount = translationRate * time;
		if (totalTime > cycleTime) {
			direction = -direction;
			totalTime = 0.0;
		}
		transAmount = direction * transAmount;
		Matrix3D newTrans = new Matrix3D();
		newTrans.translate(0, transAmount, 0);
		for (SceneNode node : controlledNodes) {
			Matrix3D curTrans = node.getLocalTranslation();
			curTrans.concatenate(newTrans);
			node.setLocalTranslation(curTrans);
		}
	}
}
