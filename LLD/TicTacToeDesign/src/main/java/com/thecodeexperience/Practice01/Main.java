package com.thecodeexperience.Practice01;

public class Main {
    public static void main(String[] args) {
        TicTacToeGame game  = new TicTacToeGame();
        game.initializeGame();
        System.out.println("Winner is : "+game.startGame());
    }
}
