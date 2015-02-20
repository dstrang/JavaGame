package games.treasureHunt.objects;

import java.awt.Color;

import sage.scene.TriMesh;
import sage.scene.shape.Rectangle;

public class Coin{

	Color color = Color.RED;
	
	public Coin()
	{ 
		Rectangle coin = new Rectangle();
		coin.setColor(color);
	}
}
