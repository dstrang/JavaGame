package objects;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.UUID;

import sage.physics.IPhysicsObject;
import sage.scene.TriMesh;

public class Avatar extends TriMesh{
	private static float[] vrts = new float[] {0,1,0,  -1,-1,1,  1,-1,1,  1,-1,-1,  -1,-1,-1};
	private static float[] cl = new float[] {1,0,0,1,  0,1,0,1,  0,0,1,1,  1,1,0,1,  1,0,1,1};
	private static int[] triangles = new int[] {0,1,2,  0,2,3,  0,3,4,  0,4,1,  1,4,2,  4,3,2};
	private UUID id;
	private boolean isJumping;
	private IPhysicsObject physicsObject;
	private float size = 0.1f;
	
	public Avatar()
	{ 
		id = UUID.randomUUID();
		FloatBuffer vertBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
		FloatBuffer colorBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
		IntBuffer triangleBuf = com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
		this.setVertexBuffer(vertBuf);
		this.setColorBuffer(colorBuf);
		this.setIndexBuffer(triangleBuf);
		this.scale(size, size, size);
		this.rotate(45, new Vector3D(0,1,0));
		this.respawn();
	}
	
	public void respawn() {
		Matrix3D origin = new Matrix3D();
		origin.translate(25, 0.5f, 25);
		this.setLocalTranslation(origin);
	}

	public UUID getUUID(){
		return id;
	}
	
	public boolean isJumping() {
		return isJumping;
	}
	
	public void setJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public IPhysicsObject getPhysicsObject() {
		return physicsObject;
	}

	public void setPhysicsObject(IPhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}
	
	public float getSize(){
		return this.size;
	}
}
