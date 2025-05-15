package ru.itschool.TicTacToe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class MenuScreen implements Screen {
    private final TicTacToeGame game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Window sizeSelectionWindow;

    public MenuScreen(TicTacToeGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));

        titleFont = new BitmapFont(Gdx.files.internal("stylo.fnt"));
        buttonFont = new BitmapFont(Gdx.files.internal("stylo.fnt"));

        titleFont.setColor(Color.GRAY);
        titleFont.getData().setScale(1.5f);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.up = createButtonDrawable(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        buttonStyle.down = createButtonDrawable(new Color(0.3f, 0.3f, 0.3f, 0.9f));
        buttonStyle.over = createButtonDrawable(new Color(0.25f, 0.25f, 0.25f, 0.8f));

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label title = new Label("TIC TAC TOE", new Label.LabelStyle(titleFont, Color.GRAY));
        mainTable.add(title).padTop(50).row();

        Table buttonTable = new Table();
        buttonTable.defaults().uniformX().pad(10);

        TextButton pvpButton = new TextButton("2 Players", buttonStyle);
        TextButton aiButton = new TextButton("Play vs AI", buttonStyle);
        TextButton exitButton = new TextButton("Exit", buttonStyle);

        pvpButton.pad(20);
        aiButton.pad(20);
        exitButton.pad(20);

        buttonTable.add(pvpButton).padBottom(15).row();
        buttonTable.add(aiButton).padBottom(15).row();
        buttonTable.add(exitButton).padTop(20);

        mainTable.add(buttonTable).expandY().padBottom(50);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = buttonFont;
        windowStyle.titleFontColor = Color.YELLOW;
        windowStyle.background = createButtonDrawable(new Color(0.1f, 0.1f, 0.1f, 0.95f));

        sizeSelectionWindow = new Window("", windowStyle);
        sizeSelectionWindow.setModal(true);
        sizeSelectionWindow.setVisible(false);

        Label sizeLabel = new Label("Choose field size", new Label.LabelStyle(buttonFont, Color.GRAY));
        sizeLabel.setAlignment(Align.center);

        TextButton size3Button = new TextButton("3x3", buttonStyle);
        TextButton size4Button = new TextButton("4x4", buttonStyle);
        TextButton size5Button = new TextButton("5x5", buttonStyle);

        size3Button.pad(15);
        size4Button.pad(15);
        size5Button.pad(15);

        Table windowTable = new Table();
        windowTable.defaults().uniformX().pad(10);
        windowTable.add(sizeLabel).colspan(3).padBottom(15).row();
        windowTable.add(size3Button).padRight(15);
        windowTable.add(size4Button).padRight(15);
        windowTable.add(size5Button);

        sizeSelectionWindow.add(windowTable).pad(20);
        sizeSelectionWindow.pack();
        sizeSelectionWindow.setPosition(
            (float) Gdx.graphics.getWidth() /2 - sizeSelectionWindow.getWidth()/2,
            (float) Gdx.graphics.getHeight() /2 - sizeSelectionWindow.getHeight()/2
        );

        pvpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showSizeSelectionWindow(false);
            }
        });

        aiButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showSizeSelectionWindow(true);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        size3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame(3, sizeSelectionWindow.getUserObject() == Boolean.TRUE);
            }
        });

        size4Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame(4, sizeSelectionWindow.getUserObject() == Boolean.TRUE);
            }
        });

        size5Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame(5, sizeSelectionWindow.getUserObject() == Boolean.TRUE);
            }
        });

        stage.addActor(mainTable);
        stage.addActor(sizeSelectionWindow);
    }

    private void showSizeSelectionWindow(boolean vsAI) {
        sizeSelectionWindow.setUserObject(vsAI);
        sizeSelectionWindow.setVisible(true);
        sizeSelectionWindow.toFront();
    }

    private void startGame(int fieldSize, boolean vsAI) {
        sizeSelectionWindow.setVisible(false);
        game.setScreen(new GameScreen(game, vsAI, fieldSize));
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
