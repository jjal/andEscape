package com.futuresandwich.game.DirtBike;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.primitive.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DynamicLine extends PolyLine {
	
	private final static int NUM_VERTICES = 20;
	protected float[] verticesX, verticesY;
	
	protected PolygonShape polyLine;
	protected Body lineBody;
	
	public static DynamicLine CreateDynamicLine(PhysicsWorld world, VertexBufferObjectManager vertexBOManager)
	{
		float[] vX = new float[NUM_VERTICES];
		float[] vY = new float[NUM_VERTICES];
		return new DynamicLine(world, vX, vY, vertexBOManager);
	}
	
	protected DynamicLine(PhysicsWorld world, float[] vX, float[] vY, VertexBufferObjectManager vertexBOManager)
	{
		super(0.0f,0.0f,vX, vY, vertexBOManager);
		verticesX = vX;
		verticesY = vY;
		
		polyLine = new com.badlogic.gdx.physics.box2d.
		
		BodyDef bodyDef = new BodyDef();
		
		// The body is also added to the world.
		lineBody = world.createBody(bodyDef);
		lineBody.setUserData(this);
		
		// Define the line shape.
		polyLine = new PolygonShape();
	}
	
	
	private void queueVertex(float x, float y)
	{
		for(int i=0;i<NUM_VERTICES-1;i++)
		{
			verticesX[i] = verticesX[i+1];
			verticesY[i] = verticesY[i+1];
		}
		verticesX[NUM_VERTICES-1] = x;
		verticesY[NUM_VERTICES-1] = y;
	}
	
	float[] temp,temp2;
	public void addVertex(float x, float y)
	{
		float prevX = verticesX[NUM_VERTICES-1];
		float prevY = verticesY[NUM_VERTICES-1];
		temp = //this.convertSceneToLocalCoordinates(
				new float[] {x,y}
		//)
		;
		temp2 = //this.convertSceneToLocalCoordinates(
				new float[] {prevX,prevY}
		//)
		;
		
		polyLine.setAsEdge(new Vector2(temp[0]/PIXEL_TO_METER_RATIO_DEFAULT, temp[1]/ PIXEL_TO_METER_RATIO_DEFAULT), 
				new Vector2(temp2[0]/ PIXEL_TO_METER_RATIO_DEFAULT,temp2[1]/ PIXEL_TO_METER_RATIO_DEFAULT));
		
		lineBody.createFixture(polyLine, 0);
		queueVertex(x,y);
		//polyLine.set(vertices)
		this.updateVertices(verticesX, verticesY);
	}
}
