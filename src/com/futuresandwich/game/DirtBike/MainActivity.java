package com.futuresandwich.game.DirtBike;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;


/**
 * @author Justin Lorenzon
 *
 */


public class MainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	private static final int MAX_LINES = 5;
	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;

	public TiledTextureRegion mBoxFaceTextureRegion;
	public TiledTextureRegion mCircleFaceTextureRegion;
	public TiledTextureRegion mTriangleFaceTextureRegion;
	public TiledTextureRegion mHexagonFaceTextureRegion;
	public ScrollableParallaxBackground parallaxBackground;
	
	private TiledTextureRegion back0;
	private TiledTextureRegion back1;
	private TiledTextureRegion back2;
	private TiledTextureRegion fireTex;
	
	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	private int mFaceCount = 0;
	private Font mFont;
	
	private LineRepository lines;
	

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
		

		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		options.getRenderOptions().setMultiSampling(true);
		return options;
	}

	@Override   
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 800, 2048, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 1, 1); // 64x32
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
		this.mTriangleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_triangle_tiled.png", 0, 64, 2, 1); // 64x32
		this.mHexagonFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_hexagon_tiled.png", 0, 96, 2, 1); // 64x32
		
		back0 =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "backlight.png", 0, 128, 1, 1); // 800x480 or something
		back1 =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "backdebris0.png", 0, 608, 1, 1); 
		back2 =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "backdebris1.png", 0, 1088, 1, 1); 
		fireTex =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "fire.png", 0, 1568, 1, 1); 
		
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
		
		this.mBitmapTextureAtlas.load();
		
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		//ParallaxLayer parallaxLayer = new ParallaxLayer(camera, true);
		parallaxBackground = new ScrollableParallaxBackground(camera, CAMERA_WIDTH, CAMERA_HEIGHT,this.mEngine);
		parallaxBackground.setParallaxChangePerSecond(0);
		parallaxBackground.setParallaxValue(1);
		
		
		AnimatedSprite back0Sprite = new AnimatedSprite(0,0, back0.getWidth(),back0.getHeight(),back0,this.getVertexBufferObjectManager());
		AnimatedSprite back1Sprite = new AnimatedSprite(0,-10, back0.getWidth(),back1.getHeight(),back1,this.getVertexBufferObjectManager());
		AnimatedSprite back2Sprite = new AnimatedSprite(0,-15, back0.getWidth(),back2.getHeight(),back2,this.getVertexBufferObjectManager());
		
		
		parallaxBackground.attachParallaxEntity(new ParallaxEntity(0,back0Sprite));
		parallaxBackground.attachParallaxEntity(new ParallaxEntity(-5,back1Sprite));
		parallaxBackground.attachParallaxEntity(new ParallaxEntity(-15,back2Sprite));
		
		this.mScene.setBackground(parallaxBackground);


		float levelWidth = 8000f;
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, levelWidth, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, levelWidth, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(levelWidth-2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle shelf = new Rectangle(0, 160, 300, 8, vertexBufferObjectManager);
		roof.setColor(Color.TRANSPARENT);
		left.setColor(Color.TRANSPARENT);
		ground.setColor(Color.TRANSPARENT);
		right.setColor(Color.YELLOW);
		shelf.setColor(new Color(0.2f,0.2f,0.2f));
		
		//fire at the bottom
		final AnimatedSprite fireSprite = new AnimatedSprite(0,CAMERA_HEIGHT-170, fireTex.getWidth(),fireTex.getHeight(),fireTex,this.getVertexBufferObjectManager());
		fireSprite.setZIndex(1000);
		final AnimatedSprite fireWallSprite = new AnimatedSprite(-500,0, fireTex.getWidth(),fireTex.getHeight(),fireTex,this.getVertexBufferObjectManager());
		fireWallSprite.setZIndex(999);
		fireWallSprite.setRotation(90.0f);
		final Rectangle fireWallBox = new Rectangle(-2*CAMERA_WIDTH,-410,CAMERA_WIDTH,CAMERA_HEIGHT*2,vertexBufferObjectManager);
		fireWallBox.setZIndex(999);
		fireWallBox.setColor(new Color(1.0f,218.0f/255.0f,0));
		
		MoveXModifier moveX = new MoveXModifier(60,-500,(int)levelWidth);
		MoveXModifier moveXBox = new MoveXModifier(60,-1*CAMERA_WIDTH-210,(int)levelWidth-CAMERA_WIDTH-210+500);
		fireWallSprite.registerEntityModifier(moveX);
		fireWallBox.registerEntityModifier(moveXBox);
		
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, shelf, BodyType.StaticBody, wallFixtureDef);
		
		Body groundBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		Body endBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		groundBody.setUserData("ground");
		endBody.setUserData("endWall");
		
		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.attachChild(shelf);
		this.mScene.attachChild(fireSprite);
		this.mScene.attachChild(fireWallSprite);
		this.mScene.attachChild(fireWallBox);
				
