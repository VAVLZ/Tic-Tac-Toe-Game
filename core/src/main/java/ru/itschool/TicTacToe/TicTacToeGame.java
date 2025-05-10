package ru.itschool.TicTacToe;

import com.badlogic.gdx.Game;

public class TicTacToeGame extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
