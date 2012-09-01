package com.futuresandwich.game.DirtBike;

import org.andengine.engine.options.EngineOptions.ScreenOrientation;

import android.app.Activity;

public class Splash1Activity extends BaseSplashActivity {

	@Override
	protected float getSplashDuration() {
		return 20;
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
		return Splash2Activity.class;
	}

	@Override
	protected String onGetSplashPath() {
		return "splash-title.png";
	}

}
