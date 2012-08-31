package com.futuresandwich.game.DirtBike;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import android.app.Activity;
import android.content.Intent;

/**
 * SplashScreenActivity v2 for GLES2
 * @author Knoll Florian (myfknoll)
 *
 */
public abstract class BaseSplashActivity extends SimpleBaseGameActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH = 720;
	private static int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mSplashImageTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		if(getScreenOrientation()==ScreenOrientation.PORTRAIT_FIXED||
				getScreenOrientation()==ScreenOrientation.PORTRAIT_SENSOR){			
			CAMERA_WIDTH = 480;
			CAMERA_HEIGHT = 720;			
		}

		return new EngineOptions(true, getScreenOrientation(), new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(1024,512, TextureOptions.BILINEAR);
		this.mSplashImageTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, onGetSplashPath(), 0, 0);
		this.mBitmapTextureAtlas.load(this.getTextureManager());
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0,0,0));

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mSplashImageTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mSplashImageTextureRegion.getHeight()) / 2;

		/* Create the face and add it to the scene. */
		final Sprite splashimage = new Sprite(centerX, centerY, this.mSplashImageTextureRegion);
	
		SequenceEntityModifier animation = new SequenceEntityModifier(
				new DelayModifier(0.5f),
				new ParallelEntityModifier(
						new ScaleModifier(getSplashDuration(),getSplashScaleFrom(),1),
						new AlphaModifier(getSplashDuration(), 0,1)
				),
				new DelayModifier(0.5f)
				
			
		);
		animation.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
				BaseSplashActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
					}
				});
			}

			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
				BaseSplashActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
			
						Intent intent = new Intent(BaseSplashActivity.this,  getFollowUpActivity());
            			startActivity(intent);
            			finish();
					}
				});
			}
		});		
		splashimage.setAlpha(0);
		splashimage.setScale(getSplashScaleFrom());
		splashimage.registerEntityModifier(animation);		
		scene.attachChild(splashimage);

		return scene;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	protected abstract float getSplashDuration();
	protected abstract ScreenOrientation getScreenOrientation();
	protected abstract float getSplashScaleFrom();
	protected abstract Class<? extends Activity> getFollowUpActivity();
	protected abstract String onGetSplashPath();

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}

