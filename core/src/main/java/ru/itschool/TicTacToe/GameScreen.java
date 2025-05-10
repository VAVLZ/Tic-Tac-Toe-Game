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

    // Текстуры
    private Texture boardTexture;
    private Texture xTexture;
    private Texture oTexture;

    // Игровые переменные
    private SpriteBatch batch;
    private char[][] board;
    private char currentPlayer;
    private Rectangle[][] cells;
    private GameState gameState;

    // UI элементы
    private Stage uiStage;
    private BitmapFont font;
    private Label statusLabel;
    private TextButton restartButton;
    private TextButton menuButton;

    public GameScreen(TicTacToeGame game, boolean vsAI) {
        this.game = game;
        this.vsAI = vsAI;
        resetGame();
    }

    private void resetGame() {
        this.board = new char[3][3];
        this.cells = new Rectangle[3][3];
        this.currentPlayer = 'X';
        this.gameState = GameState.PLAYING;
    }

    @Override
    public void show() {
        // Загрузка текстур
        boardTexture = new Texture(Gdx.files.internal("board.png"));
        xTexture = new Texture(Gdx.files.internal("X.png"));
        oTexture = new Texture(Gdx.files.internal("O.png"));
        font = new BitmapFont(Gdx.files.internal("stylo.fnt"), false);

        batch = new SpriteBatch();

        // Инициализация игрового поля
        int boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float cellSize = boardSize / 3f;
        float boardX = (float) (Gdx.graphics.getWidth() - boardSize) / 2;
        float boardY = (float) (Gdx.graphics.getHeight() - boardSize) / 2;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col] = new Rectangle(
                    boardX + col * cellSize,
                    boardY + row * cellSize,
                    cellSize,
                    cellSize
                );
            }
        }

        // Создание UI
        uiStage = new Stage();
        Gdx.input.setInputProcessor(uiStage);

        // Стиль для текста
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        statusLabel = new Label("Turn: X", labelStyle);
        statusLabel.setAlignment(Align.right);

        // Создаем текстуры для кнопок программно
        TextureRegionDrawable buttonUp = createButtonDrawable(Color.DARK_GRAY);
        TextureRegionDrawable buttonDown = createButtonDrawable(Color.GRAY);

        // Стиль для кнопок
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = buttonUp;
        buttonStyle.down = buttonDown;

        // Кнопка рестарта
        restartButton = new TextButton("Restart", buttonStyle);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetGame();
                statusLabel.setText("Turn: X");
                restartButton.setVisible(false);
                menuButton.setVisible(false);
            }
        });

        // Кнопка возврата в меню
        menuButton = new TextButton("To menu", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        restartButton.setVisible(false);
        menuButton.setVisible(false);

        // Компоновка UI с адаптацией под размер экрана
        float buttonWidth = Gdx.graphics.getWidth() * 0.2f; // 20% ширины экрана
        float buttonHeight = buttonWidth * 0.4f; // Соотношение сторон

        Table buttonTable = new Table();
        buttonTable.defaults().pad(5).width(buttonWidth).height(buttonHeight);
        buttonTable.add(restartButton).row();
        buttonTable.add(menuButton);

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().right().pad(10); // Уменьшенный отступ
        uiTable.add(statusLabel).right().padBottom(5).row();
        uiTable.add(buttonTable).right();

        uiStage.addActor(uiTable);
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

        // Отрисовка игрового поля
        batch.begin();
        int boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float boardX = (float) (Gdx.graphics.getWidth() - boardSize) / 2;
        float boardY = (float) (Gdx.graphics.getHeight() - boardSize) / 2;

        batch.draw(boardTexture, boardX, boardY, boardSize, boardSize);

        float cellSize = boardSize / 3f;
        float padding = cellSize * 0.1f;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
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

        // Обработка ввода
        if (Gdx.input.justTouched() && gameState == GameState.PLAYING) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            handleTouch(touchPos);
        }

        // Ход ИИ
        if (vsAI && currentPlayer == 'O' && gameState == GameState.PLAYING) {
            makeAIMove();
        }

        // Отрисовка UI
        uiStage.act(delta);
        uiStage.draw();
    }

    private void handleTouch(Vector3 touchPos) {
        int boardSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float boardX = (Gdx.graphics.getWidth() - boardSize) / 2;
        float boardY = (Gdx.graphics.getHeight() - boardSize) / 2;

        if (touchPos.x < boardX || touchPos.x > boardX + boardSize ||
            touchPos.y < boardY || touchPos.y > boardY + boardSize) {
            return;
        }

        int col = (int)((touchPos.x - boardX) / (boardSize / 3f));
        int row = (int)((touchPos.y - boardY) / (boardSize / 3f));
        row = 2 - row;

        if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == 0) {
            board[row][col] = currentPlayer;
            checkGameState();
            if (gameState == GameState.PLAYING) {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                statusLabel.setText("Turn: " + currentPlayer);
            }
        }
    }

    private void makeAIMove() {
        // 1. Проверка на победу ИИ
        int[] winMove = findWinningMove('O');
        if (winMove != null) {
            board[winMove[0]][winMove[1]] = 'O';
            endGame(GameState.O_WON);
            return;
        }

        // 2. Блокировка игрока
        int[] blockMove = findWinningMove('X');
        if (blockMove != null) {
            board[blockMove[0]][blockMove[1]] = 'O';
            currentPlayer = 'X';
            statusLabel.setText("Turn: X");
            return;
        }

        // 3. Захват центра
        if (board[1][1] == 0) {
            board[1][1] = 'O';
            currentPlayer = 'X';
            statusLabel.setText("Turn: X");
            return;
        }

        // 4. Случайный ход
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == 0) {
                    board[row][col] = 'O';
                    currentPlayer = 'X';
                    statusLabel.setText("Turn: X");
                    return;
                }
            }
        }
    }

    private int[] findWinningMove(char player) {
        for (int i = 0; i < 3; i++) {
            // Горизонтали
            if (board[i][0] == player && board[i][1] == player && board[i][2] == 0)
                return new int[]{i, 2};
            if (board[i][0] == player && board[i][2] == player && board[i][1] == 0)
                return new int[]{i, 1};
            if (board[i][1] == player && board[i][2] == player && board[i][0] == 0)
                return new int[]{i, 0};

            // Вертикали
            if (board[0][i] == player && board[1][i] == player && board[2][i] == 0)
                return new int[]{2, i};
            if (board[0][i] == player && board[2][i] == player && board[1][i] == 0)
                return new int[]{1, i};
            if (board[1][i] == player && board[2][i] == player && board[0][i] == 0)
                return new int[]{0, i};
        }

        // Диагонали
        if (board[0][0] == player && board[1][1] == player && board[2][2] == 0)
            return new int[]{2, 2};
        if (board[0][0] == player && board[2][2] == player && board[1][1] == 0)
            return new int[]{1, 1};
        if (board[1][1] == player && board[2][2] == player && board[0][0] == 0)
            return new int[]{0, 0};

        if (board[0][2] == player && board[1][1] == player && board[2][0] == 0)
            return new int[]{2, 0};
        if (board[0][2] == player && board[2][0] == player && board[1][1] == 0)
            return new int[]{1, 1};
        if (board[1][1] == player && board[2][0] == player && board[0][2] == 0)
            return new int[]{0, 2};

        return null;
    }

    private void checkGameState() {
        // Проверка строк
        for (int row = 0; row < 3; row++) {
            if (board[row][0] != 0 && board[row][0] == board[row][1] && board[row][0] == board[row][2]) {
                endGame(board[row][0] == 'X' ? GameState.X_WON : GameState.O_WON);
                return;
            }
        }

        // Проверка столбцов
        for (int col = 0; col < 3; col++) {
            if (board[0][col] != 0 && board[0][col] == board[1][col] && board[0][col] == board[2][col]) {
                endGame(board[0][col] == 'X' ? GameState.X_WON : GameState.O_WON);
                return;
            }
        }

        // Проверка диагоналей
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            endGame(board[0][0] == 'X' ? GameState.X_WON : GameState.O_WON);
            return;
        }

        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            endGame(board[0][2] == 'X' ? GameState.X_WON : GameState.O_WON);
            return;
        }

        // Проверка на ничью
        boolean isDraw = true;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == 0) {
                    isDraw = false;
                    break;
                }
            }
        }

        if (isDraw) {
            endGame(GameState.DRAW);
        }
    }

    private void endGame(GameState state) {
        gameState = state;
        switch (state) {
            case X_WON:
                statusLabel.setText("X won");
                break;
            case O_WON:
                statusLabel.setText("O won");
                break;
            case DRAW:
                statusLabel.setText("Draw");
                break;
        }
        restartButton.setVisible(true);
        menuButton.setVisible(true);
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
