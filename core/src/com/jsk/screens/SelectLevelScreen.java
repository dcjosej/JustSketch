package com.jsk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.jsk.transitions.ScreenTransition;
import com.jsk.transitions.ScreenTransitionSlide;
import com.jsk.utils.Assets;
import com.jsk.utils.Constants;
import com.jsk.utils.GameManager;
import com.jsk.utils.GamePreferences;

public class SelectLevelScreen extends AbstractScreen {

	private Stage stage;
	private Skin skinMenu;

	// ---------- Menu --------------------------

	// ------------- Debug ----------------------
	private boolean debugEnabled = false;
	private float debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
	private boolean drawDebug = false;

	public SelectLevelScreen(DirectedGame game) {
		super(game);
	}

	private void rebuildStage() {

		skinMenu = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.GUI_ATLAS));

		Table layerBackground = buildLayerBackground();
		Table layerLevels = buildLayerLevels();

		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerLevels);
	}

	private Table buildLayerLevels() {
		Table layer = new Table();

		for (int i = 1; i <= 6; i++) {

			Table tableButton = new Table();

			String styleName = "levelDisabled";
			if (i <= GamePreferences.instance.numLevelsActive) {
				styleName = "levelActive";
			}

			// tableButton.setBackground(new
			// TextureRegionDrawable(Assets.getGUITextureAtlas().findRegion(region)));

			ImageButton imgb = new ImageButton(skinMenu, styleName);
			imgb.addActor(tableButton);
			tableButton.setPosition(175f, 175f);
			layer.add(imgb).padRight(40f);

			final int levelButton = i;

			if (styleName == "levelActive") {
				imgb.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent event, float x, float y) {
						onLevelClicked(levelButton);
					}
				});
			}

			// layer.add(tableButton).padRight(40f);

			// LabelStyle labelStyle = new LabelStyle(
			// Utils.getFont(40, "DJGROSS"), Color.BLACK);
			LabelStyle labelStyle = new LabelStyle(
					Assets.getBitmapFont(Constants.GUI_FONT_44), Color.BLACK);
			Label lbNumLevel = new Label("Level " + i, labelStyle);
			tableButton.add(lbNumLevel);

			// labelStyle = new LabelStyle(Utils.getFont(30, "DJGROSS"),
			// Color.BLACK);
			labelStyle = new LabelStyle(
					Assets.getBitmapFont(Constants.GUI_FONT_40), Color.BLACK);

			Label lbBestScore = new Label("Best Score", labelStyle);
			tableButton.row();
			tableButton.add(lbBestScore).padTop(120f);

			// labelStyle = new LabelStyle(Utils.getFont(30, "DJGROSS"),
			// Color.BLACK);
			Label lbScore = new Label(""
					+ GamePreferences.instance.getBestScore(i), labelStyle);
			tableButton.row();
			tableButton.add(lbScore).padTop(25f);

			if (i == 3) {
				layer.row().padTop(60f);
			}
		}

		return layer;
	}

	private void onLevelClicked(int levelToLoad) {
		GameManager.getInstance().currentLevel = levelToLoad;
		ScreenTransition transition = ScreenTransitionSlide.init(1f,
				ScreenTransitionSlide.UP, true, Interpolation.linear);
		game.setScreen(new GameScreen(game), transition);
	}

	private Table buildLayerBackground() {
		Table layer = new Table();

		Image imgBackground = new Image(skinMenu, "background");
		layer.add(imgBackground);
		return layer;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (debugEnabled) {
			debugRebuildStage -= delta;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}

		stage.act(delta);
		stage.draw();
		stage.setDebugAll(false);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		stage = new Stage(new FillViewport(Constants.APP_WIDTH,
				Constants.APP_HEIGHT));
		rebuildStage();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}
}
