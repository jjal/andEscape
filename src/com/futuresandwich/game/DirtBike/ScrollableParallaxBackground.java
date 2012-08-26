package com.futuresandwich.game.DirtBike;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.AutoParallaxBackground;


public class ScrollableParallaxBackground extends AutoParallaxBackground {

	private float cameraPreviousX;

	private float cameraOffsetX;

	private Camera camera;

	private Engine engine;


	public ScrollableParallaxBackground(Camera camera, int xSize, int ySize,
			Engine mEngine) {

		super(0, 0, 0, 1);

		this.camera = camera;

		engine = mEngine;

		cameraPreviousX = camera.getCenterX();

	}

	public void updateScrollEvents() {

		if (cameraPreviousX != this.camera.getCenterX()) {

			cameraOffsetX = cameraPreviousX - this.camera.getCenterX();

			cameraPreviousX = this.camera.getCenterX();

		}

	}

	@Override
	public void onUpdate(float pSecondsElapsed) {

		super.onUpdate(pSecondsElapsed);

		this.mParallaxValue += (cameraOffsetX * 2) * pSecondsElapsed;

		cameraOffsetX = 0;

	}

}