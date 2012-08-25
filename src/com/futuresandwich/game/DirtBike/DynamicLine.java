package com.futuresandwich.game.DirtBike;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Mesh;
import org.andengine.entity.primitive.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.opengl.GLES10;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class DynamicLine extends PolyLine {
	
	private final static int NUM_VERTICES = 30;
	//the shape (andengine) vertices
	protected float[] verticesX, verticesY;
	//an array of vertices for box2d. Seems silly no?
	Vector2[] vertices;
	
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
		vertices = new Vector2[NUM_VERTICES];
		for(int i=0;i<NUM_VERTICES;i++)
			vertices[i] = new Vector2(verticesX[i],verticesY[i]);
		
		BodyDef bodyDef = new BodyDef();
		
		// The body is also added to the world.
		lineBody = world.createBody(bodyDef);
		lineBody.setUserData(this);
		
		// Define the line shape.
		polyLine = new PolygonShape();
		
		this.setDrawMode(Mesh.DrawMode.LINE_STRIP);
	}
	
	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
		super.preDraw(pGLState, pCamera);
		//doesn't work!
		GLES20.glEnable(GLES10.GL_LINE_SMOOTH);
	};
	
	private void queueVertex(float x, float y)
	{
		for(int i=0;i<NUM_VERTICES-1;i++)
		{
			verticesX[i] = verticesX[i+1];
			verticesY[i] = verticesY[i+1];
			vertices[i] = vertices[i+1];
		}
		verticesX[NUM_VERTICES-1] = x;
		verticesY[NUM_VERTICES-1] = y;
		vertices[NUM_VERTICES-1]  = new Vector2(x/ PIXEL_TO_METER_RATIO_DEFAULT,y/ PIXEL_TO_METER_RATIO_DEFAULT); 
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
		
		//rotate the vertices through
		queueVertex(x,y);
		
		//set the physics body's vertices
		//polyLine.set(vertices);
		lineBody.createFixture(polyLine, 0);
		if(lineBody.getFixtureList().size()>=NUM_VERTICES)
		{
			lineBody.destroyFixture(lineBody.getFixtureList().get(0));
		}
		
		//update the shape's (drawable) vertices
		this.updateVertices(verticesX, verticesY);
	}
}
