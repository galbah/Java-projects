import java.util.Random;

public class WhateverPlayer implements Player{
    /** this class controls the whatever BOT Player moves in game
     * this bot put marks on board randomly
     */

    Random random = new Random();

    @Override
    public void playTurn(Board board, Mark mark) {
        boolean invalidCoords = true;
        while(invalidCoords) {
            int row = random.nextInt(board.getSize());
            int col = random.nextInt(board.getSize());
            if (board.putMark(mark, row, col)) {
                invalidCoords = false;
            }
        }
    }
}
