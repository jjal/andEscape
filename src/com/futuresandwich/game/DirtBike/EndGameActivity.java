package com.futuresandwich.game.DirtBike;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.util.debug.Debug;

import android.app.Activity;

public class EndGameActivity extends BaseSplashActivity {

	private Music mMusic;
	public EndGameActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override public org.andengine.engine.options.EngineOptions onCreateEngineOptions() {
		EngineOptions options = super.onCreateEngineOptions();
		options.getAudioOptions().setNeedsMusic(true);
		return options;
	};
	
	@Override
	public void onCreateResources() {
		
		super.onCreateResources();
		MusicFactory.setAssetBasePath("mfx/");
		try {
			this.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "DayBreak.ogg");
			this.mMusic.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		Scene scene = super.onCreateScene(); 
		this.mMusic.play();
		return scene; 
	}
	
	@Override
	protected float getSplashDuration() {
		return -1;
	}

	@Override
	protected ScreenOrientation getScreenOrientation() {
		return ScreenOrientation.LANDSCAPE_FIXED;
	}

	@Override
	protected float getSplashScaleFrom() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Class<? extends Activity> getFollowUpActivity() {
		return Splash1Activity.class;
	}

	@Override
	protected String onGetSplashPath() {
		return "gameEnd.png";
	}

}
