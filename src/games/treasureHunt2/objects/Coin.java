package games.treasureHunt2.objects;

import games.treasureHunt.interfaces.ICollectible;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sage.renderer.IRenderer;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingVolume;
import sage.scene.shape.Rectangle;

public class Coin extends TriMesh implements ICollectible{

	private static float width = 1.0f;
	private static float height = 3.0f;

	private static float[] vrts = new float[] {
		0, height, 0, 		// vertex 0
		-width, 0, -width, 	// vertex 1
		width, 0, -width, 	// vertex 2
		width, 0, width, 	// vertex 3
		-width, 0, width, 	// vertex 4
		0, -height, 0 		// vertex 5
	};
	private static float[] cl = new float[] { 
		1, 0, 0, 1,		// color 0
		0, 1, 0, 1,		// color 1
		0, 0, 1, 1,		// color 2
		0, 1, 0, 1,		// color 3
		0, 0, 1, 1,		// color 4
		1, 0, 0, 1 		// color 5
	};
	private static int[] triangles = new int[] { 
		0, 1, 2,		// triangle 1
		0, 2, 3, 		// triangle 2
		0, 3, 4, 		// triangle 3
		0, 4, 1, 		// triangle 4
		5, 1, 2, 		// triangle 5
		5, 2, 3, 		// triangle 6
		5, 3, 4, 		// triangle 7
		5, 4, 1 		// triangle 8
	};

	public Coin() {
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
	public BoundingVolume worldBound() {
		
		return this.getWorldBound();
	}
	
}
