package com.thecodeexperience.Practice01;

import java.util.*;

public class TicTacToeGame {
    Deque<Player> players;
    Board gameBoard;

    public void initializeGame(){
        PieceX pieceX = new PieceX();
        Player player1 = new Player("Player01",pieceX);

        PieceO pieceO = new PieceO();
        Player player2 = new Player("Player02",pieceO);

        players = new LinkedList<>();
        players.addAll(List.of(player1,player2));
        gameBoard = new Board(3);

    }

    public String startGame() {
        boolean isGameEnded = false;
        while(!isGameEnded){
            Player playerTurn = players.pollFirst();
            gameBoard.printBoard();
            List<Pair<Integer,Integer>> freeCells = gameBoard.getFreeCells();
            if(freeCells.isEmpty()){
                isGameEnded = true;
                continue;
            }

            System.out.print("Player:" + playerTurn.getName() + " Enter row,column: ");
            Scanner inputScanner = new Scanner(System.in);
            String s = inputScanner.nextLine();
            String[] values = s.split(",");
            int inputRow = Integer.parseInt(values[0]);
            int inputColumn = Integer.parseInt(values[1]);

            boolean piecePlacedSuccessfully = gameBoard.addPiece(playerTurn,inputRow,inputColumn);
            if(!piecePlacedSuccessfully){
                System.out.println("Position already filled, try again");
                players.addFirst(playerTurn);
                continue;
            }
            System.out.println("Piece Placed Successfully");
            boolean winner  = gameBoard.isThereWinner(inputRow, inputColumn, playerTurn.getPiece());
            if(winner){
                return playerTurn.name;
            }
            players.addLast(playerTurn);
        }
        return "Tie";
    }




}
