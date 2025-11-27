package com.thecodeexperience.Practice01;


abstract class Piece {
    private PieceType pieceType;

    public Piece(PieceType pieceType){
        this.pieceType = pieceType;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }
}
