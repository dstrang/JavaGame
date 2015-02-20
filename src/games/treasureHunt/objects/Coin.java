package games.treasureHunt.objects;

import graphicslib3D.Matrix3D;

import java.awt.Color;

import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.shape.Rectangle;
import utilities.Util;

public class Coin extends Rectangle implements IEventListener{
	Util util = new Util();
	Color color = Color.RED;
	
	public Coin()
	{ 
		  Rectangle rect = new Rectangle(0.5f, 0.5f);
		  rect.setColor(util.randomColor());
		  Matrix3D rectangleMatrix = rect.getLocalTranslation();
		  rectangleMatrix.translate(util.randomInteger(-30, 30),1,util.randomInteger(-30, 30));
		  rect.setLocalTranslation(rectangleMatrix);
	}

	@Override
	public boolean handleEvent(IGameEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
