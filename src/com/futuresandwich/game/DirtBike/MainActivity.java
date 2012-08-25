package com.futuresandwich.game.DirtBike;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.hardware.SensorManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * @author Justin Lorenzon
 *
 */

// Code based on RealMayo example:
// RealMayo: THIS PROJECT IS SIMPLY THE CODE TAKEN FROM PhysicsExample.java FROM THE AndEngineExamples

public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mCircleFaceTextureRegion;
	private TiledTextureRegion mTriangleFaceTextureRegion;
	private TiledTextureRegion mHexagonFaceTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	private int mFaceCount = 0;
	
	
	private DynamicLine line;
	

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	private Camera camera;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Touch the screen to add objects.", Toast.LENGTH_LONG).show();

		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		options.getRenderOptions().setMultiSampling(true);
		return options;
	}

	@Override   
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
		this.mTriangleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_triangle_tiled.png", 0, 64, 2, 1); // 64x32
		this.mHexagonFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_hexagon_tiled.png", 0, 96, 2, 1); // 64x32
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle shelf = new Rectangle(0, 200, 300, 8, vertexBufferObjectManager);
		shelf.setColor(Color.BLUE);
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, shelf, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.attachChild(shelf);
		
		line = DynamicLine.CreateDynamicLine(this.mPhysicsWorld, this.getVertexBufferObjectManager());
		line.setColor(Color.CYAN);
		line.setLineWidth( 7f );
		
		this.mScene.attachChild(line);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
//TODO DEBUG DRAW DEBUGDRAW - TOGGLE ON OFF  	 		
	    mScene.attachChild(new Box2dDebugRenderer(mPhysicsWorld, getVertexBufferObjectManager()));	

		//this.addFace(0, 0);
		
		IEntity chassis = this.createCar(this.mScene,100,250);
		camera.setChaseEntity(chassis);
		return this.mScene;
	}
	
	private IEntity createCar(final Scene pScene,float x, float y) {
		final float centerX = x;
		final float centerY = y;

		final float spriteWidth = this.mBoxFaceTextureRegion.getWidth();
		final float spriteHeight = this.mBoxFaceTextureRegion.getHeight();

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(40, 0.1f, 1.0f);

		
		final float anchorFaceX = centerX - spriteWidth * 0.5f + 220 * (-1);
		final float anchorFaceY = centerY - spriteHeight * 0.5f;

		final IAreaShape chassisFace = new Rectangle(anchorFaceX, anchorFaceY,60.0f,10.0f, this.getVertexBufferObjectManager());
		final AnimatedSprite backMovingFace = new AnimatedSprite(anchorFaceX-(spriteWidth/2), anchorFaceY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
		final AnimatedSprite frontMovingFace = new AnimatedSprite(anchorFaceX+60+(spriteWidth/2), anchorFaceY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
		
		
		final Body backWheelBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, backMovingFace, BodyType.DynamicBody, objectFixtureDef);
		final Body frontWheelBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, frontMovingFace, BodyType.DynamicBody, objectFixtureDef);
		final Body chassisBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, chassisFace, BodyType.DynamicBody, objectFixtureDef);

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
		
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(chassisFace, chassisBody, true, true));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(backMovingFace, backWheelBody, true, true));
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(frontMovingFace, frontWheelBody, true, true));


		final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA = chassisBody;
		revoluteJointDef.bodyB = backWheelBody;
		revoluteJointDef.localAnchorA.set(-30.0f/PIXEL_TO_METER_RATIO_DEFAULT,0.0f);
		revoluteJointDef.localAnchorB.set(0.0f, 0.0f);
		revoluteJointDef.enableMotor = true;
		revoluteJointDef.motorSpeed = 10;
		revoluteJointDef.maxMotorTorque = 200;
		revoluteJointDef.collideConnected = false;
		this.mPhysicsWorld.createJoint(revoluteJointDef);
		
		final RevoluteJointDef revoluteJointDef2 = new RevoluteJointDef();
		revoluteJointDef2.bodyA = chassisBody;
		revoluteJointDef2.bodyB = frontWheelBody;
		revoluteJointDef2.localAnchorA.set(30.0f/PIXEL_TO_METER_RATIO_DEFAULT,0.0f);
		revoluteJointDef2.localAnchorB.set(0.0f, 0.0f);
		revoluteJointDef2.enableMotor = true;
		revoluteJointDef2.motorSpeed = 10;
		revoluteJointDef2.maxMotorTorque = 200;
		revoluteJointDef2.collideConnected = false;
		this.mPhysicsWorld.createJoint(revoluteJointDef2);
		
		return chassisFace;
	}

	private Vector2 lastTouch;
	private final float POINT_THRESHOLD = 6;
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		if(this.mPhysicsWorld != null) {
			Vector2 currentTouch = new Vector2(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			if(pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove() && lastTouch.dst(currentTouch)>POINT_THRESHOLD) {
				lastTouch = currentTouch;
				line.addVertex(currentTouch.x,currentTouch.y);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void addFace(final float pX, final float pY) {
		this.mFaceCount++;
		Debug.d("Faces: " + this.mFaceCount);

		final AnimatedSprite face;
		final Body body;

		if(this.mFaceCount % 4 == 0) {
			face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
			body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		} else if (this.mFaceCount % 4 == 1) {
			face = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
			body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		} else if (this.mFaceCount % 4 == 2) {
			face = new AnimatedSprite(pX, pY, this.mTriangleFaceTextureRegion, this.getVertexBufferObjectManager());
			body = MainActivity.createTriangleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		} else {
			face = new AnimatedSprite(pX, pY, this.mHexagonFaceTextureRegion, this.getVertexBufferObjectManager());
			body = MainActivity.createHexagonBody(this.mPhysicsWorld, face, BodyType.DynamicBody, FIXTURE_DEF);
		}

		face.animate(200);

		this.mScene.attachChild(face);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
	}

	/**
	 * Creates a {@link Body} based on a {@link PolygonShape} in the form of a triangle:
	 * <pre>
	 *  /\
	 * /__\
	 * </pre>
	 */
	private static Body createTriangleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		final float top = -halfHeight;
		final float bottom = halfHeight;
		final float left = -halfHeight;
		final float centerX = 0;
		final float right = halfWidth;

		final Vector2[] vertices = {
				new Vector2(centerX, top),
				new Vector2(right, bottom),
				new Vector2(left, bottom)
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	/**
	 * Creates a {@link Body} based on a {@link PolygonShape} in the form of a hexagon:
	 * <pre>
	 *  /\
	 * /  \
	 * |  |
	 * |  |
	 * \  /
	 *  \/
	 * </pre>
	 */
	private static Body createHexagonBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		/* The top and bottom vertex of the hexagon are on the bottom and top of hexagon-sprite. */
		final float top = -halfHeight;
		final float bottom = halfHeight;

		final float centerX = 0;

		/* The left and right vertices of the heaxgon are not on the edge of the hexagon-sprite, so we need to inset them a little. */
		final float left = -halfWidth + 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float right = halfWidth - 2.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float higher = top + 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float lower = bottom - 8.25f / PIXEL_TO_METER_RATIO_DEFAULT;

		final Vector2[] vertices = {
				new Vector2(centerX, top),
				new Vector2(right, higher),
				new Vector2(right, lower),
				new Vector2(centerX, bottom),
				new Vector2(left, lower),
				new Vector2(left, higher)
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
