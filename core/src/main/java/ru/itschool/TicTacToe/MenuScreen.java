package ru.itschool.TicTacToe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuScreen implements Screen {
    private final TicTacToeGame game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    public MenuScreen(TicTacToeGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));

        titleFont = new BitmapFont(Gdx.files.internal("stylo.fnt"));
        buttonFont = new BitmapFont(Gdx.files.internal("stylo.fnt"));

        titleFont.getData().setScale(1.5f);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.up = createButtonDrawable(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        buttonStyle.down = createButtonDrawable(new Color(0.3f, 0.3f, 0.3f, 0.9f));
        buttonStyle.over = createButtonDrawable(new Color(0.25f, 0.25f, 0.25f, 0.8f));

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GRAY);

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Table buttonTable = new Table();
        buttonTable.defaults().pad(75);

        Label title = new Label("TIC TAC TOE", titleStyle);
        mainTable.add(title).padTop(10).row();

        TextButton pvpButton = new TextButton("2 Players", buttonStyle);
        TextButton aiButton = new TextButton("Play vs AI", buttonStyle);
        TextButton exitButton = new TextButton("Exit", buttonStyle);

        pvpButton.pad(15);
        aiButton.pad(15);
        exitButton.pad(15);

        buttonTable.add(pvpButton).minWidth(250).minHeight(70).padBottom(15).row();
        buttonTable.add(aiButton).minWidth(250).minHeight(70).padBottom(15).row();
        buttonTable.add(exitButton).minWidth(250).minHeight(70).padTop(80);

        mainTable.add(buttonTable);

        pvpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, false));
            }
        });

        aiButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, true));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(mainTable);
    }

    private TextureRegionDrawable createButtonDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        batch.dispose();
        backgroundTexture.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
