package games.treasureHunt.objects;

import games.treasureHunt.events.CollectEvent;
import graphicslib3D.Matrix3D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.TriMesh;

public class TreasureChest extends TriMesh implements IEventListener{

	private static float width = 1.5f;
	private static float height = 1.0f;
	private static float depth = 1.0f;

	private static float[] vrts = new float[] {
		-width, height, -depth, 		// vertex 0
		width, height, -depth, 			// vertex 1
		width, height, depth, 			// vertex 2
		-width, height, depth, 			// vertex 3
		-width, -height, -depth, 		// vertex 4
		width, -height, -depth, 		// vertex 5
		width, -height, depth, 			// vertex 6
		-width, -height, depth, 		// vertex 7
	};
	private static float[] cl = new float[] { 
		1, 0, 0, 1,		// color 0
		1, 0, 0, 1,		// color 1
		1, 0, 0, 1,		// color 2
		1, 0, 0, 1,		// color 3
		0, 0, 1, 1,		// color 4
		0, 0, 1, 1, 	// color 5
		0, 0, 1, 1, 	// color 6
		0, 0, 1, 1, 	// color 7
	};
	private static int[] triangles = new int[] { 
		0, 1, 2,		// triangle 1
		0, 2, 3, 		// triangle 2
		2, 3, 6, 		// triangle 3
		3, 6, 7, 		// triangle 4
		4, 5, 6, 		// triangle 5
		4, 6, 7, 		// triangle 6
		0, 1, 5, 		// triangle 7
		0, 4, 5, 		// triangle 8
		0, 3, 7, 		// triangle 9
		0, 7, 4, 		// triangle 10
		1, 2, 5, 		// triangle 11
		5, 6, 2, 		// triangle 12
	};

	public TreasureChest() {
		FloatBuffer vertBuf = com.jogamp.common.nio.Buffers
				.newDirectFloatBuffer(vrts);
		FloatBuffer colorBuf = com.jogamp.common.nio.Buffers
				.newDirectFloatBuffer(cl);
		IntBuffer triangleBuf = com.jogamp.common.nio.Buffers
				.newDirectIntBuffer(triangles);
		this.setVertexBuffer(vertBuf);
		this.setColorBuffer(colorBuf);
		this.setIndexBuffer(triangleBuf);
	}

	@Override
	public boolean handleEvent(IGameEvent event) {
		
		double scaleAmount = 1.2;
		
		Matrix3D chestMatrix = this.getLocalScale();
		chestMatrix.scale(scaleAmount, scaleAmount, scaleAmount);
		this.setLocalScale(chestMatrix);
		
		CollectEvent collectEvent = (CollectEvent) event;
		System.out.println(collectEvent.getCollisionMessage());
		return true;
	}
}