//TODO DEBUG DRAW DEBUGDRAW - TOGGLE ON OFF  	 		
	    //mScene.attachChild(new Box2dDebugRenderer(mPhysicsWorld, getVertexBufferObjectManager()));	
		
		final AnimatedSprite chassis = (AnimatedSprite)CarFactory.createCar(this.mScene,280,80,this.mPhysicsWorld, this.getVertexBufferObjectManager(), this);
		camera.setChaseEntity(chassis);
		
		lines = new LineRepository(MAX_LINES);
		
		this.mScene.sortChildren();
		
		this.mScene.registerUpdateHandler(new IUpdateHandler()
		{
			@Override
			public void onUpdate(float pSecondsElapsed) {
				fireSprite.setPosition(camera.getXMin(), CAMERA_HEIGHT-160);
				if(fireWallSprite.getX() >= (chassis.getX()-320)) //i don't know why 
				{
					die();
				}
					
			}
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mPhysicsWorld.setContactListener(createContactListener());
		this.mScene.registerUpdateHandler(getCollisionUpdateHandler());
		return this.mScene;
	}
	
	private void addLine(float x, float y)
	{
		DynamicLine line = DynamicLine.CreateDynamicLine(x, y, this.mPhysicsWorld, this.getVertexBufferObjectManager());
		line.setColor(new Color(0.8f,0.8f,0.8f));
		line.setLineWidth( 7f );
		this.mScene.attachChild(line);
		DynamicLine removed = this.lines.addLine(line);
		if(removed != null)
		{
			this.mEngine.getScene().detachChild(removed);
			removed.dispose();
			removed = null;
		}
	}
	
	private ContactListener createContactListener()
	{
		ContactListener listener = new ContactListener()
		{
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB(); 
				if(AreContacting(x1,x2,"car","endWall"))
				{
					endLevel();
				}else
				if(AreContacting(x1, x2, "car","ground"))
				{
					die();
				}
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
			protected boolean AreContacting(Fixture x1, Fixture x2, String body1, String body2)
			{
				return (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
						&& ((x1.getBody().getUserData().equals(body1) && x2.getBody().getUserData().equals(body2))
						|| (x2.getBody().getUserData().equals(body1) && x1.getBody().getUserData().equals(body2)));
			}
		};
		return listener;
	}
	boolean isWin=false;
	boolean isLose=false;
	public IUpdateHandler getCollisionUpdateHandler()
	{
		return new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
					
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	protected void die()
	{
		if(!isLose)
		{
			isLose = true;
			this.runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			    	MainActivity.this.showMessage("You died in a fire.");
			    	Intent intent = new Intent(MainActivity.this,  MainActivity.class);
	    			startActivity(intent);
	    			finish();
			    }
			});
		}
		
	}
	
	protected void endLevel() {
		if(!isWin)
		{
			isWin = true;
			this.runOnUiThread(new Runnable() {
			    @Override
			    public void run() {
			    	MainActivity.this.showMessage("You win!");
			    	Intent intent = new Intent(MainActivity.this,  EndGameActivity.class);
	    			startActivity(intent);
	    			finish();
			    }
			});
		}
	}
	
	public void showMessage(CharSequence text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	

	private Vector2 lastTouch;
	private final float POINT_THRESHOLD = 6;
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		if(this.mPhysicsWorld != null) {
			Vector2 currentTouch = new Vector2(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			
			if(pSceneTouchEvent.isActionDown())
			{
				lastTouch = currentTouch;
				addLine(currentTouch.x,currentTouch.y);
			}
			else if(pSceneTouchEvent.isActionMove() && lastTouch.dst(currentTouch)>POINT_THRESHOLD) {
				lastTouch = currentTouch;
				lines.getLatest().addVertex(currentTouch.x,currentTouch.y);
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
		//final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		//this.mPhysicsWorld.setGravity(gravity);
		//Vector2Pool.recycle(gravity);
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
