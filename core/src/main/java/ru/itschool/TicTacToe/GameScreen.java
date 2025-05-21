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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class GameScreen implements Screen {
    private final TicTacToeGame game;
    private final boolean vsAI;
    private final int fieldSize;

    private Texture boardTexture;
    private Texture xTexture;
    private Texture oTexture;
    private SpriteBatch batch;
    private char[][] board;
    private char currentPlayer;
    private Rectangle[][] cells;
    private GameState gameState;

    private Stage uiStage;
    private BitmapFont font;
    private Label statusLabel;
    private TextButton restartButton;
    private TextButton menuButton;

    public GameScreen(TicTacToeGame game, boolean vsAI, int fieldSize) {
        this.game = game;
        this.vsAI = vsAI;
        this.fieldSize = fieldSize;
        resetGame();
    }

    private void resetGame() {
        this.board = new char[fieldSize][fieldSize];
        this.cells = new Rectangle[fieldSize][fieldSize];
        this.currentPlayer = 'X';
        this.gameState = GameState.PLAYING;
    }

    @Override
    public void show() {
        switch(fieldSize) {
            case 3: boardTexture = new Texture(Gdx.files.internal("board3x3.png")); break;
            case 4: boardTexture = new Texture(Gdx.files.internal("board4x4.png")); break;
            case 5: boardTexture = new Texture(Gdx.files.internal("board5x5.png")); break;
            default: boardTexture = new Texture(Gdx.files.internal("board3x3.png"));
        }

        xTexture = new Texture(Gdx.files.internal("X.png"));
        oTexture = new Texture(Gdx.files.internal("O.png"));
        font = new BitmapFont(Gdx.files.internal("stylo.fnt"), false);

        batch = new SpriteBatch();

        int boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float cellSize = boardSize / (float)fieldSize;
        float boardX = (float)(Gdx.graphics.getWidth() - boardSize) / 2;
        float boardY = (float)(Gdx.graphics.getHeight() - boardSize) / 2;

        for (int row = 0; row < fieldSize; row++) {
            for (int col = 0; col < fieldSize; col++) {
                cells[row][col] = new Rectangle(
                    boardX + col * cellSize,
                    boardY + row * cellSize,
                    cellSize,
                    cellSize
                );
            }
        }

        uiStage = new Stage();
        Gdx.input.setInputProcessor(uiStage);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        statusLabel = new Label("Turn: X", labelStyle);
        statusLabel.setAlignment(Align.right);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = createButtonDrawable(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        buttonStyle.down = createButtonDrawable(new Color(0.3f, 0.3f, 0.3f, 0.9f));
        buttonStyle.over = createButtonDrawable(new Color(0.25f, 0.25f, 0.25f, 0.8f));

        restartButton = new TextButton("Restart", buttonStyle);
        restartButton.pad(10, 15, 10, 15);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetGame();
                statusLabel.setText("Turn: X");
                restartButton.setVisible(false);
                menuButton.setVisible(false);
            }
        });

        menuButton = new TextButton("Menu", buttonStyle);
        menuButton.pad(10, 15, 10, 15);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        restartButton.setVisible(false);
        menuButton.setVisible(false);

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().right().pad(20);

        Table rightPanel = new Table();
        rightPanel.add(statusLabel).right().padBottom(10).row();

        Table buttonTable = new Table();
        buttonTable.add(restartButton).padRight(10);
        buttonTable.add(menuButton);

        rightPanel.add(buttonTable).right();
        uiTable.add(rightPanel).right();

        uiStage.addActor(uiTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        int boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float boardX = (float)(Gdx.graphics.getWidth() - boardSize) / 2;
        float boardY = (float)(Gdx.graphics.getHeight() - boardSize) / 2;

        batch.draw(boardTexture, boardX, boardY, boardSize, boardSize);

        float cellSize = boardSize / (float)fieldSize;
        float padding = cellSize * 0.1f;

        for (int row = 0; row < fieldSize; row++) {
            for (int col = 0; col < fieldSize; col++) {
                if (board[row][col] == 'X') {
                    batch.draw(xTexture,
                        boardX + col * cellSize + padding,
                        boardY + row * cellSize + padding,
                        cellSize - 2*padding,
                        cellSize - 2*padding);
                } else if (board[row][col] == 'O') {
                    batch.draw(oTexture,
                        boardX + col * cellSize + padding,
                        boardY + row * cellSize + padding,
                        cellSize - 2*padding,
                        cellSize - 2*padding);
                }
            }
        }
        batch.end();

        if (Gdx.input.justTouched() && gameState == GameState.PLAYING) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            handleTouch(touchPos);
        }

        if (vsAI && currentPlayer == 'O' && gameState == GameState.PLAYING) {
            makeAIMove();
        }

        uiStage.act(delta);
        uiStage.draw();
    }

    private void handleTouch(Vector3 touchPos) {
        int boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float boardX = (float)(Gdx.graphics.getWidth() - boardSize) / 2;
        float boardY = (float)(Gdx.graphics.getHeight() - boardSize) / 2;

        if (touchPos.x < boardX || touchPos.x > boardX + boardSize ||
            touchPos.y < boardY || touchPos.y > boardY + boardSize) {
            return;
        }

        int col = (int)((touchPos.x - boardX) / (boardSize / (float)fieldSize));
        int row = fieldSize - 1 - (int)((touchPos.y - boardY) / (boardSize / (float)fieldSize));

        if (row >= 0 && row < fieldSize && col >= 0 && col < fieldSize && board[row][col] == 0) {
            board[row][col] = currentPlayer;
            checkGameState();
            if (gameState == GameState.PLAYING) {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                statusLabel.setText("Turn: " + currentPlayer);
            }
        }
    }

    private void makeAIMove() {
        for (int row = 0; row < fieldSize; row++) {
            for (int col = 0; col < fieldSize; col++) {
                if (board[row][col] == 0) {
                    board[row][col] = 'O';
                    currentPlayer = 'X';
                    statusLabel.setText("Turn: X");
                    checkGameState();
                    return;
                }
            }
        }
    }

    private boolean checkLine(int startRow, int startCol, int rowDelta, int colDelta, char player) {
        for (int i = 0; i < fieldSize; i++) {
            int row = startRow + i * rowDelta;
            int col = startCol + i * colDelta;
            if (row < 0 || row >= fieldSize || col < 0 || col >= fieldSize || board[row][col] != player) {
                return false;
            }
        }
        return true;
    }

    private void checkGameState() {
        for (int i = 0; i < fieldSize; i++) {
            if (checkLine(i, 0, 0, 1, currentPlayer)) {
                endGame(currentPlayer == 'X' ? GameState.X_WON : GameState.O_WON);
                return;
            }
            if (checkLine(0, i, 1, 0, currentPlayer)) {
                endGame(currentPlayer == 'X' ? GameState.X_WON : GameState.O_WON);
                return;
            }
        }

        if (checkLine(0, 0, 1, 1, currentPlayer) ||
            checkLine(0, fieldSize-1, 1, -1, currentPlayer)) {
            endGame(currentPlayer == 'X' ? GameState.X_WON : GameState.O_WON);
            return;
        }

        boolean isDraw = true;
        for (int row = 0; row < fieldSize; row++) {
            for (int col = 0; col < fieldSize; col++) {
                if (board[row][col] == 0) {
                    isDraw = false;
                    break;
                }
            }
            if (!isDraw) break;
        }

        if (isDraw) {
            endGame(GameState.DRAW);
        }
    }

    private void endGame(GameState state) {
        gameState = state;
        switch (state) {
            case X_WON:
                statusLabel.setText("X wins!");
                break;
            case O_WON:
                statusLabel.setText("O wins!");
                break;
            case DRAW:
                statusLabel.setText("Draw!");
                break;
        }
        restartButton.setVisible(true);
        menuButton.setVisible(true);
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
    public void dispose() {
        batch.dispose();
        boardTexture.dispose();
        xTexture.dispose();
        oTexture.dispose();
        font.dispose();
        uiStage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}

enum GameState {
    PLAYING, X_WON, O_WON, DRAW
}
