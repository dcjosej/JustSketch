package com.tfg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.tfg.transitions.ScreenTransition;
import com.tfg.transitions.ScreenTransitionFade;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class CreditScreen extends AbstractScreen {

	private Stage stage;
	private Skin skinMenu;

	// ---------- Menu --------------------------

	// ------------- Debug ----------------------
	private boolean debugEnabled = false;
	private float debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
	private boolean drawDebug = false;

	public CreditScreen(DirectedGame game) {
		super(game);
	}

	private void rebuildStage() {

		System.out.println("Pintando de nuevo!");

		skinMenu = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.GUI_ATLAS));

		Table layerBackground = buildLayerBackground();
		Table layerCredits = buildLayerCredits();
		Table layerButtons = buildLayerButtons();
		Container<Label> labelThanks = buildContainerLabelThanks();

		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerCredits);
		stack.add(layerButtons);
		stack.add(labelThanks);
	}

	private Container<Label> buildContainerLabelThanks() {
		LabelStyle labelStyle = new LabelStyle(
				Assets.getBitmapFont(Constants.GUI_FONT_80), Color.BLACK);

		Label labelThanks = new Label("Thanks for playing!", labelStyle);
		
		Container<Label> res = new Container<Label>(labelThanks).top().padTop(80f);

		return res;
	}

	private void onBackClicked() {
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		game.setScreen(new MenuScreen(game), transition);
	}

	private Table buildLayerButtons() {

		Table layer = new Table();
		layer.bottom();

		Button btnBack = new Button(skinMenu, "back");
		layer.add(btnBack).width(170f).height(70f).padBottom(50f);

		btnBack.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackClicked();
			}
		});

		return layer;
	}

	private Table buildLayerCredits() {
		Table layer = new Table();
		LabelStyle labelStyle = new LabelStyle(
				Assets.getBitmapFont(Constants.GUI_FONT_60), Color.BLACK);

		Label labelProgrammingArt = new Label(
				"Programming and art: Jose J. Delgado", labelStyle);
		layer.add(labelProgrammingArt).center().center();

		layer.row();

		Label labelMusic = new Label("Music: DST", labelStyle);
		layer.add(labelMusic);

		return layer;
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
	public void hide() {
		stage.dispose();
		skinMenu.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}
}
