package com.futuresandwich.game.DirtBike;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public final class CarFactory {
	public static IEntity createCar(final Scene pScene,float x, float y, PhysicsWorld pPhysicsWorld, VertexBufferObjectManager vertexBufferObjectManager, MainActivity activity) {
		final float centerX = x;
		final float centerY = y;

		final float spriteWidth = activity.mBoxFaceTextureRegion.getWidth();
		final float spriteHeight = activity.mBoxFaceTextureRegion.getHeight();
		
		final float anchorFaceX = centerX - spriteWidth * 0.5f + 220 * (-1);
		final float anchorFaceY = centerY - spriteHeight * 0.5f;

		final AnimatedSprite chassisFace = new AnimatedSprite(anchorFaceX, anchorFaceY,90.0f,30.0f, activity.mBoxFaceTextureRegion, vertexBufferObjectManager);
		final AnimatedSprite backMovingFace = new AnimatedSprite(anchorFaceX-(spriteWidth/2), anchorFaceY, activity.mCircleFaceTextureRegion, vertexBufferObjectManager);
		final AnimatedSprite frontMovingFace = new AnimatedSprite(anchorFaceX+90+(spriteWidth/2), anchorFaceY, activity.mCircleFaceTextureRegion, vertexBufferObjectManager);
		
		
		final Body backWheelBody = PhysicsFactory.createCircleBody(pPhysicsWorld, backMovingFace, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(20.0f, 0.2f, 2.0f));
		final Body frontWheelBody = PhysicsFactory.createCircleBody(pPhysicsWorld, frontMovingFace, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(110.0f, 0.2f, 2.0f));
		final Body chassisBody = PhysicsFactory.createBoxBody(pPhysicsWorld, chassisFace, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.0f, 0.2f, 1.0f));
		
		backWheelBody.setUserData("car");
		frontWheelBody.setUserData("car");
		chassisBody.setUserData("car");

		pScene.attachChild(chassisFace);
		pScene.attachChild(backMovingFace);
		pScene.attachChild(frontMovingFace);

//			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(anchorFace, anchorBody, true, true){
//				@Override
//				public void onUpdate(final float pSecondsElapsed) {
//					super.onUpdate(pSecondsElapsed);
//					final Vector2 movingBodyWorldCenter = movingBody.getWorldCenter();
//					connectionLine.setPosition(connectionLine.getX1(), connectionLine.getY1(), movingBodyWorldCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, movingBodyWorldCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
//				}
//			});
		
		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(chassisFace, chassisBody, true, true));
		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(backMovingFace, backWheelBody, true, true));
		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(frontMovingFace, frontWheelBody, true, true));


		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA = chassisBody;
		revoluteJointDef.bodyB = backWheelBody;
		revoluteJointDef.localAnchorA.set(-30.0f/PIXEL_TO_METER_RATIO_DEFAULT,20.0f/PIXEL_TO_METER_RATIO_DEFAULT);
		revoluteJointDef.localAnchorB.set(0.0f, 0.0f);
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.motorSpeed = 30;
		revoluteJointDef.maxMotorTorque = 400;
		revoluteJointDef.collideConnected = false;
		pPhysicsWorld.createJoint(revoluteJointDef);
		
		final RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef();
		revoluteJointDef2.bodyA = chassisBody;
		revoluteJointDef2.bodyB = frontWheelBody;
		revoluteJointDef2.localAnchorA.set(30.0f/PIXEL_TO_METER_RATIO_DEFAULT,20.0f/PIXEL_TO_METER_RATIO_DEFAULT);
		revoluteJointDef2.localAnchorB.set(0.0f, 0.0f);
		revoluteJointDef2.enableMotor = true;
		revoluteJointDef2.motorSpeed = 30;
		revoluteJointDef2.maxMotorTorque = 400;
		revoluteJointDef2.collideConnected = false;
		pPhysicsWorld.createJoint(revoluteJointDef2);
		
		return chassisFace;
	}
}
