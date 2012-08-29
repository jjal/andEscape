package com.futuresandwich.game.DirtBike;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.ui.activity.BaseGameActivity;

public class SplashScreenActivity extends BaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Final Fields
	// ===========================================================
 
 
	// ===========================================================
	// Constants
	// ===========================================================
 
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 800;
 
	// ===========================================================
	// Fields
	// ===========================================================
 
	protected Camera mCamera;
 
	// <span class="posthilit">splash</span> scene
	protected Scene mSplashScene;
	private BitmapTextureAtlas mSplashBackgroundTextureAtlas;
	private ITextureRegion mSplashBackgroundTextureRegion;
 
	// menu scene
	protected Scene mMenuScene;
	// ...
 
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
 
 
		public EngineOptions onCreateEngineOptions() {
			this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
			return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		}
 
		@Override
		public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			mSplashBackgroundTextureAtlas = new BitmapTextureAtlas(512, 1024, TextureOptions.NEAREST);
			mSplashBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSplashBackgroundTextureAtlas, this, "splash.png", 0, 0);
			mEngine.getTextureManager().loadTexture(mSplashBackgroundTextureAtlas);
			//this.mSplashBackgroundTextureAtlas.load(this.getTextureManager());
			
			pOnCreateResourcesCallback.onCreateResourcesFinished();
		}


		@Override
		public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
			mSplashScene = new Scene();
			mSplashScene.setBackgroundEnabled(false);
			mSplashScene.attachChild(new Sprite(0, 0, mSplashBackgroundTextureRegion, this.getVertexBufferObjectManager()));

			pOnCreateSceneCallback.onCreateSceneFinished(this.mSplashScene);
		}


		@Override
		public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
			mEngine.registerUpdateHandler(new TimerHandler(0.01f, new ITimerCallback() {
				public void onTimePassed(final TimerHandler pTimerHandler) {
					mEngine.unregisterUpdateHandler(pTimerHandler);
					loadResources();
					loadScenes();
					
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					
					mEngine.setScene(mMenuScene);
				}
			}));
			
			pOnPopulateSceneCallback.onPopulateSceneFinished();
		}

	// ===========================================================
	// Methods
	// ===========================================================
 
	public void loadResources() {

		// menu resources
		// help resources
		// pick level resources
		// game resources
	}
 
	public void loadScenes() {

		mMenuScene = new Scene();
		mMenuScene.setBackground(new Background(0,45,66));
 
		// help scene
		// pick level scene
		// game scene
	}


	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}