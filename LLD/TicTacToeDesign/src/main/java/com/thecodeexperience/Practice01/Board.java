package com.thecodeexperience.Practice01;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Board {
    private Piece[][] board;
    private int size;

    public Board(int size){
        this.board = new Piece[size][size];
        this.size = size;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void printBoard() {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != null) {
                    System.out.print(board[i][j].getPieceType().name() + "   ");
                } else {
                    System.out.print("    ");

                }
                System.out.print(" | ");
            }
            System.out.println();
        }
    }


    public List<Pair<Integer,Integer>> getFreeCells(){
        List<Pair<Integer,Integer>> list = new ArrayList<>();
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                if(Objects.isNull(board[i][j])) list.add(new Pair<>(i,j));
            }
        }
        return list;
    }

    public boolean addPiece(Player player, int positionX, int positionY){
        if(Objects.nonNull(board[positionX][positionY])) return false;
        board[positionX][positionY]=player.getPiece();
        return true;
    }

    public boolean isThereWinner(int row, int column, Piece piece){
        boolean rowMatch = true;
        boolean columnMatch = true;
        boolean diagonalMatch = true;
        boolean antiDiagonalMatch = true;

        for(int i=0;i<size;i++){
            if(board[row][i]==null || !board[row][i].equals(piece)){
                rowMatch=false;
                break;
            }
        }

        for(int i=0;i<size;i++){
            if(board[i][column]==null || !board[i][column].equals(piece)){
                columnMatch=false;
                break;
            }
        }

        for(int i=0,j=0;i<size;i++,j++){
            if(board[i][j]==null || !board[i][j].equals(piece)){
                diagonalMatch=false;
                break;
            }
        }

        for(int i=0,j=size-1;i<size;i++,j--){
            if(board[i][j]==null || !board[i][j].equals(piece)){
                antiDiagonalMatch=false;
                break;
            }
        }

        return rowMatch || columnMatch || diagonalMatch || antiDiagonalMatch;
    }
}
