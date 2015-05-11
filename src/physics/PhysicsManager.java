package physics;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import objects.Avatar;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.scene.SceneNode;


public class PhysicsManager {
	
	private IPhysicsEngine	physicsEngine;
	
	private String			engine	= "sage.physics.JBullet.JBulletPhysicsEngine";
	private boolean			physicsEngineEnabled = true; 
	
	private boolean			pEnabled = true;
	
	public PhysicsManager(SceneNode localPlayer) {
		if(physicsEngineEnabled){
			physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
			physicsEngine.initSystem();
			float[] gravity = { 0, -4f, 0 };
			physicsEngine.setGravity(gravity);
		}
	}
	
	public void updatePhysicsState(Iterable<SceneNode>  sceneNodes){
		if (pEnabled && physicsEngineEnabled) {
			Matrix3D mat;
			Vector3D translateVec;
			physicsEngine.update(20.0f);
			for (SceneNode s : sceneNodes) {
				if (s.getPhysicsObject() != null && s instanceof Avatar) {
					if( ((Avatar) s).isJumping()){
						mat = new Matrix3D(s.getPhysicsObject().getTransform());
						translateVec = mat.getCol(3);
						s.getLocalTranslation().setCol(3, translateVec);
					}
				}
			}
		}
		
	}
	
	
	public IPhysicsObject bindPhysicsProperty(	SceneNode object,
												float mass) {
		IPhysicsObject pObject;
		pObject = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, object
				.getWorldTransform().getValues(), 1.0f);
		pObject.setBounciness(1.0f);
		object.setPhysicsObject(pObject);
		return pObject;
	}
	
	public IPhysicsObject bindGroundPhysics(SceneNode object) {
		// Apply physics properties to the world floor
		IPhysicsObject ground;
		float up[] = { -0.05f, 0.95f, 0 }; // {0,1,0} is flat
		ground = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), object
				.getWorldTransform().getValues(), up, 0.0f);
		ground.setBounciness(1.0f);
		object.setPhysicsObject(ground);
		return ground;
	}
	
	public boolean isPhysicsEngineEnabled() {
		return physicsEngineEnabled;
	}
	
}
